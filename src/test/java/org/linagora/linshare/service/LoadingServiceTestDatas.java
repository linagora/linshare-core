/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.service;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadingServiceTestDatas {
	
	protected Logger logger = LoggerFactory.getLogger(LoadingServiceTestDatas.class);
	
	private UserRepository<User> userRepository;
	
	public static String rootDomainName = "TEST_Domain-0";
	public static String topDomainName = "TEST_Domain-0-1";
	public static String topDomainName2 = "TEST_Domain-0-2";
	public static String subDomainName1 = "TEST_Domain-0-1-1";
	public static String subDomainName2 = "TEST_Domain-0-1-2";
	public static String guestDomainName1 = "guestDomainName1";
	
	public static String sqlRootDomain = "LinShareRootDomain";
	public static String sqlDomain = "MyDomain";
	public static String sqlSubDomain = "MySubDomain";
	public static String sqlGuestDomain = "GuestDomain";
	
	
	public static String TEST_TIME_STAMPING="TEST_TIME_STAMPING";
	public static String FILESIZE_MAX="TEST_FILESIZE_MAX";
	public static String QUOTA_USER="TEST_QUOTA_USER";
	public static String QUOTA_GLOBAL="TEST_QUOTA_GLOBAL";
	public static String GUEST="GUEST";
	public static String FUNC1="TEST_FUNC1";
	public static String FUNC2="TEST_FUNC2";
	public static String FUNC3="TEST_FUNC3";
	public static String FUNC4="TEST_FUNC4";
	public static String FUNC5="TEST_FUNC5";
	
	private static String domainePolicyName0 = "TestAccessPolicy0";
	
	public static int TOTAL_COUNT_FUNC=10;
	public static String timeStampingUrl = "http://server/service";

	private User user1;  /* John Doe */
	private User user2;	 /* Jane Smith */
	private User user3;	 /* Foo Bar */
	private Account root;
	

	public LoadingServiceTestDatas(
			UserRepository<User> userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public  void loadUsers() throws BusinessException {
		root = userRepository.findByMailAndDomain(rootDomainName, "root@localhost.localdomain@test");
		user1 = userRepository.findByMail("user1@linshare.org");
		user2 = userRepository.findByMail("user2@linshare.org");
		user3 = userRepository.findByMail("user3@linshare.org");
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
