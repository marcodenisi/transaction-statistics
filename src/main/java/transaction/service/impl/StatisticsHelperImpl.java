package transaction.service.impl;

import org.springframework.stereotype.Service;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.StatisticsHelper;

@Service
public class StatisticsHelperImpl implements StatisticsHelper {
    @Override
    public StatisticsBean initStatistics() {
        return new StatisticsBean.StatisticsBeanBuilder()
                .withSum(0.0)
                .withAvg(0.0)
                .withCount(0)
                .withMax(0.0)
                .withMin(0.0)
                .build();
    }

    @Override
    public StatisticsBean updateStatistics(StatisticsBean statistics, Transaction currentTransaction) {
        double newSum = statistics.getSum() + currentTransaction.getAmount();
        int newCount = statistics.getCount() + 1;
        return new StatisticsBean.StatisticsBeanBuilder()
                .withMax(Math.max(statistics.getMax(), currentTransaction.getAmount()))
                .withMin(Math.min(statistics.getMin(), currentTransaction.getAmount()))
                .withCount(newCount)
                .withSum(newSum)
                .withAvg(newSum / newCount)
                .build();
    }
}
