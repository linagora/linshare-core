/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SearchDocumentService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;

/*
 * This all class was disable because of a huge spring context problem
 * */
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class SearchDocumentServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(SearchDocumentServiceImplTest.class);
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	@Autowired
	private SearchDocumentService searchDocumentService;
	
	@Autowired
	private ShareService shareService;
	
	private InputStream inputStream;
	private String inputStreamUuid;
	private User John;
	private User Jane;
	private Document aDocument;

	private LoadingServiceTestDatas datas;

	private Wiser wiser;
	
	public SearchDocumentServiceImplTest() {
		super();
		wiser = new Wiser(2525);

	}
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();

		
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		
		John = datas.getUser1();
		Jane = datas.getUser2();
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		inputStreamUuid = fileRepository.insertFile(Jane.getLogin(), inputStream, 10000, "linShare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, Jane,false,false,new Long(10000));
		List<Signature> signatures = new ArrayList<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
			Jane.addDocument(aDocument);
			userRepository.update(Jane);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		documentRepository.delete(aDocument);
		Jane.deleteDocument(aDocument);
		userRepository.update(Jane);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		datas.deleteUsers();
		
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testRetrieveDocument() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Set<Document> janeDocuments = searchDocumentService.retrieveDocument(Jane.getLogin());

		Assert.assertTrue(janeDocuments.contains(aDocument));
		
		janeDocuments = searchDocumentService.retrieveDocument(Jane);
		
		Assert.assertTrue(janeDocuments.contains(aDocument));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testRetrieveDocuments() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
	
		UserVo janeVo = new UserVo(Jane);
		
		List<DocumentVo> documents = searchDocumentService.retrieveDocuments(Jane);
		
		Assert.assertTrue(documents.get(0).getFileName().equalsIgnoreCase(aDocument.getName()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRetrieveDocumentContainsCriterion() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		UserVo janeVo = new UserVo(Jane);
		UserVo johnVo = new UserVo(John);
		
		// User criterion
		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null, null, null, null, null, null, null, null, null);
		
		List<DocumentVo> documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// User criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(johnVo, null, null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Name criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, aDocument.getName(), null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Name criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, "toto", null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Min size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, new Long(0), null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Min size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, new Long(Long.MAX_VALUE), null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Max size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , new Long(Long.MAX_VALUE), null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Max size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , new Long(0), null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Type criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , aDocument.getType(), null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Type criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , "toto", null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Shared criterion
		Boolean shared = false;
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, shared, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Shared criterion fail
		shared = true;
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, shared, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Creation date criterion
		Calendar creationDate = Calendar.getInstance();
		creationDate.add(Calendar.YEAR, -2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, creationDate, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Creation date criterion fail
		creationDate.add(Calendar.YEAR, 4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, creationDate, null, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Expiration date criterion
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.add(Calendar.YEAR, 2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, expirationDate, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Expiration date criterion fail
		expirationDate.add(Calendar.YEAR, -4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, expirationDate, null, null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Extension criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, "properties", null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Extension criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, "toto", null, null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
	
		// Shared From criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, null, janeVo.getMail(), null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Shared From criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, null,"toto" , null);
		
		documents = searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRetrieveShareDocumentContainsCriterion() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		UserVo janeVo = new UserVo(Jane);
		UserVo johnVo = new UserVo(John);
		
		Calendar cldr = Calendar.getInstance();
		cldr.add(Calendar.YEAR, 1);
		
	    List<Document> shareDocuments = new ArrayList<Document>();
	    shareDocuments.add(aDocument);
		
	    List<User> recipient = new ArrayList<User>();
	    recipient.add(Jane);
	    
	    SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(shareDocuments, John, recipient, cldr );
		
		// User criterion
		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null, null, null, null, null, null, null, null, null);
		
		List<ShareDocumentVo> documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// User criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(johnVo, null, null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Name criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, aDocument.getName(), null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Name criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, "toto", null, null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Min size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, new Long(0), null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Min size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, new Long(Long.MAX_VALUE), null, null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Max size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , new Long(Long.MAX_VALUE), null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Max size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , new Long(0), null, null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Type criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , aDocument.getType(), null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Type criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , "toto", null, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Shared criterion
		Boolean shared = false;
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, shared, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Shared criterion fail
		shared = true;
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, shared, null, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		
		// Creation date criterion
		Calendar creationDate = Calendar.getInstance();
		creationDate.add(Calendar.YEAR, -2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, creationDate, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Creation date criterion fail
		creationDate.add(Calendar.YEAR, 4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, creationDate, null, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		// Expiration date criterion
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.add(Calendar.YEAR, 2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, expirationDate, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Expiration date criterion fail
		expirationDate.add(Calendar.YEAR, -4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, expirationDate, null, null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		// Extension criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, "properties", null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Extension criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, "toto", null, null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
	
		// Shared From criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, null, janeVo.getMail(), null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertFalse(documents.isEmpty());
		
		// Shared From criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null, null , null , null, null, null, null, null,"toto" , null);
		
		documents = searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
		
		Assert.assertTrue(documents.isEmpty());
		
		
		Jane.deleteReceivedShare(shares.getSuccessesItem().get(0));
		John.deleteShare(shares.getSuccessesItem().get(0));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
