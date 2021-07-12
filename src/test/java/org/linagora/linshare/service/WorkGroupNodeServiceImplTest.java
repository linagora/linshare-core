/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.fields.DocumentKind;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceNodeField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.QuotaService;
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
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-fake-domains.sql",
	"/import-tests-domain-quota-updates.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml" })
@DirtiesContext
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
	private FunctionalityReadOnlyService functionalityService;

	@Autowired
	private QuotaService quotaService;

	private User john;

	private SharedSpaceNode ssnode;

	private WorkGroup workGroup;

	private WorkGroupNode rootFolder;
	
	private WorkGroupNode folder;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		ssnode = sharedSpaceNodeService.create(john, john,
				new SharedSpaceNode("Workgroup_test", NodeType.WORK_GROUP));
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
		sharedSpaceNodeService.delete(john, john, ssnode);
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

	@Test
	public void createFolderSpecialCharactersTest() {
		WorkGroupNode groupNode = new WorkGroupNode(new AccountMto(john),
				"EP_TEST_v233<script>alert(document.cookie)</script>", rootFolder.getUuid(), workGroup.getLsUuid());
		groupNode.setNodeType(WorkGroupNodeType.FOLDER);
		groupNode = workGroupNodeService.create(john, john, workGroup, groupNode, false, false);
		Assertions.assertNotNull(groupNode, "Folder is null");
		Assertions.assertEquals(groupNode.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
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

	@Test
	public void testQuotaDelete() throws IOException {
		InputStream stream1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		File tempFile1 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile1);
		AccountQuota workgroupQuota = quotaService.findByRelatedAccount(workGroup);
		Long quota = quotaService.getRealTimeUsedSpace(john, john, workgroupQuota.getUuid());
		WorkGroupDocument document1 = (WorkGroupDocument) workGroupNodeService.create(john, john, workGroup, tempFile1,
				tempFile1.getName(), folder.getUuid(), false);
		Long quotaAfterCreate = quotaService.getRealTimeUsedSpace(john, john, workgroupQuota.getUuid());
		Assertions.assertEquals(quotaAfterCreate, quota + document1.getSize(),
				"The quota must take in count the file creation");
		workGroupNodeService.delete(john, john, workGroup, document1.getUuid());
		Long newQuota = quotaService.getRealTimeUsedSpace(john, john, workgroupQuota.getUuid());
		Assertions.assertEquals(quota, newQuota, "The quota must be the same after adding then deleting a file");
	}

	@Test
	public void testFindAllDefault() throws IOException {
		createWorkgroupDocument("linshare-default.properties", "linshare-temp0");
		createWorkgroupDocument("linshare-test.properties", "linshare-temp1");
		// disable pagination
		PageContainer<WorkGroupNode> pageContainer = new PageContainer<WorkGroupNode>(null, null);
		// sorting has default values set from webservices. We should set them manually
		// in the service.
		boolean withTree = false;
		PageContainer<WorkGroupNode> nodes = workGroupNodeService.findAll(john, john, workGroup, null, null, withTree,
				false, pageContainer, null, null, null, null, null, null, null, null,
				SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the wg should returns all folders/documents/revisions in all levels
		Assertions.assertEquals(5, nodes.getPageResponse().getContent().size());
		nodes = workGroupNodeService.findAll(john, john, workGroup, folder.getUuid(), null, withTree, false, pageContainer,
				null, null, null, null, null, null, null, null, SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the folder should returns all folders/documents/revisions in all
		// levels
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());
	}

	@Test
	public void testFindAllWithName() throws IOException {
		// Test with search by name pattern | findAll method (used to search workgroup
		// nodes)
		createWorkgroupDocument("linshare-default.properties", "linshare-temp0");
		createWorkgroupDocument("linshare-test.properties", "linshare-tempxyz");
		// disable pagination
		PageContainer<WorkGroupNode> pageContainer = new PageContainer<WorkGroupNode>(null, null);
		String pattern = "xyz";
		boolean withTree = false;
		PageContainer<WorkGroupNode> nodes = workGroupNodeService.findAll(john, john, workGroup, null, pattern, withTree,
				false, pageContainer, null, null, null, null, null, null, null, null,
				SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the workgroup should returns only folders/documents/revisions with
		// name that contains given pattern
		Assertions.assertEquals(2, nodes.getPageResponse().getContent().size());
		pattern = "xYZ";
		nodes = workGroupNodeService.findAll(john, john, workGroup, null, pattern, withTree, false, pageContainer, null,
				null, null, null, null, null, null, null, SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the workgroup should returns only folders/documents/revisions with
		// name that contains given pattern
		Assertions.assertEquals(2, nodes.getPageResponse().getContent().size());
		// caseSensitive enabled
		boolean caseSensitive = true;
		nodes = workGroupNodeService.findAll(john, john, workGroup, null, pattern, withTree, caseSensitive, pageContainer,
				null, null, null, null, null, null, null, null, SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the workgroup should returns only folders/documents/revisions with
		// name that contains given pattern
		Assertions.assertEquals(0, nodes.getPageResponse().getContent().size());
		pattern = "";
		nodes = workGroupNodeService.findAll(john, john, workGroup, null, pattern, withTree, false, pageContainer, null,
				null, null, null, null, null, null, null, SortOrder.DESC, SharedSpaceNodeField.modificationDate, null);
		// search on the workgroup with name that contains empty pattern should returns
		// all folders/documents/revisions
		Assertions.assertEquals(5, nodes.getPageResponse().getContent().size());
	}

	@Test
	public void testFindAllWithAllFilters() throws IOException {
		// Test with search with all filters set together | findAll method (used to
		// search workgroup nodes)
		createWorkgroupDocument("linshare-default.properties", "linshare-temp0");
		createWorkgroupDocument("linshare-test.properties", "linshare-tempxyz");
		PageContainer<WorkGroupNode> pageContainer = new PageContainer<WorkGroupNode>(0, 5);
		String pattern = "";
		Date wgCreationDate = workGroup.getCreationDate();
		Date creationDateAfter = wgCreationDate;
		Date creationDateBefore = addHoursToDate(workGroup.getCreationDate(), 1);
		Date modificationDateAfter = wgCreationDate;
		Date modificationDateBefore = addHoursToDate(wgCreationDate, 1);
		List<WorkGroupNodeType> nodeTypes = Arrays.asList(WorkGroupNodeType.FOLDER, WorkGroupNodeType.DOCUMENT,
				WorkGroupNodeType.DOCUMENT_REVISION);
		List<String> lastAuthors = Lists.newArrayList(john.getLsUuid());
		Long minSize = 0L;
		Long maxSize = Long.MAX_VALUE;
		List<DocumentKind> documentKinds = Lists.newArrayList();
		documentKinds.add(DocumentKind.DOCUMENT);
		SortOrder sortOrder = SortOrder.DESC;
		SharedSpaceNodeField sortField = SharedSpaceNodeField.modificationDate;
		String parent = folder.getUuid();
		boolean withTree = false;
		PageContainer<WorkGroupNode> nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree,
				false, pageContainer, null, null, null, null, null, null, null, null, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, null, null, null, null, null, null, null, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore, null, null,
				null, null, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore, nodeTypes,
				null, null, null, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore,
				nodeTypes, lastAuthors, null, null, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore,
				nodeTypes, lastAuthors, minSize, maxSize, sortOrder, sortField, null);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore,
				nodeTypes, lastAuthors, minSize, maxSize, sortOrder, sortField, documentKinds);
		Assertions.assertEquals(4, nodes.getPageResponse().getContent().size());

		documentKinds.add(DocumentKind.OTHER);
		// means we want documents that have mimeType not exists in all defaultSupported
		// mimeTypes defined in DocumentKind Enum 
		// see WorkgroupNodeBusinessServiceImpl.getDefaultSupportedMimetypes()
		nodes = workGroupNodeService.findAll(john, john, workGroup, parent, pattern, withTree, false,
				pageContainer, creationDateAfter, creationDateBefore, modificationDateAfter, modificationDateBefore,
				nodeTypes, lastAuthors, minSize, maxSize, sortOrder, sortField, documentKinds);
		// documentKind of created workgroup documents is DOCUMENT, expected empty list
		Assertions.assertEquals(0, nodes.getPageResponse().getContent().size());
	}

	public Date addHoursToDate(Date date, int hours) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(1, 1);
		return calendar.getTime();
	}

	private void createWorkgroupDocument(String streamName, String tempFileName) throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(streamName);
		File tempFile = File.createTempFile(tempFileName, ".tmp");
		IOUtils.transferTo(stream, tempFile);
		workGroupNodeService.create(john, john, workGroup, tempFile, tempFile.getName(), folder.getUuid(), false);
	}

}
