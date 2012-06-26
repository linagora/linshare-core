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


import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DocumentRepositoryImplTest  extends AbstractTransactionalJUnit4SpringContextTests{
    
    private final String identifier = "docId";
    private final String type = "doctype";
    private final long fileSize = 1l;
    
    
    // Services
	@Autowired
	private DocumentRepository documentRepository;
	
	
	@Test
	public void testCreateDocument() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document doc = new Document(identifier, type, fileSize);
		documentRepository.create(doc);

		Assert.assertTrue(documentRepository.findById(identifier)!=null);
		Assert.assertFalse(documentRepository.findById(identifier+"dummy")!=null);
		documentRepository.delete(doc);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
