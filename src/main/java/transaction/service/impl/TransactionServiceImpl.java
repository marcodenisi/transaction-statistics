package transaction.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.TransactionService;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    // constants
    private static final int TIME_FRAME = 60000;

    // locks
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    // instance variables
    private final Transaction[] transactions = new Transaction[TIME_FRAME];
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private double avg = 0.;
    private double sum = 0;
    private int count = 0;

    @Override
    public boolean addTransaction(Transaction transaction) {
        logger.debug("Adding transaction: {}", transaction);

        long now = Instant.now().toEpochMilli();
        if (transaction.getTimestamp() < now - TIME_FRAME || transaction.getTimestamp() > now) {
            logger.debug("Transaction too old or in the future, rejecting it. Timestamp: {} - now: {}",
                    transaction.getTimestamp(), now);
            return false;
        }

        try {
            writeLock.lock();
            int position = (int) (transaction.getTimestamp() % TIME_FRAME);
            transactions[position] = transaction;
            count++;
            sum += transaction.getAmount();
            avg = sum / count;

            if (max < transaction.getAmount()) {
                max = transaction.getAmount();
            }
            if (min > transaction.getAmount()) {
                min = transaction.getAmount();
            }

        } finally {
            writeLock.unlock();
        }

        return true;
    }

    @Override
    public StatisticsBean getStatistics() {
        try {
            readLock.lock();
            return new StatisticsBean.StatisticsBeanBuilder()
                    .withAvg(avg)
                    .withCount(count)
                    .withMax(max)
                    .withMin(min)
                    .withSum(sum)
                    .build();
        } finally {
            readLock.unlock();
        }
    }

    @Scheduled(fixedRate = 1L)
    void popUpTransition() {
        try {
            writeLock.lock();

            long now = Instant.now().toEpochMilli();
            int position = (int) ((now - 1) % TIME_FRAME);
            Transaction oldTransaction = transactions[position];

            count--;
            sum -= oldTransaction.getAmount();
            avg = sum / count;
            transactions[position] = null;

            double actualMin = Double.MAX_VALUE;
            double actualMax = Double.MIN_VALUE;
            for (int i = 0; i < TIME_FRAME; i++) {
                if (transactions[i] == null) {
                    continue;
                }

                if (actualMax < transactions[i].getAmount()) {
                    actualMax = transactions[i].getAmount();
                }
                if (actualMin > transactions[i].getAmount()) {
                    actualMin = transactions[i].getAmount();
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    Transaction[] getTransactions() {
        return transactions;
    }
}
