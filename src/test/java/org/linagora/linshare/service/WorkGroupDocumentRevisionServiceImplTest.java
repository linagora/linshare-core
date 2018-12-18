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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.WorkGroupVersioning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Sql({"/import-tests-default-domain-quotas.sql",
	"/import-tests-domain-quota-updates.sql",
	"/import-tests-jwt-long-time-functionnality.sql"})
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml" })
public class WorkGroupDocumentRevisionServiceImplTest {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	protected WorkGroupNodeService workGroupNodeService;

	@Autowired
	protected WorkGroupDocumentService workGroupDocumentService;
	
	@Autowired
	protected WorkGroupDocumentRevisionService workGroupDocumentRevisionService;

	@Autowired
	protected ThreadService threadService;

	@Autowired
	SharedSpaceNodeService sharedSpaceNodeService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private InitMongoService initMongoService;

	LoadingServiceTestDatas datas;

	private User john;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		initMongoService.init();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createNewDocumentTest() throws IOException {
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		SharedSpaceNode ssn = sharedSpaceNodeService.create(john, john, node);
		WorkGroup workGroup = threadService.find(john, john, ssn.getUuid());
		WorkGroupNode folder = workGroupNodeService.getRootFolder(john, john, workGroup);
		InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);

		WorkGroupNode document = workGroupDocumentService.create(john, john, workGroup, tempFile1, tempFile1.getName(), folder);
		WorkGroupNode revision = workGroupDocumentRevisionService.create(john, john, workGroup, tempFile1, tempFile1.getName(), document);

		assertEquals(tempFile1.getName(), document.getName());
		assertEquals(folder.getUuid(), document.getParent());
		assertEquals(document.getUuid(), revision.getParent());
		assertEquals(document.getName(), revision.getName());
	}

	@Test
	public void findAllTest() throws IOException {
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		node.setWorkGroupVersioning(new WorkGroupVersioning(true, false, null, null));
		SharedSpaceNode ssn = sharedSpaceNodeService.create(john, john, node);
		WorkGroup workGroup = threadService.find(john, john, ssn.getUuid());
		WorkGroupNode folder = workGroupNodeService.getRootFolder(john, john, workGroup);

		InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare.properties.sample");
		InputStream stream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		File tempFile3 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream3, tempFile3);

		WorkGroupNode document = workGroupDocumentService.create(john, john, workGroup, tempFile1, tempFile1.getName(), folder);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile1, tempFile1.getName(), document);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile2, tempFile2.getName(), document);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile3, tempFile3.getName(), document);

		WorkGroupNode createdDocument = workGroupDocumentService.find(john, john, workGroup, document.getUuid());
		List<WorkGroupNode> createdRevisions = workGroupDocumentRevisionService.findAll(john, workGroup, createdDocument.getUuid());

		assertNotNull(createdDocument);
		// We expect the number of revision - 1  because the most recent one is not returned
		assertEquals(2, createdRevisions.size());
	}

	@Test
	public void restoreRevisionTest() throws IOException {
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		node.setWorkGroupVersioning(new WorkGroupVersioning(true, false, null, null));
		SharedSpaceNode ssn = sharedSpaceNodeService.create(john, john, node);
		WorkGroup workGroup = threadService.find(john, john, ssn.getUuid());
		WorkGroupNode folder = workGroupNodeService.getRootFolder(john, john, workGroup);

		InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare.properties.sample");
		InputStream stream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		File tempFile3 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream3, tempFile3);

		WorkGroupNode document = workGroupDocumentService.create(john, john, workGroup, tempFile1, tempFile1.getName(), folder);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile1, tempFile1.getName(), document);
		WorkGroupNode revision2 = workGroupDocumentRevisionService.create(john, john, workGroup, tempFile2, tempFile2.getName(), document);
		WorkGroupNode revision3 = workGroupDocumentRevisionService.create(john, john, workGroup, tempFile3, tempFile3.getName(), document);

		assertEquals(((WorkGroupDocument) document).getSize(), ((WorkGroupDocumentRevision) revision3).getSize());

		workGroupDocumentRevisionService.restore(john, john, workGroup, revision2.getUuid());
		WorkGroupNode updatedDocument = workGroupDocumentService.find(john, john, workGroup, document.getUuid());

		assertEquals(((WorkGroupDocument) updatedDocument).getSize(), ((WorkGroupDocumentRevision) revision2).getSize());
	}

	@Test
	public void deleteRevisionTest() throws IOException {
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		node.setWorkGroupVersioning(new WorkGroupVersioning(true, false, null, null));
		SharedSpaceNode ssn = sharedSpaceNodeService.create(john, john, node);
		WorkGroup workGroup = threadService.find(john, john, ssn.getUuid());
		WorkGroupNode folder = workGroupNodeService.getRootFolder(john, john, workGroup);

		InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare.properties.sample");
		InputStream stream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		File tempFile3 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream3, tempFile3);

		WorkGroupNode document = workGroupDocumentService.create(john, john, workGroup, tempFile1, tempFile1.getName(), folder);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile1, tempFile1.getName(), document);
		WorkGroupNode revision2 = workGroupDocumentRevisionService.create(john, john, workGroup, tempFile2, tempFile2.getName(), document);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile3, tempFile3.getName(), document);

		WorkGroupNode createdDocument = workGroupDocumentService.find(john, john, workGroup, document.getUuid());
		List<WorkGroupNode> createdRevisions = workGroupDocumentRevisionService.findAll(john, workGroup, createdDocument.getUuid());

		// We expect the number of revision - 1  because the most recent one is not returned
		assertEquals(2, createdRevisions.size());

		workGroupDocumentRevisionService.delete(john, john, workGroup, revision2);

		List<WorkGroupNode> newRevisionList = workGroupDocumentRevisionService.findAll(john, workGroup, createdDocument.getUuid());
		assertEquals(1, newRevisionList.size());
	}

	@Test
	public void deleteAllTest() throws IOException {
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		node.setWorkGroupVersioning(new WorkGroupVersioning(true, false, null, null));
		SharedSpaceNode ssn = sharedSpaceNodeService.create(john, john, node);
		WorkGroup workGroup = threadService.find(john, john, ssn.getUuid());
		WorkGroupNode folder = workGroupNodeService.getRootFolder(john, john, workGroup);

		InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare.properties.sample");
		InputStream stream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		File tempFile3 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream3, tempFile3);

		WorkGroupNode document = workGroupDocumentService.create(john, john, workGroup, tempFile1, tempFile1.getName(), folder);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile1, tempFile1.getName(), document);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile2, tempFile2.getName(), document);
		workGroupDocumentRevisionService.create(john, john, workGroup, tempFile3, tempFile3.getName(), document);

		WorkGroupNode createdDocument = workGroupDocumentService.find(john, john, workGroup, document.getUuid());
		List<WorkGroupNode> createdRevisions = workGroupDocumentRevisionService.findAll(john, workGroup, createdDocument.getUuid());

		assertNotNull(createdDocument);
		// We expect the number of revision - 1  because the most recent one is not returned
		assertEquals(2, createdRevisions.size());

		workGroupDocumentRevisionService.deleteAll(john, john, workGroup, document);
		workGroupDocumentService.delete(john, john, workGroup, document);

		try{
			workGroupDocumentService.find(john, john, workGroup, document.getUuid());
			assertEquals(true, false); // We voluntary fail the test if any BusinessException is thrown
		} catch(BusinessException be) {
			// Test passes only if a BusinessException is thrown
			assertTrue(true);
		}
		List<WorkGroupNode> expectedEmptyRevisions = workGroupDocumentRevisionService.findAll(john, workGroup, createdDocument.getUuid());
		assertEquals(0, expectedEmptyRevisions.size());
	}

}