package transaction.service;

import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

public interface TransactionService {
    boolean addTransaction(Transaction transaction);
    StatisticsBean getStatistics();
}
