/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.dao.searchDocument;



/*
 * This all class was disable because of a huge spring context problem
 * 
@ContextConfiguration(locations={"classpath:springContext-service.xml",
    "classpath:springContext-datasource.xml",
    "classpath:springContext-repository.xml",
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
		linStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		jackUuid=fileRepository.insertFile(login, jackStream, 10000, "jackRabbit.properties", "text/plain");
		linUuid=fileRepository.insertFile(login, linStream, 10000, "linshare-default.properties", "text/css");
		user=userRepository.findByLogin(login);

		userVo=new UserVo(user.getLogin(),user.getFirstName(),user.getLastName(),user.getMail(),user.getUserType());

		FileInfo jackInfo=fileRepository.getFileInfoByUUID(jackUuid);
		FileInfo linInfo=fileRepository.getFileInfoByUUID(linUuid);
		

		Calendar lastModified=jackInfo.getLastModified();
		Calendar exp=jackInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		//new Document
		documentJack=new Document(jackUuid,jackInfo.getName(),jackInfo.getMimeType(),lastModified,exp,user,false,false,false,new Long(10000));
		Calendar lastModifiedLin=linInfo.getLastModified();
		Calendar expLin=linInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		documentLin=new Document(linUuid,linInfo.getName(),linInfo.getMimeType(),lastModifiedLin,expLin,user,false,false,false,new Long(10000));
		
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
/**/
