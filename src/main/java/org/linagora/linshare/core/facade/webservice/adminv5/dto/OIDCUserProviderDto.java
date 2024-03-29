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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OIDCUserProvider", description = "A OIDC user provider")
public class OIDCUserProviderDto extends AbstractUserProviderDto {

	@Schema(description = "domain discriminator used when user login to know in which domain he belongs."
			+ "Must be unique among all domains. (claim name: domain_discriminator)", required = true)
	private String domainDiscriminator;

	@Schema(description = "Whether or not we should compare the external unique identifier we store at profile "
			+ "creation time with the one provided by OIDC when user authenticate himself. (claim name: external_uid)",
			required = false,
			defaultValue = "false")
	private Boolean checkExternalUserID;

	@Schema(description = "Whether or not we should use access claim value to grant access to LinShare. (claim name: linshare_access)."
			+ "By default linshare_role should match the string value 'true', See linshare.properties file to change the default value.",
			required = false,
			defaultValue = "false")
	private Boolean useAccessClaim;

	@Schema(description = "Whether or not we should use role claim value to use it for profile creation at login time. (claim name: linshare_role)."
			+ "Possible values for this claim: SIMPLE / ADMIN",
			required = false,
			defaultValue = "false")
	private Boolean useRoleClaim;

	@Schema(description = "Whether or not we should use email locale claim value to use it for profile creation at login time. (claim name: linshare_locale)."
			+ "See Language enum/model for possible values for this claim.",
			required = false,
			defaultValue = "false")
	private Boolean useEmailLocaleClaim;

//	@Schema(description = "Whether or not we should move a user from one domain to another automatically",
//			required = false,
//			defaultValue = "false")
//	private Boolean useMoveBetweenDomainClaim;

	@Schema(defaultValue = "OIDC_PROVIDER")
	@Override
	public UserProviderType getType() {
		return UserProviderType.OIDC_PROVIDER;
	}

	protected OIDCUserProviderDto() {
		super();
	}

	public OIDCUserProviderDto(OIDCUserProvider up) {
		super(up);
		this.checkExternalUserID = up.getCheckExternalUserID();
		this.domainDiscriminator = up.getDomainDiscriminator();
		this.useEmailLocaleClaim = up.getUseEmailLocaleClaim();
		this.useAccessClaim = up.getUseAccessClaim();
		this.useRoleClaim = up.getUseRoleClaim();
		this.type = UserProviderType.OIDC_PROVIDER;
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

	public void setCheckExternalUserID(Boolean checkExternalUserID) {
		this.checkExternalUserID = checkExternalUserID;
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

	@Override
	public String toString() {
		return "OIDCUserProviderDto [domainDiscriminator=" + domainDiscriminator + ", checkExternalUserID="
				+ checkExternalUserID + ", useAccessClaim=" + useAccessClaim + ", useRoleClaim=" + useRoleClaim
				+ ", useEmailLocaleClaim=" + useEmailLocaleClaim + "]";
	}

}
