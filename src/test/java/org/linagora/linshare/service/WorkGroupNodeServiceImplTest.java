/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2019 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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

import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-default-domain-quotas.sql",
	"/import-tests-domain-quota-updates.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
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
public class WorkGroupNodeServiceImplTest {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	protected WorkGroupNodeService workGroupNodeService;

	@Autowired
	protected WorkGroupDocumentService workGroupDocumentService;

	@Autowired
	protected WorkGroupDocumentRevisionService workGroupDocumentRevisionService;

	@Autowired
	protected WorkGroupFolderService workGroupFolderService;

	@Autowired
	protected ThreadService threadService;

	@Autowired
	SharedSpaceNodeService sharedSpaceNodeService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private InitMongoService initMongoService;

	@Autowired
	private FunctionalityReadOnlyService functionalityService;

	LoadingServiceTestDatas datas;

	private User john;

	private SharedSpaceNode ssnode;

	private WorkGroup workGroup;

	private WorkGroupNode rootFolder;
	
	private WorkGroupNode folder;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		initMongoService.init();
		ssnode = sharedSpaceNodeService.create(john, john,
				new SharedSpaceNode("Workgroup_test", "My parent nodeUuid", NodeType.WORK_GROUP));
		workGroup = threadService.find(john, john, ssnode.getUuid());
		rootFolder = workGroupNodeService.getRootFolder(john, john, workGroup);
		WorkGroupNode wgn = new WorkGroupNode(new AccountMto(john), "MY_FOLDER", rootFolder.getUuid(),
				workGroup.getLsUuid());
		wgn.setNodeType(WorkGroupNodeType.FOLDER);
		folder = workGroupNodeService.create(john, john, workGroup, wgn, false, false);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	/**
	 * On a workgroup, test a sub-folder creation (Create a folder in another).
	 */
	@Test
	public void createFolderTest() {
		WorkGroupNode embeddedFolder = workGroupNodeService.create(john, john, workGroup, folder, false, false);
		assertAll("The folder creation has failed", () -> {
			Assertions.assertNotNull(embeddedFolder, "Folder is null");
			Assertions.assertEquals(embeddedFolder.getUuid(), folder.getUuid(), "expected node is different.");
		});
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void countAndSizeNodeTest() throws IOException {
		// File and streams
		InputStream stream1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		InputStream stream2 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		// store the size of uploaded files
		Long documentsSize = tempFile1.length() + tempFile2.length();
		// upload the first document in the folder "MY_FOLDER" inside the workgroup
		// "Workgoupt_test"
		WorkGroupDocument document1 = (WorkGroupDocument) workGroupNodeService.create(john, john, workGroup, tempFile1,
				tempFile1.getName(), folder.getUuid(), false);
		// activate versioning functionality
		Functionality versioningFunctionality = functionalityService.getWorkGroupFileVersioning(john.getDomain());
		versioningFunctionality.getActivationPolicy().setStatus(true);
		ssnode.setVersioningParameters(new VersioningParameters(true));
		// upload a document with same name on same folder
		// revision will be created
		WorkGroupDocument revision = (WorkGroupDocument) workGroupNodeService.create(john, john, workGroup, tempFile2,
				document1.getName(), folder.getUuid(), false);
		// assertions
		Assertions.assertEquals(revision.getParent(), document1.getUuid(), "The revision parent is wrong");
		Assertions.assertEquals(revision.getNodeType(), WorkGroupNodeType.DOCUMENT_REVISION,
				"The node type is not a revision");
		// recover document node metadata integrating revisions
		NodeMetadataMto folderDetailsWithRev = workGroupNodeService.findMetadata(john, john, workGroup, folder, true);
		// assertions
		Assertions.assertEquals(Long.valueOf(1), folderDetailsWithRev.getCount(), "folder contains more than one document");
		Assertions.assertEquals(documentsSize, folderDetailsWithRev.getStorageSize(),
				"The size should be the sum of the original document and its revision");
		// recover document node metadata without revisions
		NodeMetadataMto folderDetails = workGroupNodeService.findMetadata(john, john, workGroup, folder, false);
		Assertions.assertEquals(document1.getSize(), folderDetails.getSize(),
				"The size should be only the size of the document without the revision");
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			workGroupNodeService.findMetadata(john, john, workGroup, revision, false);
		});
		Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, exception.getErrorCode());
	}

}
