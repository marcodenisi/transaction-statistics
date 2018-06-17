package transaction.bean;

import java.math.BigDecimal;

public class StatisticsBean {

    private BigDecimal max;
    private BigDecimal min;
    private BigDecimal sum;
    private BigDecimal avg;
    private long count;

    public StatisticsBean() {
    }

    private StatisticsBean(StatisticsBeanBuilder builder) {
        this.max = builder.max;
        this.min = builder.min;
        this.sum = builder.sum;
        this.avg = builder.avg;
        this.count = builder.count;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

    public static class StatisticsBeanBuilder {
        private BigDecimal max;
        private BigDecimal min;
        private BigDecimal sum;
        private BigDecimal avg;
        private long count;

        public StatisticsBeanBuilder withMax(BigDecimal max) {
            this.max = max;
            return this;
        }

        public StatisticsBeanBuilder withMin(BigDecimal min) {
            this.min = min;
            return this;
        }

        public StatisticsBeanBuilder withSum(BigDecimal sum) {
            this.sum = sum;
            return this;
        }

        public StatisticsBeanBuilder withAvg(BigDecimal avg) {
            this.avg = avg;
            return this;
        }

        public StatisticsBeanBuilder withCount(long count) {
            this.count = count;
            return this;
        }

        public StatisticsBean build() {
            return new StatisticsBean(this);
        }
    }
}
