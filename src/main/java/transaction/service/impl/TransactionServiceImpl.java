package transaction.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.StatisticsHelper;
import transaction.service.TransactionService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    // constants
    private static final int LENGTH = 61;
    private static final int MINUTE = 60;

    // locks
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    // instance variables
    private final StatisticsBean[] statistics = new StatisticsBean[LENGTH];
    private StatisticsBean overallStatistics;

    @Autowired private StatisticsHelper statisticsHelper;

    @Override
    public boolean addTransaction(Transaction transaction) {
        logger.debug("Adding transaction: {}", transaction);

        long now = Instant.now().toEpochMilli();
        if (transaction.getTimestamp() < now - (MINUTE * 1000) || transaction.getTimestamp() > now) {
            logger.debug("Transaction too old or in the future, rejecting it. Timestamp: {} - now: {}",
                    transaction.getTimestamp(), now);
            return false;
        }

        try {
            writeLock.lock();

            int position = (int) ((transaction.getTimestamp() / 1000) % LENGTH);
            StatisticsBean statisticsBean = Optional.ofNullable(statistics[position])
                    .orElse(statisticsHelper.initStatistics());

            statistics[position] = statisticsHelper.updateStatistics(statisticsBean, transaction);
        } finally {
            writeLock.unlock();
        }

        return true;
    }

    @Override
    public StatisticsBean getStatistics() {
        try {
            readLock.lock();
            return overallStatistics;
        } finally {
            readLock.unlock();
        }
    }

    @Scheduled(fixedDelay = 100L)
    void updateOverallStatistics() {
        long now = Instant.now().toEpochMilli();

        int endPosition = (int) ((now / 1000) % LENGTH);
        int startPosition = endPosition < LENGTH - 2 ? endPosition + 2 : 0;

        try {
            writeLock.lock();

            List<StatisticsBean> lastMinuteStats = new ArrayList<>();

            boolean hasMore = true;
            while (hasMore) {
                Optional.ofNullable(statistics[startPosition]).ifPresent(lastMinuteStats::add);

                if (startPosition == endPosition) {
                    hasMore = false;
                }

                if (startPosition == LENGTH - 1) {
                    startPosition = 0;
                } else {
                    startPosition++;
                }
            }

            overallStatistics = statisticsHelper.updateOverallStatistics(lastMinuteStats);

            // clear old statistics
            if (startPosition >= LENGTH - 1) {
                startPosition = -1;
            }
            statistics[startPosition + 1] = statisticsHelper.initStatistics();

        } finally {
            writeLock.unlock();
        }

    }
}
