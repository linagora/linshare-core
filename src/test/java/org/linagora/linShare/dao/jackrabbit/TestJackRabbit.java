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
package org.linagora.linShare.dao.jackrabbit;


/*
 * This all class was disable because of a huge spring context problem
 
@ContextConfiguration(locations={"classpath:springContext-jackRabbit.xml","classpath:springContext-test.xml"})
public class TestJackRabbit extends AbstractJUnit4SpringContextTests{

	
	
	@Autowired
	private FileSystemDao fileRepository;
	private InputStream inputStream;
	
	@Before
	public void setUp() throws Exception {
		
		this.inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		
	}

	@Test
	@DirtiesContext
	public void testInsertFile(){
		
		
		String uuid=this.fileRepository.insertFile("user1", inputStream, 0, "linShare-default.properties", "txt");
		try {
			this.inputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
		BufferedReader out=new BufferedReader(new InputStreamReader(this.fileRepository.getFileContentByUUID(uuid)));
		String first=null;
		String second=null;
		try{
		while( (first=bufferedReader.readLine())!=null && ((second=out.readLine()) !=null) ){
			if(!first.equals(second)){
				break;
			}
		}
		
		if(null==first && null==second){
			Assert.assertTrue(true);
		}
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertFalse(true);
		}
		
		
		
	}
	
//
//	@Test
//	public void testGetFile(){
//		//this.fileRepository.insertFile("user1", inputStream, "txt");
//		InputStream stream=this.fileRepository.getFile("user1/user1/");
//		BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
//		String dd="";
//		try {
//			while(( dd=reader.readLine())!=null){
//				System.out.println(dd);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(this.inputStream.equals(stream)){
//			Assert.assertTrue(true);
//		}
//	}
//
//	
//	@Test
//	public void getPath(){
//		
//		for(String current:this.fileRepository.getAllPath()){
//			System.out.println(current);
//		}
//	}
//	
}
/**/