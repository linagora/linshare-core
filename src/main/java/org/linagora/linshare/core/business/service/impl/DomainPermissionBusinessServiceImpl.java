/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;


import com.google.common.collect.Lists;

public class DomainPermissionBusinessServiceImpl implements
		DomainPermissionBusinessService {

	private final DomainBusinessService domainBusinessService;

	public DomainPermissionBusinessServiceImpl(
			final DomainBusinessService domainBusinessService) {
		super();
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public boolean isAdminforThisDomain(Account actor, AbstractDomain domain) {
		if (!(actor.hasSuperAdminRole() || actor.hasSystemAccountRole())) {
			if (!domain.isManagedBy(actor)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isAdminForThisUser(Account actor, User user) {
		return isAdminforThisDomain(actor, user.getDomain())
				|| isOwner(actor, user);
	}

	@Override
	public boolean isAdminForThisUploadRequest(Account actor, UploadRequest request) {
		return isAdminforThisDomain(actor, request.getUploadRequestGroup().getAbstractDomain())
				|| isOwner(actor, request);
	}

	@Override
	public List<String> getAdministredDomainsIdentifiers(Account actor, String domainUuid) {
		if (!(actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			return Lists.newArrayList();
		}
		return findRecursivelyDomainsIdentifiers(domainUuid);
	}

	private List<String> findRecursivelyDomainsIdentifiers(String uuid) {
		List<String> list = Lists.newArrayList();
		list.add(uuid);
		List<String> identifiers = domainBusinessService.getSubDomainsByDomainIdentifiers(uuid);
		for (String identifier : identifiers) {
			list.addAll(findRecursivelyDomainsIdentifiers(identifier));
		}
		return list;
	}

	@Override
	public List<AbstractDomain> getMyAdministredDomains(Account actor) {
		if (!(actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			return Lists.newArrayList();
		}
		return findRecursivelyDomains(actor.getDomain());
	}

	private List<AbstractDomain> findRecursivelyDomains(AbstractDomain root) {
		List<AbstractDomain> list = Lists.newArrayList();
		list.add(root);
		List<AbstractDomain> abstractDomains = domainBusinessService.getSubDomainsByDomain(root.getUuid());
		for (AbstractDomain sub : abstractDomains) {
			list.addAll(findRecursivelyDomains(sub));
		}
		return list;
	}

	private boolean isOwner(Account actor, User guest) {
		return guest instanceof Guest && guest.getOwner().equals(actor);
	}

	private boolean isOwner(Account actor, UploadRequest request) {
		return request.getUploadRequestGroup().getOwner().equals(actor);
	}
}
