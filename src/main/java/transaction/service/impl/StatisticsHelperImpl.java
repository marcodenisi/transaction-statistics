package transaction.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.StatisticsHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.BinaryOperator;

@Service
public class StatisticsHelperImpl implements StatisticsHelper {

    private BinaryOperator<StatisticsBean> updateStatisticsOperator = (s1, s2) -> {
        BigDecimal newSum = s1.getSum().add(s2.getSum());
        long newCount = s1.getCount() + s2.getCount();
        boolean calcAllStats = newCount != 0;

        return new StatisticsBean.StatisticsBeanBuilder()
                .withSum(newSum)
                .withCount(newCount)
                .withAvg(calcAllStats ? newSum.divide(BigDecimal.valueOf(newCount), RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .withMin(calcAllStats ? s1.getMin().min(s2.getMin()) : BigDecimal.ZERO)
                .withMax(calcAllStats ? s1.getMax().max(s2.getMax()) : BigDecimal.ZERO)
                .build();
    };

    @Override
    public StatisticsBean initStatistics() {
        return new StatisticsBean.StatisticsBeanBuilder()
                .withSum(BigDecimal.ZERO)
                .withAvg(BigDecimal.ZERO)
                .withCount(0L)
                .withMax(BigDecimal.valueOf(Double.MIN_VALUE))
                .withMin(BigDecimal.valueOf(Double.MAX_VALUE))
                .build();
    }

    @Override
    public StatisticsBean updateStatistics(StatisticsBean statistics, Transaction currentTransaction) {
        if (currentTransaction == null) {
            return statistics;
        }

        BigDecimal newSum = statistics.getSum().add(currentTransaction.getAmount());
        long newCount = statistics.getCount() + 1;
        return new StatisticsBean.StatisticsBeanBuilder()
                .withMax(statistics.getMax().max(currentTransaction.getAmount()))
                .withMin(statistics.getMin().min(currentTransaction.getAmount()))
                .withCount(newCount)
                .withSum(newSum)
                .withAvg(newSum.divide(BigDecimal.valueOf(newCount), RoundingMode.HALF_UP))
                .build();
    }

    @Override
    public StatisticsBean updateOverallStatistics(List<StatisticsBean> statisticsBeanList) {
        if (CollectionUtils.isEmpty(statisticsBeanList)) {
            return buildDefaultOverallStatistics();
        }

        return statisticsBeanList.stream().reduce(initStatistics(), updateStatisticsOperator);
    }

    private StatisticsBean buildDefaultOverallStatistics() {
        return new StatisticsBean.StatisticsBeanBuilder()
                .withSum(BigDecimal.ZERO)
                .withCount(0L)
                .withAvg(BigDecimal.ZERO)
                .withMin(BigDecimal.ZERO)
                .withMax(BigDecimal.ZERO)
                .build();
    }
}
