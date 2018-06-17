package transaction.model;

import java.math.BigDecimal;

public class Transaction {
    private BigDecimal amount;
    private long timestamp;

    public Transaction() {
    }

    private Transaction(TransactionBuilder builder) {
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class TransactionBuilder {
        private BigDecimal amount;
        private long timestamp;

        public TransactionBuilder withAmount(BigDecimal amount) {
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
