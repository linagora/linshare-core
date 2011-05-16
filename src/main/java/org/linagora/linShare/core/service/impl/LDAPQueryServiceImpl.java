package org.linagora.linShare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.ldap.JScriptEvaluator;

public class LDAPQueryServiceImpl implements LDAPQueryService {
	
	private final DomainService domainService;

	public LDAPQueryServiceImpl(DomainService domainService) {
		this.domainService = domainService;
	}

	@Override
	public User getUser(String userId, String domainId, User actor) throws BusinessException {
		
		Domain domain = domainService.retrieveDomain(domainId);
		String command = domain.getPattern().getGetUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("principal", actor.getId());
		params.put("userId", userId);
		params.put("domain", domain.getDifferentialKey());
		Map<String, List<String>> retMap = JScriptEvaluator.evalToEntryMap(domain, command, params);
		System.out.println(retMap);
		System.out.println("----------");
		// tableau de correspondance entre attribut LDAP retourné et attribut de l'objet user
		String[] keys = domain.getPattern().getGetUserResult().split(" ");
		User user = mapToUser(retMap, keys, domain);
        
        return user;
	}

	@Override
	public List<User> getAllDomainUsers(String domainId, User actor) throws BusinessException {
		
		Domain domain = domainService.retrieveDomain(domainId);
		String command = domain.getPattern().getGetAllDomainUsersCommand();
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("principal", actor.getId());
		params.put("domain", domain.getDifferentialKey());
		List<String> ret = JScriptEvaluator.evalToStringList(domain, command, params);
		List<User> users = dnListToUsersList(domain, ret);
        
        return users;
	}

	@Override
	public boolean isAdmin(String userId, String domainId) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User auth(String login, String userPasswd, String domainId) throws BusinessException, NamingException, IOException {
		
		Domain domain = domainService.retrieveDomain(domainId);
		String command = domain.getPattern().getAuthCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("domain", domain.getDifferentialKey());
		List<String> retList = JScriptEvaluator.evalToStringList(domain, command, params);
		System.out.println(retList);
		System.out.println("----------");
		System.out.println(userPasswd);
		// tableau de correspondance entre attribut LDAP retourné et attribut de l'objet user
		String dn = retList.get(0);
		if(JScriptEvaluator.auth(userPasswd, dn, domain)) {
			String[] keys = domain.getPattern().getGetUserResult().split(" ");
			return dnToUser(domain, keys, dn);
		}
		return null;
	}

	@Override
	public List<User> searchUser(String mail, String firstName,
			String lastName, String domainId, User actor) throws BusinessException {
		
		Domain domain = domainService.retrieveDomain(domainId);
		String command = domain.getPattern().getSearchUserCommand();
		System.out.println(command);
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
		System.out.println(ret);
		String[] keys = domain.getPattern().getGetUserResult().split(" ");
		System.out.println(Arrays.asList(keys));
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
		System.out.println(retMap);
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

}
