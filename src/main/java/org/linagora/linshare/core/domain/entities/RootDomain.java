/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

public class RootDomain extends AbstractDomain {

	public RootDomain() {
	}

	public RootDomain(String label) {
		super(label);
		this.defaultRole=Role.SUPERADMIN;
		this.defaultTapestryLocale=SupportedLanguage.ENGLISH;
	}

	@Deprecated
	public RootDomain(DomainDto domainDto) {
		super(domainDto, null);
		this.defaultRole=Role.SUPERADMIN;
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.ROOTDOMAIN;
	}

	@Override
	public boolean isRootDomain() {
		return true;
	}

	@Override
	public void updateDomainWith(AbstractDomain d) {
		this.label = d.getLabel();
		this.description = d.getDescription();
		this.defaultTapestryLocale = d.getDefaultTapestryLocale();
		this.externalMailLocale = d.getExternalMailLocale();
		this.authShowOrder = d.getAuthShowOrder();
		this.defaultRole = Role.SUPERADMIN;
	}

}
