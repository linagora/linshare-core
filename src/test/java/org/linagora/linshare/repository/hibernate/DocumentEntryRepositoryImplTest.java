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
package org.linagora.linshare.repository.hibernate;


import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DocumentEntryRepositoryImplTest  extends AbstractTransactionalJUnit4SpringContextTests{

    private static final String LOGIN = "login";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";

    
    private final String identifier = "docId";
    private final String name ="docName";
    private final String type = "doctype";
    private final Boolean encrypted = false;
    private final Boolean shared = false;
    private final long fileSize = 1l;
    
    
    // Members
    private AbstractDomain rootDomain;
    private User user;
    private Document document;
    
    //Services
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	

	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	
	@Autowired
	private DocumentEntryRepository documentEntryRepository;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		user = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true, "comment");
		user.setDomain(rootDomain);
		userRepository.create(user);
		
		document = new Document(identifier, type, fileSize);
		documentRepository.create(document);
		
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		documentRepository.delete(document);
		userRepository.delete(user);
		logger.debug("End tearDown");
	}
	
	@Test
	public void testCreateDocumentEntry() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		DocumentEntry d = new DocumentEntry(user, "name", "comment", document);
		documentEntryRepository.create(d);
		documentEntryRepository.delete(d);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testCreateShareEntry() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		DocumentEntry doc = new DocumentEntry(user, "name", "comment", document);
		documentEntryRepository.create(doc);
		logger.debug("documentEntry id : " + doc.getUuid());
		
		ShareEntry share = new ShareEntry(user, "my share", "comment", user, doc);
		documentEntryRepository.create(share);
		logger.debug("shareEntry id : " + share.getUuid());
		
		documentEntryRepository.delete(share);
		documentEntryRepository.delete(doc);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateAnonymousShareEntry() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
//		Assert.assertTrue(false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	
}
