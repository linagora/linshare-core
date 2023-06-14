package org.linagora.linshare.auth.oidc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.annotation.DirtiesContext;

@ExtendWith({ MockitoExtension.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class OidcLinshareUserClaimsTest {

    @Test
    public void createFromOAuth2User(){
        OAuth2User mockedUser = mock(OAuth2User.class);
        when(mockedUser.getAttributes()).thenReturn(makeAttributes());
        OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromOAuth2User(mockedUser);

        Assertions.assertEquals("domain_discriminator", claims.getDomainDiscriminator());
        Assertions.assertEquals("external_uid", claims.getExternalUid());
        Assertions.assertEquals("linshare_access", claims.getLinshareAccess());
        Assertions.assertEquals("email", claims.getEmail());
        Assertions.assertEquals("first_name", claims.getFirstName());
        Assertions.assertEquals("last_name", claims.getLastName());
        Assertions.assertEquals("linshare_role", claims.getRole());
        Assertions.assertEquals("linshare_locale", claims.getLocale());
    }

    @Test
    public void createFromAttributes(){
        OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromAttributes(makeAttributes());

        Assertions.assertEquals("domain_discriminator", claims.getDomainDiscriminator());
        Assertions.assertEquals("external_uid", claims.getExternalUid());
        Assertions.assertEquals("linshare_access", claims.getLinshareAccess());
        Assertions.assertEquals("email", claims.getEmail());
        Assertions.assertEquals("first_name", claims.getFirstName());
        Assertions.assertEquals("last_name", claims.getLastName());
        Assertions.assertEquals("linshare_role", claims.getRole());
        Assertions.assertEquals("linshare_locale", claims.getLocale());
    }

    @Test
    public void createFromAttributesWithList(){
        OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromAttributes(makeAttributesWithList());

        Assertions.assertEquals("domain_discriminator", claims.getDomainDiscriminator());
        Assertions.assertEquals("external_uid", claims.getExternalUid());
        Assertions.assertEquals("linshare_access", claims.getLinshareAccess());
        Assertions.assertEquals("email", claims.getEmail());
        Assertions.assertEquals("first_name", claims.getFirstName());
        Assertions.assertEquals("last_name", claims.getLastName());
        Assertions.assertEquals("linshare_role", claims.getRole());
        Assertions.assertEquals("linshare_locale", claims.getLocale());
    }

    @Test
    public void createFromAltAttributes(){
        OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromAttributes(makeAltAttributes());

        Assertions.assertEquals("domain_discriminator", claims.getDomainDiscriminator());
        Assertions.assertEquals("external_uid", claims.getExternalUid());
        Assertions.assertEquals("linshare_access", claims.getLinshareAccess());
        Assertions.assertEquals("email", claims.getEmail());
        Assertions.assertEquals("first_name", claims.getFirstName());
        Assertions.assertEquals("last_name", claims.getLastName());
        Assertions.assertEquals("linshare_role", claims.getRole());
        Assertions.assertEquals("linshare_locale", claims.getLocale());
    }

    private Map<String, Object> makeAttributesWithList() {
        return new HashMap<String, Object>() {{
            put("domain_discriminator", List.of("domain_discriminator"));
            put("external_uid", "external_uid");
            put("linshare_access", "linshare_access");
            put("email", "email");
            put("first_name", "first_name");
            put("last_name", "last_name");
            put("linshare_role", "linshare_role");
            put("linshare_locale", "linshare_locale");
        }};
    }
    private Map<String, Object> makeAttributes() {
        return new HashMap<String, Object>() {{
            put("domain_discriminator", "domain_discriminator");
            put("external_uid", "external_uid");
            put("linshare_access", "linshare_access");
            put("email", "email");
            put("first_name", "first_name");
            put("last_name", "last_name");
            put("linshare_role", "linshare_role");
            put("linshare_locale", "linshare_locale");
        }};
    }
    private Map<String, Object> makeAltAttributes() {
        return new HashMap<String, Object>() {{
            put("domain_discriminator", "domain_discriminator");
            put("external_uid", "external_uid");
            put("linshare_access", "linshare_access");
            put("email", "email");
            put("name", "first_name");
            put("family_name", "last_name");
            put("linshare_role", "linshare_role");
            put("linshare_locale", "linshare_locale");
        }};
    }
}
