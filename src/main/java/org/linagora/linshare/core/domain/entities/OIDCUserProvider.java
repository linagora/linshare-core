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
