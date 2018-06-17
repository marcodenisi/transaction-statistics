package transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import transaction.bean.StatisticsBean;
import transaction.model.Transaction;
import transaction.service.TransactionService;

@RestController
public class TransactionsController {

    @Autowired private TransactionService transactionService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public StatisticsBean getStatistics() {
        return transactionService.getStatistics();
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Void> addTransaction(@RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.addTransaction(transaction) ?
                HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }
}