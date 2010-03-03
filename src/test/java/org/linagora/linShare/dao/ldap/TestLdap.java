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
package org.linagora.linShare.dao.ldap;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.linagora.linShare.core.dao.LdapDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
    "classpath:springContext-datasource.xml", 
    "classpath:springContext-dao.xml",
    "classpath:springContext-startopends.xml"})
public class TestLdap extends AbstractJUnit4SpringContextTests{

	@Autowired
	private LdapDao ldapDao;

	
	@Test
	public void testExist(){
		Assert.assertTrue(ldapDao.exist("user1@linpki.org"));
	}
	
	@Test
	public void testNotExist(){
		Assert.assertFalse(ldapDao.exist("8"));
	}
	
	@Test
	public void testRetrieveValues(){
		ArrayList<String> keys=new ArrayList<String>();
		keys.add("user1@linpki.org");
		
		Map<String,String>map=ldapDao.getValues(keys, "mail","givenName","uid");
		
		Assert.assertEquals("user1@linpki.org", map.get("mail"));
		Assert.assertEquals("John", map.get("givenName"));
		Assert.assertEquals("user1", map.get("uid"));
	}
	
}
