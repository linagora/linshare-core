package org.linagora.linShare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import junit.framework.TestCase;

import org.junit.Test;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.ldap.JScriptEvaluator;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations={"classpath:springContext-test.xml",
	    "classpath:springContext-datasource.xml", 
	    "classpath:springContext-dao.xml",
	    "classpath:springContext-startopends.xml"})
public class JScriptEvaluatorTest extends TestCase {
	
	@Test
	public void test() throws NamingException, IOException {
		// Set up JavaScript objects
		Map<String, Object> javaScriptObjects = new HashMap<String, Object>();
//		javaScriptObjects.put("principal", "uid=jdoe,ou=Users,dc=foo,dc=bar");
//		javaScriptObjects.put("target", "uid=jsmith,ou=Users,dc=foo,dc=bar");
//		javaScriptObjects.put("domain", "test");
		Map<String, List<String>> attendedResults = new HashMap<String, List<String>>();
		List<String> openldapRootDSEOC = new ArrayList<String>();
		openldapRootDSEOC.add("top");
		openldapRootDSEOC.add("OpenLDAProotDSE");
		attendedResults.put("objectClass", openldapRootDSEOC);
		LDAPConnection ldapConn = new LDAPConnection("test", "ldap://localhost:389", "anonymous");
//		LDAPConnection ldapConn = new LDAPConnection("test", "ldap://ldap.par.lng", "anonymous");
		Domain domain = new Domain("test", "", null, ldapConn);
		assertEquals(attendedResults,JScriptEvaluator.evalToEntryMap(domain, "ldap.entry(\"\",\"objectClass=*\");", javaScriptObjects));
		javaScriptObjects.put("id", "rlaporte");
		javaScriptObjects.put("domain", "dc=nodomain,dc=com");
		
		List<String> sdupreyRes = new ArrayList<String>();
		sdupreyRes.add("robert.laporte@nodomain.com");
		sdupreyRes.add("rlaporte@nodomain.com");
//		assertEquals(sdupreyRes,JScriptEvaluator.evalToEntryMap(domain, "ldap.entry(\"uid=\" + id + \",ou=test,o=linShare,\" + domain, \"objectClass=*\");", javaScriptObjects).get("mail"));
		System.out.println(JScriptEvaluator.evalToStringList(domain, "ldap.list(\"ou=test,o=linShare,\" + domain, \"(&(objectClass=*)(mail=rla*)(givenName=*)(sn=*))\");", javaScriptObjects));
		System.out.println(JScriptEvaluator.auth("robert2", "uid=rlaporte,ou=test,o=linShare,dc=nodomain,dc=com", domain));
	}
	
}
