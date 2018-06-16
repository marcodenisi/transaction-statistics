package transaction.service.impl;

import org.springframework.stereotype.Service;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.StatisticsHelper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

@Service
public class StatisticsHelperImpl implements StatisticsHelper {

    private BinaryOperator<StatisticsBean> updateStatisticsOperator = (s1, s2) -> {
        double newSum = s1.getSum() + s2.getSum();
        int newCount = s1.getCount() + s2.getCount();
        return new StatisticsBean.StatisticsBeanBuilder()
                .withSum(newSum)
                .withCount(newCount)
                .withAvg(newSum / newCount)
                .withMin(Math.min(s1.getMin(), s2.getMin()))
                .withMax(Math.max(s1.getMax(), s2.getMax()))
                .build();
    };

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
        if (currentTransaction == null) {
            return statistics;
        }

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

    @Override
    public StatisticsBean updateOverallStatistics(List<StatisticsBean> statisticsBeanList) {
        return Optional.ofNullable(statisticsBeanList)
                .orElse(Collections.emptyList())
                .stream()
                .reduce(updateStatisticsOperator)
                .orElse(initStatistics());
    }
}
