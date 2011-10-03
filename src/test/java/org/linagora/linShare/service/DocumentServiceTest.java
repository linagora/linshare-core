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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

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
@RunWith( SpringJUnit4ClassRunner.class) 
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
public class DocumentServiceTest{
	
	private InputStream inputStream;
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private DomainService domainService;
	
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
	
	private TransactionTemplate transactionTemplate;
	
	@Resource(name = "transactionManager")
	private HibernateTransactionManager tx;
	
	private String inputStreamUuid;
	private User John;
	private User Jane;
	private Document aDocument;
	
	private static final String DOMAIN_IDENTIFIER = "baseDomain";
	
	@Before
	@Transactional (propagation=Propagation.REQUIRED)
	public void setUp() throws Exception {
		
		John = userService
				.findAndCreateUser("user1@linpki.org", DOMAIN_IDENTIFIER);
		
		Jane = userService
				.findAndCreateUser("user2@linpki.org", DOMAIN_IDENTIFIER);

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
	}

	@After
	@Transactional (propagation=Propagation.SUPPORTS)
	public void tearDown() throws Exception {
		documentRepository.delete(aDocument);
		fileRepository.removeFileByUUID(aDocument.getIdentifier());
	}

	@Test
	public void testGetMimeType() {
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
	}

	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.SUPPORTS)
	public void testInsertFile() {
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
	}

	@Ignore
	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.REQUIRED)
	public void testUpdateFileContent() {

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
	}

	@Test
	@DirtiesContext
	// TODO : tester si la signature est bien dans jackrabbit et la bd.
	public void testInsertSignatureFile() {
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
	}

	@Test
	@DirtiesContext
	// TODO : if param.getGlobalQuotaActive()
	public void testGetAvailableSize() {
		try {
			long expected = 51200000; // default max size : baseParam in import-mysql.sql
			long actual = documentService.getAvailableSize(John);
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
	}

	@Test
	public void testGetTotalSize() {
		long expected = 51200000; // default max size : baseParam in import-mysql.sql
		long actual;
		
		try {
			actual = domainService.retrieveDomain(DOMAIN_IDENTIFIER).getParameter().getUserAvailableSize();
			Assert.assertEquals(expected, actual);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
	}

	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.REQUIRED)
	public void testDeleteFile() {
	
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
	}

	@Ignore
	@Test
	public void testDeleteFileWithNotification() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetDocument() {
		Document expected = aDocument;
		Document actual = documentService.getDocument(inputStreamUuid);
		
		Assert.assertEquals(expected, actual);
	}

	@Test
	// TODO : test : a Document with a thumbnailUuid not null
	public void testGetDocumentThumbnail() {
		InputStream expected = null;
		InputStream actual = documentService.getDocumentThumbnail(aDocument.getIdentifier());
		
		Assert.assertEquals(expected, actual);
	}

	@Test
	// TODO : test if a doc actually has a thumbnail
	public void testDocumentHasThumbnail() {
		Assert.assertFalse(documentService.documentHasThumbnail(aDocument.getIdentifier()));
	}

	@Ignore
	@Test
	public void testRetrieveFileStreamDocumentVoUserVo() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testDownloadSharedDocument() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testRetrieveFileStreamDocumentVoString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.REQUIRED)
	public void testDuplicateDocument() {
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
	}

	@Ignore
	@Test
	public void testRetrieveSignatureFileStream() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public void testUpdateDocumentContent() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.REQUIRED)
	public void testRenameFile() {
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

			String actual = aNewDoc.getName();
			Assert.assertEquals(expected, actual);
			// TODO Assert.assertEquals(expected, fileRepository.getFileInfoByUUID(aNewDoc.getIdentifier()).getName());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}	
	}

	@Test
	@DirtiesContext
	@Transactional (propagation=Propagation.REQUIRED)
	public void testUpdateFileProperties() {
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
			e.printStackTrace();
			Assert.assertFalse(true);
		}	
	}
	
}
