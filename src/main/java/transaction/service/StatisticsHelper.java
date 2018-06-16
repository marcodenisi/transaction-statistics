package transaction.service;

import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

public interface StatisticsHelper {
    StatisticsBean initStatistics();
    StatisticsBean updateStatistics(StatisticsBean statistics, Transaction currentTransaction);
}
