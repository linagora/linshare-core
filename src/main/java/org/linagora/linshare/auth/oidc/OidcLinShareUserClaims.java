package org.linagora.linshare.auth.oidc;

import org.apache.commons.lang3.Validate;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OidcLinShareUserClaims {

	private static String DOMAIN_DISCRIMINATOR = "domain_discriminator";

	private static String EXTERNAL_UID = "external_uid";

	private static String LINSHARE_ACCESS = "linshare_access";

	private static String EMAIL = "email";

	private static String FIRST_NAME = "first_name";

	private static String LAST_NAME = "last_name";

	private static String ROLE = "linshare_role";

	private static String LOCALE = "linshare_locale";

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
		claims.lastName = getAttribute(attributes, LAST_NAME);
		claims.firstName = getAttribute(attributes, FIRST_NAME);
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
		claims.firstName = user.getAttribute(FIRST_NAME);
		claims.lastName = user.getAttribute(LAST_NAME);
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
