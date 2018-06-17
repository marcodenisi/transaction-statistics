package transaction.service.impl;

import org.assertj.core.api.Condition;
import org.junit.Test;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticsHelperImplTest {

    StatisticsHelperImpl service = new StatisticsHelperImpl();

    @Test
    public void shouldInitStatistics() {
        // when
        StatisticsBean statisticsBean = service.initStatistics();

        // then
        Predicate<StatisticsBean> zeroesPredicate = s -> s.getCount() == 0 &&
                s.getSum() == 0.0 && s.getAvg() == 0.0 &&
                s.getMax() == Double.MIN_VALUE && s.getMin() == Double.MAX_VALUE;
        assertThat(statisticsBean).isNotNull().has(new Condition<>(zeroesPredicate, null, statisticsBean));
    }

    @Test
    public void shouldNotUpdateStatistics() {
        // given
        StatisticsBean statisticsBean = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(0.0)
                .withAvg(0.0)
                .withCount(0)
                .withMax(0.0)
                .withMin(0.0)
                .build();

        // when
        StatisticsBean result = service.updateStatistics(statisticsBean, null);

        // then
        assertThat(result).isNotNull().isEqualToComparingFieldByField(statisticsBean);
    }

    @Test
    public void shouldUpdateStatistics() {
        // given
        StatisticsBean statisticsBean = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(20.)
                .withAvg(10.0)
                .withCount(2)
                .withMax(11.0)
                .withMin(9.0)
                .build();

        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(13L)
                .build();

        // when
        StatisticsBean result = service.updateStatistics(statisticsBean, transaction);

        // then
        Predicate<StatisticsBean> updatedCondition = s -> s.getMin() == 9. && s.getMax() == 13. &&
                s.getAvg() == 11. && s.getSum() == 33. && s.getCount() == 3;
        assertThat(result).isNotNull().has(new Condition<>(updatedCondition, null, result));
    }

    @Test
    public void shouldUpdateStatisticsWithInitialStatistics() {
        // given
        StatisticsBean statisticsBean = service.initStatistics();

        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(13L)
                .build();

        // when
        StatisticsBean result = service.updateStatistics(statisticsBean, transaction);
        StatisticsBean overallResult = service.updateOverallStatistics(Arrays.asList(result, service.initStatistics()));

        // then
        Predicate<StatisticsBean> updatePredicate = s -> s.getMin() == 13. && s.getMax() == 13. &&
                s.getAvg() == 13. && s.getSum() == 13. && s.getCount() == 1;
        Condition<StatisticsBean> updateCondition = new Condition<>(updatePredicate, null, result);
        assertThat(result).isNotNull().has(updateCondition);
        assertThat(overallResult).isNotNull().has(updateCondition);
    }

    @Test
    public void shouldReturnEmptyStatistics() {
        //given
        StatisticsBean emptyStats = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(0.0)
                .withAvg(0.0)
                .withCount(0)
                .withMax(Double.MIN_VALUE)
                .withMin(Double.MAX_VALUE)
                .build();

        // when
        StatisticsBean result = service.updateOverallStatistics(null);

        // then
        assertThat(result).isNotNull().isEqualToComparingFieldByField(emptyStats);
    }

    @Test
    public void shouldReturnOverallStatisticsWithOneInput() {
        // given
        StatisticsBean s = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(20.0)
                .withAvg(10.0)
                .withCount(2)
                .withMax(15.0)
                .withMin(5.0)
                .build();

        // when
        StatisticsBean result = service.updateOverallStatistics(Collections.singletonList(s));

        // then
        assertThat(result).isNotNull().isEqualToComparingFieldByField(s);
    }

    @Test
    public void shouldReturnOverallStatistics() {
        // given
        StatisticsBean s1 = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(20.0)
                .withAvg(10.0)
                .withCount(2)
                .withMax(15.0)
                .withMin(5.0)
                .build();

        StatisticsBean s2 = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(30.0)
                .withAvg(10.0)
                .withCount(3)
                .withMax(16.0)
                .withMin(1.0)
                .build();

        // when
        StatisticsBean result = service.updateOverallStatistics(Arrays.asList(s1, s2));

        // then
        Predicate<StatisticsBean> updatedCondition = s -> s.getMin() == 1. && s.getMax() == 16. &&
                s.getAvg() == 10. && s.getSum() == 50. && s.getCount() == 5;
        assertThat(result).isNotNull().has(new Condition<>(updatedCondition, null, result));
    }

}