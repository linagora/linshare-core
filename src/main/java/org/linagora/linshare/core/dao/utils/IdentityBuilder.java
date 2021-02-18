/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2021 LINAGORA
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

package org.linagora.linshare.core.dao.utils;

import java.util.Optional;

import com.google.common.base.MoreObjects;

public class IdentityBuilder {

	private Optional<String> identity = Optional.empty();
	private Optional<String> userName = Optional.empty();
	private Optional<String> userDomain = Optional.empty();
	private Optional<String> tenantIdentifier = Optional.empty();

	private IdentityBuilder() {
		super();
	}

	public static IdentityBuilder New( ) {
		return new IdentityBuilder();
	}

	public IdentityBuilder identity(String identity) {
		this.identity = Optional.ofNullable(identity).filter(s -> !s.isEmpty());
		return this;
	}

	public IdentityBuilder userName(String userName) {
		this.userName = Optional.ofNullable(userName).filter(s -> !s.isEmpty());
		return this;
	}

	public IdentityBuilder userDomainName(String userDomain) {
		this.userDomain = Optional.ofNullable(userDomain).filter(s -> !s.isEmpty());
		return this;
	}

	public IdentityBuilder tenantName(String tenantIdentifier) {
		this.tenantIdentifier = Optional.ofNullable(tenantIdentifier).filter(s -> !s.isEmpty());
		return this;
	}

	/**
	 * This method will return the expected formated identity string for jcloud.
	 * @return
	 */
	public String build() {
		if (identity.isPresent()) {
			return identity.get();
		}
		StringBuilder sb = new StringBuilder();
		if (userDomain.isPresent() || tenantIdentifier.isPresent()) {
			if (userDomain.isPresent()) {
				sb.append(userDomain.get());
			} else {
				sb.append(tenantIdentifier.get());
			}
			sb.append(":");
		}
		sb.append(userName.get());
		return sb.toString();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("identity", identity)
				.add("userName", userName)
				.add("userDomain", userDomain)
				.add("tenantIdentifier", tenantIdentifier)
				.toString();
	}

}
