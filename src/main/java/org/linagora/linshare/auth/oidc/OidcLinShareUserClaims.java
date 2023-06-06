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
package org.linagora.linshare.auth.oidc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OidcLinShareUserClaims {

	private static final String DOMAIN_DISCRIMINATOR = "domain_discriminator";

	private static final String EXTERNAL_UID = "external_uid";

	private static final String LINSHARE_ACCESS = "linshare_access";

	private static final String EMAIL = "email";

	private static final String FIRST_NAME = "first_name";
	private static final String ALT_FIRST_NAME = "name";

	private static final String LAST_NAME = "last_name";
	private static final String ALT_LAST_NAME = "family_name";

	private static final String ROLE = "linshare_role";

	private static final String LOCALE = "linshare_locale";

	private String domainDiscriminator;

	private String externalUid;

	private String email;

	private String linshareAccess;

	private String firstName;

	private String lastName;

	private String role;

	private String locale;

	private OidcLinShareUserClaims() {
	}

	public static OidcLinShareUserClaims fromAttributes(Map<String, Object> attributes) {
		Validate.notNull(attributes, "Attributes for the token should exist");
		OidcLinShareUserClaims claims = new OidcLinShareUserClaims();
		claims.domainDiscriminator = getAttribute(attributes, DOMAIN_DISCRIMINATOR);
		claims.externalUid = getAttribute(attributes, EXTERNAL_UID);
		claims.email = getAttribute(attributes, EMAIL);
		claims.linshareAccess = getAttribute(attributes, LINSHARE_ACCESS);
		claims.lastName = !StringUtils.isBlank(getAttribute(attributes, LAST_NAME)) ? getAttribute(attributes, LAST_NAME) : getAttribute(attributes, ALT_LAST_NAME);
		claims.firstName = !StringUtils.isBlank(getAttribute(attributes, FIRST_NAME)) ? getAttribute(attributes, FIRST_NAME) : getAttribute(attributes, ALT_FIRST_NAME);
		claims.locale = getAttribute(attributes, LOCALE);
		claims.role = getAttribute(attributes, ROLE);
		return claims;
	}

	public static OidcLinShareUserClaims fromOAuth2User(OAuth2User user) {
		Validate.notNull(user, "The user shoud not be null");
		Validate.notNull(user.getAttributes(), "The user shoud not be null");
		OidcLinShareUserClaims claims = new OidcLinShareUserClaims();
		claims.domainDiscriminator = user.getAttribute(DOMAIN_DISCRIMINATOR);
		claims.externalUid = user.getAttribute(EXTERNAL_UID);
		claims.email = user.getAttribute(EMAIL);
		claims.linshareAccess = user.getAttribute(LINSHARE_ACCESS);
		claims.firstName = !StringUtils.isBlank(user.getAttribute(FIRST_NAME)) ? user.getAttribute(FIRST_NAME) : user.getAttribute(ALT_FIRST_NAME);
		claims.lastName = !StringUtils.isBlank(user.getAttribute(LAST_NAME)) ? user.getAttribute(LAST_NAME) : user.getAttribute(ALT_LAST_NAME);
		claims.locale = user.getAttribute(LOCALE);
		claims.role = user.getAttribute(ROLE);
		return claims;
	}

	private static String getAttribute(Map<String, Object> attributes, String name) {
		Object value = attributes.get(name);
		return value != null ? value.toString() : null;
	}

	@Override
	public String toString() {
		return "OidcLinShareUserClaims{" +
				"domainDiscriminator='" + domainDiscriminator + '\'' +
				", externalUid='" + externalUid + '\'' +
				", email='" + email + '\'' +
				", linshareAccess='" + linshareAccess + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", role='" + role + '\'' +
				", locale='" + locale + '\'' +
				'}';
	}

	public String getDomainDiscriminator() {
		return domainDiscriminator;
	}

	public String getExternalUid() {
		return externalUid;
	}

	public String getEmail() {
		return email;
	}

	public String getLinshareAccess() {
		return linshareAccess;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getRole() {
		return role;
	}

	public String getLocale() {
		return locale;
	}

}
