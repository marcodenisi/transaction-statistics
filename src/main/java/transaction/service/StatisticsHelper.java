package transaction.service;

import transaction.bean.StatisticsBean;
import transaction.model.Transaction;

import java.util.List;

public interface StatisticsHelper {
    StatisticsBean initStatistics();
    StatisticsBean updateStatistics(StatisticsBean statistics, Transaction currentTransaction);
    StatisticsBean updateOverallStatistics(List<StatisticsBean> statisticsBeanList);
}
