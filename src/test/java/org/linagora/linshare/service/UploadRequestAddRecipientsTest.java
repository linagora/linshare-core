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
		"classpath:springContext-mongo.xml",
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

	private UploadRequest uploadRequest;

	private User john;

	private User jane;

	public UploadRequestAddRecipientsTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
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
	public void addNewRecipientInIndividualMode() throws BusinessException {
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
	public void addNewRecipientInCollectivedMode() throws BusinessException {
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