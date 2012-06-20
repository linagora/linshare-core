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
package org.linagora.linShare.repository.hibernate;


import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DocumentRepositoryImplTest  extends AbstractTransactionalJUnit4SpringContextTests{

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
    
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userDao;

	@Autowired
	private DocumentRepository documentRepository;
	
//	@Test
//	public void testExistDocument() throws BusinessException{
//		User u = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true, "comment");
//		
//		userDao.create(u);
//		
//		Document doc = new Document(identifier, name, type, new GregorianCalendar(), new GregorianCalendar(), u, encrypted, shared, fileSize);
//		
//		documentRepository.create(doc);
//		
//		Assert.assertTrue(documentRepository.findById(identifier)!=null);
//		Assert.assertFalse(documentRepository.findById(identifier+"dummy")!=null);
//		Assert.assertFalse(userDao.exist("login2", PASSWORD));
//		
//		
//		documentRepository.delete(doc);
//	}
	
	@Test
	public void testUserDocument() throws BusinessException{
		User u = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true,"comment");
	
		userDao.create(u);

		Document aDoc = new Document(identifier, name, type, new GregorianCalendar(), new GregorianCalendar(), u, encrypted, shared, fileSize);
		
		u.addDocument(aDoc);
		
		userDao.update(u);
		
		User u1 = userDao.findByMail(LOGIN);
		Assert.assertTrue(u1.getDocuments()!=null);
		Document document=u1.getDocuments().iterator().next();
		
		Assert.assertTrue(document!=null);
		Assert.assertTrue(document.getIdentifier()==identifier);

		u = null;
		userDao.delete(u1);
	}
	
	
	@Test
	public void testUserCannotAddDocument() throws IllegalArgumentException, BusinessException {
		User u = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true,"comment");
	
		userDao.create(u);

		Document aDoc = new Document(identifier, name, type, new GregorianCalendar(), new GregorianCalendar(), u, encrypted, shared, fileSize);
		
		try {
			u.addDocument(aDoc);
		} catch (BusinessException e) {
			Assert.assertTrue(e.getErrorCode().equals(BusinessErrorCode.USER_CANNOT_UPLOAD));
		}
		
		
		userDao.delete(u);
	}
}
