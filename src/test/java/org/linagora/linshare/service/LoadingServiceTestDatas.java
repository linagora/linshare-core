/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
