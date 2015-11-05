package org.linagora.linshare.core.business.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;

public interface UserMonthlyStatBusinessService {

	UserMonthlyStat create(User user, Date beginDate, Date endDate);

	List<UserMonthlyStat> findBetweenTwoDates(User user, Date beginDate, Date endDate);

	void deleteBeforeDate(Date date);

	List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate);
}
