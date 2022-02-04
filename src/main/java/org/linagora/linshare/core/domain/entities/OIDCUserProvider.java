/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
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

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;

public class OIDCUserProvider extends UserProvider {

	private String domainDiscriminator;

	private Boolean checkExternalUserID;

	private Boolean useAccessClaim;

	private Boolean useRoleClaim;

	private Boolean useEmailLocaleClaim;

	private Boolean moveBetweenDomainClaim;

	public OIDCUserProvider() {
		super();
	}

	public OIDCUserProvider(AbstractDomain domain, String domainDiscriminator) {
		super(domain);
		this.domainDiscriminator = domainDiscriminator;
		this.checkExternalUserID = false;
		this.useAccessClaim = false;
		this.useRoleClaim = false;
		this.useEmailLocaleClaim = false;
		this.moveBetweenDomainClaim = false;
	}

	public String getDomainDiscriminator() {
		return domainDiscriminator;
	}

	public void setDomainDiscriminator(String domainDiscriminator) {
		this.domainDiscriminator = domainDiscriminator;
	}

	public Boolean getCheckExternalUserID() {
		return checkExternalUserID;
	}

	public void setCheckExternalUserID(Boolean checkExternlUserID) {
		this.checkExternalUserID = checkExternlUserID;
	}

	public Boolean getUseAccessClaim() {
		return useAccessClaim;
	}

	public void setUseAccessClaim(Boolean useAccessClaim) {
		this.useAccessClaim = useAccessClaim;
	}

	public Boolean getUseRoleClaim() {
		return useRoleClaim;
	}

	public void setUseRoleClaim(Boolean useRoleClaim) {
		this.useRoleClaim = useRoleClaim;
	}

	public Boolean getUseEmailLocaleClaim() {
		return useEmailLocaleClaim;
	}

	public void setUseEmailLocaleClaim(Boolean useEmailLocaleClaim) {
		this.useEmailLocaleClaim = useEmailLocaleClaim;
	}

	public Boolean getMoveBetweenDomainClaim() {
		return moveBetweenDomainClaim;
	}

	public void setMoveBetweenDomainClaim(Boolean moveBetweenDomainClaim) {
		this.moveBetweenDomainClaim = moveBetweenDomainClaim;
	}

	@Override
	public String toString() {
		return "OIDCUserProvider [domainDiscriminator=" + domainDiscriminator + ", checkExternalUserID="
				+ checkExternalUserID + ", useAccessClaim=" + useAccessClaim + ", useRoleClaim=" + useRoleClaim
				+ ", useEmailLocalClaim=" + useEmailLocaleClaim + ", moveBetweenDomainClaim="
				+ moveBetweenDomainClaim + "]";
	}

	@Deprecated
	@Override
	public LDAPUserProviderDto toLDAPUserProviderDto() {
		// it is not used anymore, only kept for admin/v4 support.
		return null;
	}
}
