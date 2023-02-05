/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.service;

import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadingServiceTestDatas {
	
	protected Logger logger = LoggerFactory.getLogger(LoadingServiceTestDatas.class);
	
	private UserRepository<User> userRepository;

	public static String sqlRootDomain = "LinShareRootDomain";
	public static String sqlDomain = "MyDomain";
	public static String sqlSubDomain = "MySubDomain";
	public static String sqlGuestDomain = "GuestDomain";

	public static int TOTAL_COUNT_FUNC=10;
	public static String timeStampingUrl = "http://server/service";

	private User user1;  /* John Doe */
	private User user2;	 /* Jane Smith */
	private User user3;	 /* Foo Bar */
	private Account root;
	private Account system;

	public LoadingServiceTestDatas(
			UserRepository<User> userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public  void loadUsers() throws BusinessException {
		root = userRepository.findByMail(LinShareTestConstants.ROOT_ACCOUNT);
		system = userRepository.getBatchSystemAccount();
		user1 = userRepository.findByMail("user1@linshare.org"); // John Do
		user2 = userRepository.findByMail("user2@linshare.org"); // Jane Smith
		user3 = userRepository.findByMail("user3@linshare.org"); // Foo Bar
	}

	public User getUser1() {
		return user1;
	}

	public User getUser2() {
		return user2;
	}

	public User getUser3() {
		return user3;
	}

	public Account getRoot() {
		return root;
	}

	public Account getSystem() {
		return system;
	}

	public int getAvailableFunctionalitiesForTopDomain2() {
		// three have their activation policy dedicated to the root domain (forbidden or mandatory): -3 : (FUNC1,FUNC2,FUNC3)
		return TOTAL_COUNT_FUNC -3 ;
	}
	
	public int getAlterableFunctionalitiesForTopDomain2() {
		// two have their activation policy set to forbidden , so they can't be active nor configurable : -2 : (FUNC1,FUNC2)
		// two have their configuration policy set to system : -2 (FUNC4,FUNC5)
		return TOTAL_COUNT_FUNC -2 -2;
	}
	
	public int getEditableFunctionalitiesForTopDomain2() {
		// two are dedicated to the system : -1 : (QUOTA_GLOBAL)
		// two have their activation policy set to forbidden , so they can't be active nor configurable nor usable : -2 : (FUNC1,FUNC2)
		// one have its configuration policy status set to false : -1 (FUNC5)
		return TOTAL_COUNT_FUNC -1 -2 -1 ;
	}
	
}
