package transaction.service.impl;

import org.assertj.core.api.Condition;
import org.junit.Test;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

import java.math.BigDecimal;
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
        Predicate<StatisticsBean> zeroesPredicate = s -> s.getCount() == 0L &&
                s.getSum().equals(BigDecimal.ZERO) && s.getAvg().equals(BigDecimal.ZERO) &&
                s.getMax().equals(BigDecimal.valueOf(Double.MIN_VALUE)) && s.getMin().equals(BigDecimal.valueOf(Double.MAX_VALUE));
        assertThat(statisticsBean).isNotNull().has(new Condition<>(zeroesPredicate, null, statisticsBean));
    }

    @Test
    public void shouldNotUpdateStatistics() {
        // given
        StatisticsBean statisticsBean = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(BigDecimal.ZERO)
                .withAvg(BigDecimal.ZERO)
                .withCount(0)
                .withMax(BigDecimal.ZERO)
                .withMin(BigDecimal.ZERO)
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
                .withSum(new BigDecimal("20.0"))
                .withAvg(new BigDecimal("10.0"))
                .withCount(2)
                .withMax(new BigDecimal("11.0"))
                .withMin(new BigDecimal("9.0"))
                .build();

        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(new BigDecimal("13.0"))
                .build();

        // when
        StatisticsBean result = service.updateStatistics(statisticsBean, transaction);

        // then
        Predicate<StatisticsBean> updatedCondition = s -> s.getCount() == 3 &&
                s.getSum().equals(new BigDecimal("33.0")) && s.getAvg().equals(new BigDecimal("11.0")) &&
                s.getMax().equals(new BigDecimal("13.0")) && s.getMin().equals(new BigDecimal("9.0"));
        assertThat(result).isNotNull().has(new Condition<>(updatedCondition, null, result));
    }

    @Test
    public void shouldUpdateStatisticsWithInitialStatistics() {
        // given
        StatisticsBean statisticsBean = service.initStatistics();

        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(new BigDecimal("13.0"))
                .build();

        // when
        StatisticsBean result = service.updateStatistics(statisticsBean, transaction);
        StatisticsBean overallResult = service.updateOverallStatistics(Arrays.asList(result, service.initStatistics()));

        // then
        Predicate<StatisticsBean> updatePredicate = s -> s.getSum().equals(new BigDecimal("13.0")) &&
                s.getAvg().equals(new BigDecimal("13.0")) &&
                s.getMax().equals(new BigDecimal("13.0")) &&
                s.getMin().equals(new BigDecimal("13.0")) &&
                s.getCount() == 1;
        Condition<StatisticsBean> updateCondition = new Condition<>(updatePredicate, null, result);
        assertThat(result).isNotNull().has(updateCondition);
        assertThat(overallResult).isNotNull().has(updateCondition);
    }

    @Test
    public void shouldReturnEmptyStatistics() {
        //given
        StatisticsBean emptyStats = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(BigDecimal.ZERO)
                .withAvg(BigDecimal.ZERO)
                .withCount(0)
                .withMax(BigDecimal.ZERO)
                .withMin(BigDecimal.ZERO)
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
                .withSum(new BigDecimal("20.0"))
                .withAvg(new BigDecimal("10.0"))
                .withCount(2)
                .withMax(new BigDecimal("15.0"))
                .withMin(new BigDecimal("5.0"))
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
                .withSum(new BigDecimal("20.0"))
                .withAvg(new BigDecimal("10.0"))
                .withCount(2)
                .withMax(new BigDecimal("15.0"))
                .withMin(new BigDecimal("5.0"))
                .build();

        StatisticsBean s2 = new StatisticsBean.StatisticsBeanBuilder()
                .withSum(new BigDecimal("30.0"))
                .withAvg(new BigDecimal("10.0"))
                .withCount(3)
                .withMax(new BigDecimal("16.0"))
                .withMin(new BigDecimal("1.0"))
                .build();

        // when
        StatisticsBean result = service.updateOverallStatistics(Arrays.asList(s1, s2));

        // then

        Predicate<StatisticsBean> updatedCondition = s -> s.getSum().equals(new BigDecimal("50.0")) &&
                s.getAvg().equals(new BigDecimal("10.0")) &&
                s.getMax().equals(new BigDecimal("16.0")) &&
                s.getMin().equals(new BigDecimal("1.0")) &&
                s.getCount() == 5;
        assertThat(result).isNotNull().has(new Condition<>(updatedCondition, null, result));
    }

}