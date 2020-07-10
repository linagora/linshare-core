/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-upload-request-add-recipients.sql" })
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadRequestAddRecipientsTest {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestAddRecipientsTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private LoadingServiceTestDatas datas;

	private UploadRequest uploadRequest;

	private User john;

	private User jane;

	public UploadRequestAddRecipientsTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		john.setDomain(subDomain);
		jane.setDomain(subDomain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void addNewRecipientInRestrictedMode() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequestGroup> uploadRequestGroups = uploadRequestGroupService.findAll(john, john, null);
		Assertions.assertEquals(uploadRequestGroups.size(), 3);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroups.get(0);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		List<ContactDto> recipients = Lists.newArrayList();
		recipients.add(addToList("amy@mail.test"));
		recipients.add(addToList("peter@mail.test"));
		uploadRequestGroup = uploadRequestGroupService.addNewRecipients(john, john, uploadRequestGroup, recipients);
		Assertions.assertEquals(uploadRequestGroup.getUploadRequests().size(), 3);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void addNewRecipientInGroupedMode() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequestGroup> uploadRequestGroups = uploadRequestGroupService.findAll(jane, jane, null);
		Assertions.assertEquals(uploadRequestGroups.size(), 1);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroups.get(0);
		Assertions.assertEquals(uploadRequestGroup.getUploadRequests().size(), 1);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Set<UploadRequestUrl> uploadRequestUrls = uploadRequest.getUploadRequestURLs();
		Assertions.assertEquals(uploadRequestUrls.size(), 0);
		List<ContactDto> recipients = Lists.newArrayList();
		recipients.add(addToList("amy@mail.test"));
		recipients.add(addToList("peter@mail.test"));
		uploadRequestGroup = uploadRequestGroupService.addNewRecipients(jane, jane, uploadRequestGroup, recipients);
		Set<UploadRequestUrl> requestUrls = uploadRequest.getUploadRequestURLs();
		Assertions.assertEquals(uploadRequestGroup.getUploadRequests().size(), 1);
		Assertions.assertEquals(requestUrls.size(), 2);
	}

	public ContactDto addToList(String mail) {
		ContactDto contactDto = new ContactDto(null, null, mail);
		return contactDto;
	}
}