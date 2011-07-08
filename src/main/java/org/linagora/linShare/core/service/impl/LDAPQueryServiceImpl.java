package org.linagora.linShare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.ldap.JScriptEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;

public class LDAPQueryServiceImpl implements LDAPQueryService {
	
	private static final String LOG_INFO_KEY_PARSE = "Keys parsed which will be used in LDAP query are:";
	private static final Logger LOGGER = LoggerFactory.getLogger(LDAPQueryServiceImpl.class);
	@Override
	public User getUser(String userId, Domain domain, User actor) throws BusinessException {
		
		String command = domain.getPattern().getGetUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("domain", domain.getDifferentialKey());
		Map<String, List<String>> retMap = JScriptEvaluator.evalToEntryMap(domain, command, params);
		
		// tableau de correspondance entre attribut LDAP retourné et attribut de l'objet user
		String[] keys = parseKeys(domain);
		User user = mapToUser(retMap, keys, domain);
        
        return user;
	}

	@Override
	public List<User> getAllDomainUsers(Domain domain, User actor) throws BusinessException {
		
		String command = domain.getPattern().getGetAllDomainUsersCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domain", domain.getDifferentialKey());
		List<String> ret = JScriptEvaluator.evalToStringList(domain, command, params);
		List<User> users = dnListToUsersList(domain, ret);
        
        return users;
	}

	@Override
	public User auth(String login, String userPasswd, Domain domain) throws BusinessException, NamingException, IOException {
		
		String command = domain.getPattern().getAuthCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("domain", domain.getDifferentialKey());
		List<String> retList = JScriptEvaluator.evalToStringList(domain, command, params);
		
		if (retList == null || retList.size() < 1) {
			throw new NameNotFoundException("No user found for login: "+login+" and domain: "+domain.getIdentifier());
		}
		
		// tableau de correspondance entre attribut LDAP retourné et attribut de l'objet user
		String dn = retList.get(0);
		if(JScriptEvaluator.auth(userPasswd, dn, domain)) {
			String[] keys = parseKeys(domain);
			return dnToUser(domain, keys, dn);
		}
		return null;
	}

	@Override
	public List<User> searchUser(String mail, String firstName,
			String lastName, Domain domain, User actor) throws BusinessException {
		
		String command = domain.getPattern().getSearchUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		mail = toStarredString(mail);
		firstName = toStarredString(firstName);
		lastName = toStarredString(lastName);
		params.put("mail", mail);
		params.put("firstName", firstName);
		params.put("lastName", lastName);
		params.put("domain", domain.getDifferentialKey());
		List<String> ret = JScriptEvaluator.evalToStringList(domain, command, params);
		List<User> users = dnListToUsersList(domain, ret);
        
        return users;
	}

	private String toStarredString(String string) {
		if (string==null || string.length()<1){
			string="*";
		} else {
			string = "*" + string + "*";
		}
		return string;
	}

	private List<User> dnListToUsersList(Domain domain, List<String> ret) {
		String[] keys = parseKeys(domain);
		List<User> users = new ArrayList<User>();
		for (String string : ret) {
			User user = dnToUser(domain, keys, string);
	        users.add(user);
		}
		return users;
	}

	private User dnToUser(Domain domain, String[] keys, String string) {
		String unitCommand = "ldap.entry(dn,\"objectClass=*\");";
		Map<String, Object> unitParams = new HashMap<String, Object>();
		unitParams.put("dn", string);
		Map<String, List<String>> retMap = JScriptEvaluator.evalToEntryMap(domain, unitCommand, unitParams);
		User user = mapToUser(retMap, keys, domain);
		return user;
	}

	private User mapToUser(Map<String, List<String>> retMap, String[] keys, Domain domain) {
		String mail = (String) retMap.get(keys[0]).get(0);
        	String firstName = (String) retMap.get(keys[1]).get(0);
	        String lastName = (String) retMap.get(keys[2]).get(0);
	        User user = new Internal(mail, firstName, lastName, mail);
		user.setDomain(domain);
		return user;
	}

	private String[] parseKeys(Domain domain) {
		String keys = domain.getPattern().getGetUserResult().replaceAll("\\s+", " ").trim().toLowerCase();
		LOGGER.info(LOG_INFO_KEY_PARSE + keys);
		return keys.split(" ");
	}

}
