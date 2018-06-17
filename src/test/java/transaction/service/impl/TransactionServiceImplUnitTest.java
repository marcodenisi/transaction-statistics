package transaction.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import transaction.model.Transaction;
import transaction.service.StatisticsHelper;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplUnitTest {

    @InjectMocks TransactionServiceImpl service;
    @Spy StatisticsHelper statisticsHelper = new StatisticsHelperImpl();

    @Test
    public void shouldNotInsert() {
        // given
        Transaction tooOldTransaction = new Transaction.TransactionBuilder()
                .withAmount(new BigDecimal("10."))
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
        BigDecimal amount = new BigDecimal("10.");
        Transaction transaction = new Transaction.TransactionBuilder()
                .withAmount(amount)
                .withTimestamp(Instant.now().toEpochMilli())
                .build();

        // when
        boolean result = service.addTransaction(transaction);

        // then
        assertTrue(result);
    }

}