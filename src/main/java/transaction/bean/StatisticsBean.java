package transaction.bean;

public class StatisticsBean {

    private double max;
    private double min;
    private double sum;
    private double avg;
    private int count;

    public StatisticsBean() {
    }

    private StatisticsBean(StatisticsBeanBuilder builder) {
        this.max = builder.max;
        this.min = builder.min;
        this.sum = builder.sum;
        this.avg = builder.avg;
        this.count = builder.count;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        return avg;
    }

    public int getCount() {
        return count;
    }

    public static class StatisticsBeanBuilder {
        private double max;
        private double min;
        private double sum;
        private double avg;
        private int count;

        public StatisticsBeanBuilder withMax(double max) {
            this.max = max;
            return this;
        }

        public StatisticsBeanBuilder withMin(double min) {
            this.min = min;
            return this;
        }

        public StatisticsBeanBuilder withSum(double sum) {
            this.sum = sum;
            return this;
        }

        public StatisticsBeanBuilder withAvg(double avg) {
            this.avg = avg;
            return this;
        }

        public StatisticsBeanBuilder withCount(int count) {
            this.count = count;
            return this;
        }

        public StatisticsBean build() {
            return new StatisticsBean(this);
        }
    }
}
