package transaction.model;

public class Transaction {
    private double amount;
    private long timestamp;

    public Transaction() {
    }

    private Transaction(TransactionBuilder builder) {
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class TransactionBuilder {
        private double amount;
        private long timestamp;

        public TransactionBuilder withAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
