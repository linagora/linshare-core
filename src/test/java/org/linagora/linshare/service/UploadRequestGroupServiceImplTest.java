/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml", "classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml", "classpath:springContext-rac.xml", "classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml", "classpath:springContext-test.xml", })
public class UploadRequestGroupServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestGroupServiceImplTest.class);

	private LinShareWiser wiser;

	public UploadRequestGroupServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService service;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private UploadRequest ure = new UploadRequest();

	private LoadingServiceTestDatas datas;

	private UploadRequest uploadRequest;

	private User john;

	private Contact yoda;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		// UPLOAD REQUEST CREATE
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100);
		ure.setMaxFileCount(new Integer(3));
		ure.setMaxFileSize((long) 50);
		ure.setStatus(UploadRequestStatus.STATUS_CREATED);
		ure.setExpiryDate(new Date());
		ure.setSecured(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale("en");
		ure.setActivationDate(new Date());
		List<UploadRequest> eList = service.createRequest(john, john, ure, Lists.newArrayList(yoda),
				"This is a subject", "This is a body", false);
		ure = eList.get(0);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		service.deleteRequest(john, john, ure.getUuid());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequestGroup> groups = service.findAllGroupRequest(john, john, null);
		Assert.assertNotNull(groups.get(0));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findFiltred() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		ure.setActivationDate(tomorrow);
		service.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		List<UploadRequestGroup> groups = service.findAllGroupRequest(john, john, Lists.newArrayList("STATUS_ENABLED"));
		Assert.assertEquals(service.findAllGroupRequest(john, john, null).size() - 1, groups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
