package transaction.service.impl;

import org.junit.Test;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

import java.time.Instant;

import static org.junit.Assert.*;

public class TransactionServiceImplUnitTest {

    TransactionServiceImpl service = new TransactionServiceImpl();

    @Test
    public void shouldNotInsert() {
        // given
        Transaction tooOldTransaction = new Transaction.TransactionBuilder()
                .withAmount(10.0)
                .withTimestamp(0L)
                .build();

        // when
        boolean result = service.addTransaction(tooOldTransaction);

        // then
        assertFalse(result);
    }

    @Test
    public void shouldInsert() {
        // given
        double amount = 5.5;
        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(amount)
                .withTimestamp(Instant.now().toEpochMilli())
                .build();

        // when
        boolean result = service.addTransaction(transaction);

        // then
        StatisticsBean statistics = service.getStatistics();
        assertTrue(result);
        assertNotNull(statistics);
        assertEquals(amount, statistics.getAvg(), 0.0);
        assertEquals(amount, statistics.getSum(), 0.0);
        assertEquals(amount, statistics.getMax(), 0.0);
        assertEquals(amount, statistics.getMin(), 0.0);
        assertEquals(1, statistics.getCount());
    }

}