/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractDomainRepository extends AbstractRepository<AbstractDomain> {
	
	AbstractDomain findById(String identifier);

	/**
	 * return all TopDomain, SubDomain, GuestDomain and root domain identifiers
	 * @return List<String>
	 */
	List<String> findAllDomainIdentifiers();

	/**
	 * return all TopDomain, SubDomain, GuestDomain and root domain identifiers
	 * order by reverse creation date.
	 * This method is used during authentication process to browser all User Provider.
	 * It is  temporary workaround for SAAS deployment with thousands of domains.
	 * @return List<String>
	 */
	List<String> findAllDomainIdentifiersForAuthenticationDiscovery();

	/**
	 * return all TopDomain and SubDomain objects
	 * @return List<AbstractDomain>
	 */
	List<AbstractDomain> findAllDomain();
	
	/**
	 * return all TopDomain and SubDomain objects, excluding Guest and Root domains
	 * @return List<AbstractDomain>
	 */
	List<AbstractDomain> findAllTopAndSubDomain();

	/**
	 * return all SubDomain and GuestDomain identifiers, excluding Top and Root domains
	 * @return List<String>
	 */
	List<String> findAllGuestAndSubDomainIdentifiers();

	/**
	 * return all TopDomain objects
	 * @return List<AbstractDomain>
	 */
	List<AbstractDomain> findAllTopDomain();
	
	/**
	 * return all SubDomain objects
	 * @return List<AbstractDomain>
	 */
	List<AbstractDomain> findAllSubDomain();

	/**
	 * return the unique root domain
	 * @return AbstractDomain
	 */
	AbstractDomain getUniqueRootDomain() throws BusinessException;

	/**
	 * return all domains using cfg as their current mail configuration
	 * @param cfg
	 * @return List<AbstractDomain>
	 */
	List<AbstractDomain> findByCurrentMailConfig(MailConfig cfg);

	List<AbstractDomain> loadDomainsForAWelcomeMessage(WelcomeMessages welcomeMessage) throws BusinessException;

	List<String> getAllSubDomainIdentifiers(String domain);
	
	void markToPurge(AbstractDomain abstractDomain);

	void purge(AbstractDomain abstractDomain);

	List<String> findAllAbstractDomainsReadyToPurge();
	
	AbstractDomain findDomainReadyToPurge(String lsUuid);

	List<AbstractDomain> getSubDomainsByDomain(String uuid);
	
	AbstractDomain getGuestSubDomainByDomain(String uuid);

	/**
	 * return all Domains with group provider
	 * @return List<String>
	 */
	List<String> findAllDomainIdentifiersWithGroupProviders();

	List<String> getSubDomainsByDomainIdentifiers(String domain);

	List<String> findAllDomainIdentifiersWithWorkSpaceProviders();

	List<AbstractDomain> findAllDomainsByLdapConnection(LdapConnection ldapConnection);

	List<AbstractDomain> findAllDomainsByTwakeConnection(TwakeConnection twakeConnection);

	List<AbstractDomain> findAllDomainsByUserFilter(UserLdapPattern domainUserFilter);

	List<AbstractDomain> findAllDomainsByGroupFilter(GroupLdapPattern domainGroupFilter);

	List<AbstractDomain> findAllDomainsByWorkSpaceFilter(LdapWorkSpaceFilter domainWorkSpaceFilter);

}
