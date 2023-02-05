/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;

public interface LDAPGroupSyncService {

	SharedSpaceLDAPGroup createOrUpdateLDAPGroup(Account actor, AbstractDomain domain, LdapGroupObject group,
			Date syncDate, LdapGroupsBatchResultContext resultContext, NodeType nodeType);

	SharedSpaceLDAPGroupMember createOrUpdateLDAPGroupMember(Account actor, String domainUuid,
			SharedSpaceLDAPGroup group, LdapGroupMemberObject memberObject, Date syncDate,
			LdapGroupsBatchResultContext resultContext, Boolean searchInOtherDomains);

	void applyTask(Account actor, AbstractDomain domain, LdapGroupObject ldapGroupObject,
			Set<LdapGroupMemberObject> memberObjects, Date syncDate, LdapGroupsBatchResultContext resultContext);

	void executeBatch(Account actor, AbstractDomain domain, LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupsBatchResultContext resultContext)
			throws BusinessException, NamingException, IOException;

}
