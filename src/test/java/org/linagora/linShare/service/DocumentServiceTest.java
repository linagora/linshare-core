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
package org.linagora.linShare.service;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.FunctionalityService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;



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
public class DocumentServiceTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(DocumentServiceTest.class);
	
	private InputStream inputStream;
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private AbstractDomainService abstractDomainService;
	
	@Autowired
	private FunctionalityService functionalityService;

	
	@Autowired
	private UserService userService;
	
	@SuppressWarnings("unchecked")
	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;

	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	private String inputStreamUuid;
	private User John;
	private User Jane;
	private Document aDocument;
	
	// default import.sql
	private static final String DOMAIN_IDENTIFIER = "MySubDomain";
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		try {
			John = userService.findOrCreateUser("user1@linpki.org", DOMAIN_IDENTIFIER);
			Jane = userService.findOrCreateUser("user2@linpki.org", DOMAIN_IDENTIFIER);
		} catch (BusinessException e1) {
			try {
				John = userService.findOrCreateUser("user1@linpki.org", DOMAIN_IDENTIFIER);
				Jane = userService.findOrCreateUser("user2@linpki.org", DOMAIN_IDENTIFIER);
			} catch (BusinessException e2) {
				logger.error("Can't create default user for test environnement.");
				e2.printStackTrace();
			}
			
//			logger.error("Can't create default user for test environnement.");
//			e1.printStackTrace();
		}
		
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		inputStreamUuid = fileRepository.insertFile(Jane.getLogin(), inputStream, 10000, "linShare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, Jane,false,false,false,new Long(10000));
		List<Signature> signatures = new ArrayList<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		documentRepository.delete(aDocument);
		fileRepository.removeFileByUUID(aDocument.getIdentifier());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testGetMimeType() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream tmp = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		String expected = "text/plain";
		String actual = null;
		try {
			actual = documentService.getMimeType(tmp, "/");
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		Assert.assertEquals(expected, actual);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testInsertFile() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream tmp = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		try {
			Document aNewDoc = documentService.insertFile(John.getLogin(), tmp, tmp.available(), "linShare-default.properties", "text/plain", John);
			
			Assert.assertTrue(John.getDocuments().contains(aNewDoc));
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testUpdateFileContent() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {				
			InputStream update = Thread.currentThread().getContextClassLoader().getResourceAsStream("jackRabbit.properties");
			InputStream updatedFile = null;
			String expected = IOUtils.toString(update, "UTF-8");
			String actual = null;
			
			updatedFile = fileRepository.getFileContentByUUID(aDocument.getIdentifier());
			
			documentService.updateFileContent(aDocument.getIdentifier(), update, 
					update.available(), aDocument.getName(), 
					aDocument.getType(), aDocument.getEncrypted(), aDocument.getOwner());
			
			updatedFile = fileRepository.getFileContentByUUID(aDocument.getIdentifier());

			IOUtils.closeQuietly(update);				
			actual = IOUtils.toString(updatedFile, "UTF-8");
			
			System.out.println("==============================================");
			System.out.println(actual);
			System.out.println("==============================================");
			
			IOUtils.closeQuietly(updatedFile);
			
			Assert.assertEquals(expected, actual);			
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	// TODO : tester si la signature est bien dans jackrabbit et la bd.
	public void testInsertSignatureFile() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {		
			InputStream signatureFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("CAS.bin.export");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate c = (X509Certificate)cf.generateCertificate(signatureFile);
		    
			//Assert.assertTrue(aDocument.getSignatures().size() == 0);
			
			documentService.insertSignatureFile(signatureFile, (long)10000,
						"cert", "text/plain", Jane, aDocument,
						(X509Certificate)c);
				
			signatureFile.close();				 
			} catch (BusinessException e) {
				e.printStackTrace();
				Assert.assertFalse(true);
			} catch (CertificateException e) {
				e.printStackTrace();
				Assert.assertFalse(true);
			} catch (IOException e) {
				e.printStackTrace();
				Assert.assertFalse(true);
			}
				
		List<Signature> signatures = aDocument.getSignatures();
		Assert.assertTrue(signatures.size() == 1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	// TODO : if param.getGlobalQuotaActive()
	public void testGetAvailableSize() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			long expected = 104857600; // default max size : baseParam in import-mysql.sql
			long actual = documentService.getAvailableSize(John);
			logger.debug("actual : "+ actual );
			logger.debug("expected : "+ expected );
			Assert.assertTrue(actual == expected);
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
			documentService.insertFile(John.getLogin(),
					inputStream, 20000,
					"linShare.properties", documentService
							.getMimeType(inputStream,
									"linShare.properties"),
					John);		
			
			long afterInsert = documentService.getAvailableSize(John);
			Assert.assertEquals(afterInsert, expected - 20000);
			
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetTotalSize() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long expected = 104857600; // default max size : import.sql
		long actual;
		
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(DOMAIN_IDENTIFIER);
			SizeUnitValueFunctionality func = functionalityService.getUserQuotaFunctionality(domain);
			
			actual = func.getPlainSize();
			Assert.assertEquals(expected, actual);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteFile() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			System.out.println(John.getDocuments().size());
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
			Document aDoc = documentService.insertFile(John.getLogin(),
					inputStream, 20000,
					"linShare.properties", documentService
							.getMimeType(inputStream,
									"linShare.properties"),
					John);	
			Assert.assertFalse(John.getDocuments().isEmpty());
			documentService.deleteFile(John.getLogin(), aDoc.getIdentifier(), Reason.EXPIRY);
			Assert.assertTrue(John.getDocuments().size() == 0);
			Assert.assertNull(fileRepository.getFileContentByUUID(aDoc.getIdentifier()));
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testDeleteFileWithNotification() {
		fail("Not yet implemented"); 
	}

	@Test
	public void testGetDocument() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Document expected = aDocument;
		Document actual = documentService.getDocument(inputStreamUuid);
		
		Assert.assertEquals(expected, actual);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	// test : a Document with a thumbnailUuid not null
	public void testGetDocumentThumbnail() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream expected = null;
		InputStream actual = documentService.getDocumentThumbnail(aDocument.getIdentifier());
		
		Assert.assertEquals(expected, actual);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	// test if a doc actually has a thumbnail
	public void testDocumentHasThumbnail() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assert.assertFalse(documentService.documentHasThumbnail(aDocument.getIdentifier()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testRetrieveFileStreamDocumentVoUserVo() {
		fail("Not yet implemented"); // 
	}

	@Ignore
	@Test
	public void testDownloadSharedDocument() {
		fail("Not yet implemented"); // 
	}

	@Ignore
	@Test
	public void testRetrieveFileStreamDocumentVoString() {
		fail("Not yet implemented"); // 
	}

	@Test
	public void testDuplicateDocument() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			String expected = IOUtils.toString(fileRepository.getFileContentByUUID(aDocument.getIdentifier()));
			String actual = null;
			Document aNewDoc = documentService.duplicateDocument(aDocument, John);
			
			actual = IOUtils.toString(fileRepository.getFileContentByUUID(aNewDoc.getIdentifier()));
			
			Assert.assertEquals(expected, actual);
			Assert.assertTrue(John.getDocuments().contains(aNewDoc));
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testRetrieveSignatureFileStream() {
		fail("Not yet implemented"); // 
	}

	@Ignore
	@Test
	public void testUpdateDocumentContent() {
	}

	@Test
	public void testRenameFile() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		String expected = "toto.txt";
		
		try {		
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
			Document aNewDoc = documentService.insertFile(John.getLogin(),
					inputStream, 20000,
					"linShare.properties", documentService
							.getMimeType(inputStream,
									"linShare.properties"),
					John);
			
			documentService.renameFile(aNewDoc.getIdentifier(), expected);

			String actual = documentRepository.findById(aNewDoc.getIdentifier()).getName();
			Assert.assertEquals(expected, actual);
			// TODO Assert.assertEquals(expected, fileRepository.getFileInfoByUUID(aNewDoc.getIdentifier()).getName());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}	
		
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateFileProperties() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String expected = "a file";
		try{
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
			Document aNewDoc = documentService.insertFile(John.getLogin(),
					inputStream, 20000,
					"linShare.properties", documentService
							.getMimeType(inputStream,
									"linShare.properties"),
					John);
			documentService.updateFileProperties(aNewDoc.getIdentifier(), null, expected);
			
			String actual = aNewDoc.getFileComment();
			Assert.assertEquals(expected, actual);	
		} catch (BusinessException e) {
			logger.error("documentService.updateFileProperties failed!");
			e.printStackTrace();
			Assert.assertFalse(true);
		}	
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
