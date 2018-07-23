package org.linagora.linshare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;

public interface LDAPGroupQueryService {

	public List<String> listGroups(LdapConnection ldapConnection, String baseDn, GroupLdapPattern pattern)
			throws BusinessException, NamingException, IOException;
}
