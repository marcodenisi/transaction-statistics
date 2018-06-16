package transaction.controller;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import transaction.model.Transaction;

import java.time.Instant;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
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
                .withAmount(10.)
                .build();

        // when
        ResponseEntity<Void> voidResponseEntity = controller.addTransaction(t);

        // then
        Predicate<ResponseEntity> hasNoContent = e -> HttpStatus.CREATED.equals(e.getStatusCode());
        assertThat(voidResponseEntity).isNotNull().has(new Condition<ResponseEntity>(
                hasNoContent, "201 Created", voidResponseEntity
        ));
    }

}