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
package org.linagora.linshare.repository.hibernate;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.linagora.linshare.utils.DocumentCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DocumentEntryRepositoryImplTest  {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";

    
    private final String identifier = "docId";
    private final String type = "doctype";
    private final long fileSize = 1L;
    
    
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
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		user = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true, "comment");
		user.setDomain(rootDomain);
		user.setCmisLocale(rootDomain.getDefaultTapestryLocale().toString());
		userRepository.create(user);
		document = new Document(identifier, "name", type, Calendar.getInstance(), null, user, false, false, fileSize);
		documentRepository.create(document);
		logger.debug("End setUp");
	}

	@AfterEach
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
	public void testCreateAndFindGroupBy() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		DocumentEntry d = new DocumentEntry(user, "name", "comment", document);
		documentEntryRepository.create(d);
		Calendar bDate = Calendar.getInstance();
		bDate.add(GregorianCalendar.DATE, -1);
		Calendar eDate = Calendar.getInstance();
		eDate.add(GregorianCalendar.DATE, +7);
		List<DocumentCount> mimeTypes = documentEntryRepository.countAndGroupByMimeType(rootDomain, bDate, eDate);
		Assertions.assertEquals(1, mimeTypes.size());
		documentEntryRepository.delete(d);
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
