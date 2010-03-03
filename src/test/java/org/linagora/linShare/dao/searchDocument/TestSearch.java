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
package org.linagora.linShare.dao.searchDocument;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.enums.DocumentType;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-service.xml",
    "classpath:springContext-datasource.xml",
    "classpath:springContext-repository.xml",
    "classpath:springContext-dao.xml",
    "classpath:springContext-facade.xml",
    "classpath:springContext-jackRabbit.xml","classpath:springContext-test.xml"})
public class TestSearch extends AbstractJUnit4SpringContextTests{

	@Autowired
	private SearchDocumentFacade searchDocumentFacade;

	@Autowired
	private DocumentRepository documentRepository;

	@SuppressWarnings("unchecked")
	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;


	
	@Autowired
	private FileSystemDao fileRepository;
	
	private String jackUuid;
	
	private String linUuid;
	
	private InputStream jackStream;
	
	private InputStream linStream;
	
	private User user;
	
	private UserVo userVo;
	
	private String login;
	
	private Document documentJack;
	private Document documentLin;
	
	private List<String> documents;
	
	
	@Before
	public void setUp() throws Exception {
		documents=new ArrayList<String>();
		login="root@localhost.localdomain";
		jackStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("jackRabbit.properties");
		linStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		jackUuid=fileRepository.insertFile(login, jackStream, 10000, "jackRabbit.properties", "text/plain");
		linUuid=fileRepository.insertFile(login, linStream, 10000, "linShare-default.properties", "text/css");
		user=userRepository.findByLogin(login);

		userVo=new UserVo(user.getLogin(),user.getFirstName(),user.getLastName(),user.getMail(),user.getUserType());

		FileInfo jackInfo=fileRepository.getFileInfoByUUID(jackUuid);
		FileInfo linInfo=fileRepository.getFileInfoByUUID(linUuid);
		

		Calendar lastModified=jackInfo.getLastModified();
		Calendar exp=jackInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		//new Document
		documentJack=new Document(jackUuid,jackInfo.getName(),jackInfo.getMimeType(),lastModified,exp,user,false,false,new Long(10000));
		Calendar lastModifiedLin=linInfo.getLastModified();
		Calendar expLin=linInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		documentLin=new Document(linUuid,linInfo.getName(),linInfo.getMimeType(),lastModifiedLin,expLin,user,false,false,new Long(10000));
		
		documentRepository.create(documentJack);
		documentRepository.create(documentLin);
		documents.add(documentJack.getName());
		documents.add(documentLin.getName());
		
		
	}
	
	@Test
	@DirtiesContext
	public void testSearchByUser(){
		for(DocumentVo document: searchDocumentFacade.retrieveDocument(userVo) ){
			if(!documents.contains(document.getFileName())){
				Assert.fail();
			}
		}
	}
	
	@Test
	@DirtiesContext
	public void testSearchByName(){
		SearchDocumentCriterion searchDocumentCriterion=new SearchDocumentCriterion(userVo,documentJack.getName(),null,null,null,null,null,null,null,null,DocumentType.BOTH);
		for(DocumentVo document: searchDocumentFacade.retrieveDocumentContainsCriterion(searchDocumentCriterion) ){
			if(!documents.contains(document.getFileName())){
				Assert.fail();
			}
		}
		Assert.assertTrue(true);
	}
	
	
	@After
	public void tearDown() throws Exception{
		
			documentRepository.delete(documentJack);
			documentRepository.delete(documentLin);
			fileRepository.removeFileByUUID(documentJack.getIdentifier());
			fileRepository.removeFileByUUID(documentLin.getIdentifier());
	}
	
	

}

