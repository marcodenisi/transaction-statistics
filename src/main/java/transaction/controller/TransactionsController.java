package transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import transaction.service.TransactionService;

@Controller
public class TransactionsController {

    @Autowired private TransactionService transactionService;

}
