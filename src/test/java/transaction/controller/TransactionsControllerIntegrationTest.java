package transaction.controller;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionsControllerIntegrationTest {
    @Autowired private TransactionsController controller;

    @Test
    public void shouldNotInsertTransaction() {
        // given
        Transaction t = new Transaction.TransactionBuilder()
                .withTimestamp(0L)
                .build();

        // when
        ResponseEntity<Void> voidResponseEntity = controller.addTransaction(t);

        // then
        Predicate<ResponseEntity> hasNoContent = e -> HttpStatus.NO_CONTENT.equals(e.getStatusCode());
        assertThat(voidResponseEntity).isNotNull().has(new Condition<ResponseEntity>(
                hasNoContent, "204 No Content", voidResponseEntity
        ));
    }

    @Test
    public void shouldInsertTransaction() {
        // given
        Transaction t = new Transaction.TransactionBuilder()
                .withTimestamp(Instant.now().toEpochMilli())
                .withAmount(new BigDecimal("10."))
                .build();

        // when
        ResponseEntity<Void> voidResponseEntity = controller.addTransaction(t);

        // then
        Predicate<ResponseEntity> hasNoContent = e -> HttpStatus.CREATED.equals(e.getStatusCode());
        assertThat(voidResponseEntity).isNotNull().has(new Condition<ResponseEntity>(
                hasNoContent, "201 Created", voidResponseEntity
        ));
    }

    @Test
    public void shouldGetCorrectStatistics() throws InterruptedException {
        // given
        long now = Instant.now().toEpochMilli();
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal max = BigDecimal.valueOf(Double.MIN_VALUE);

        Random random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            BigDecimal rnd = BigDecimal.valueOf(random.nextDouble());
            controller.addTransaction(new Transaction.TransactionBuilder()
                    .withTimestamp(now)
                    .withAmount(rnd)
                    .build());
            sum = sum.add(rnd);
            min = min.min(rnd);
            max = max.max(rnd);
        }

        Thread.sleep(2000L);

        // when
        StatisticsBean overallStatistics = controller.getStatistics();

        // then
        assertThat(overallStatistics).isNotNull();
        assertThat(overallStatistics.getCount()).isEqualTo(10);
        assertThat(overallStatistics.getSum()).isEqualTo(sum);
        assertThat(overallStatistics.getMin()).isEqualTo(min);
        assertThat(overallStatistics.getMax()).isEqualTo(max);
        assertThat(overallStatistics.getAvg()).isEqualTo(sum.divide(BigDecimal.valueOf(10), RoundingMode.HALF_UP));
    }

}