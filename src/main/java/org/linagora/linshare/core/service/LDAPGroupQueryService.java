package org.linagora.linshare.core.service;

import java.io.IOException;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;

public interface LDAPGroupQueryService {

	public Set<LdapGroupObject> listGroups(LdapConnection ldapConnection, String baseDn, GroupLdapPattern groupPattern)
			throws BusinessException, NamingException, IOException;

	public Set<LdapGroupObject> searchGroups(LdapConnection ldapConnection, String baseDn, GroupLdapPattern groupPattern, String pattern)
			throws BusinessException, NamingException, IOException;

	public Set<LdapGroupMemberObject> listMembers(LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupObject group) throws BusinessException, NamingException, IOException;
}
