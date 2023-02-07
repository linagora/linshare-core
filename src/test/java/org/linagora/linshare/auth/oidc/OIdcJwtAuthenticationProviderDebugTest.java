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

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

// Only for researching purpose
class OIdcJwtAuthenticationProviderDebugTest {

	OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDetailsFactoryMock = Mockito.mock(OidcAuthenticationTokenDetailsFactory.class);

	private String issuerUrl = "https://login.microsoftonline.com/d6eca270-8f2c-4ae6-af9a-cb097502469a/v2.0";

	private OIdcJwtAuthenticationProvider subj =
			new OIdcJwtAuthenticationProvider(oidcAuthenticationTokenDetailsFactoryMock, issuerUrl, true);

	private String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ii1LSTNROW5OUjdiUm9meG1lWm9YcWJIWkdldyJ9.eyJhdWQiOiI2NjQwOTgyYy02ZGM4LTQxOTMtYWQ3MC03NTU2N2YyMGM1ZTMiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vZDZlY2EyNzAtOGYyYy00YWU2LWFmOWEtY2IwOTc1MDI0NjlhL3YyLjAiLCJpYXQiOjE2NzU3NzQyMjYsIm5iZiI6MTY3NTc3NDIyNiwiZXhwIjoxNjc1Nzc4Mzc1LCJhaW8iOiJBVFFBeS84VEFBQUFoQWJGTHdWbXhaeWt0RDNGYWp0ZGtCdk5WaUtNNFhhYko4WlRwRFhjdExhcHM1ckdSUXl1SXpwWE1FK3orU2gyIiwiYXpwIjoiNjY0MDk4MmMtNmRjOC00MTkzLWFkNzAtNzU1NjdmMjBjNWUzIiwiYXpwYWNyIjoiMCIsIm9pZCI6ImUxNDRlYTQzLTJhYWUtNDM0NC1hNmIwLTE5ZTk0ZGYzZGIwOSIsInByZWZlcnJlZF91c2VybmFtZSI6ImFudG9uQHdib3VkaWNoZWxpbmFnb3JhLm9ubWljcm9zb2Z0LmNvbSIsInJoIjoiMC5BVTRBY0tMczFpeVA1a3F2bXNzSmRRSkdtaXlZUUdiSWJaTkJyWEIxVm44Z3hlT0RBRlEuIiwic2NwIjoibGluc2hhcmUtc2NvcGUiLCJzdWIiOiJaNnFaZ3RvbkVJUWQxWWRPTlpDTFJ4SVJxc3FIcTY4c3haNEE5ZGZ6NTBnIiwidGlkIjoiZDZlY2EyNzAtOGYyYy00YWU2LWFmOWEtY2IwOTc1MDI0NjlhIiwidXRpIjoiY2lpMlI5YjEwRU9NN1RKRnVuSTBBUSIsInZlciI6IjIuMCIsImRvbWFpbl9kaXNjcmltaW5hdG9yIjoiQURMaW5TaGFyZURvbWFpbiIsImxpbnNoYXJlX2FjY2VzcyI6ImxpbnNoYXJlIiwibGluc2hhcmVfbG9jYWxlIjoiRU5HTElTSCIsImxpbnNoYXJlX3JvbGUiOiJBRE1JTiIsImZpcnN0X25hbWUiOiJBbnRvbiIsImxhc3RfbmFtZSI6IlNoZXBpbG92In0.mlKz7fJYlYK-xy8gGr-M3W9sQ6q51s-W4kpSiAfK2yADD7QepWGgJ7z6crISZN60AHC3PGcH8WUmSaXXVh7vp2ZEVVtlWR_DW2jksGWzk1JO5Ii2MbydKrF6DO8W-qls_GFm1OoObVU_ubiQ7drFK_sq0VQnUBnIWKoeY6clfhXynnHB3vwkI3LA7ySmgCkdIzj30aY9rRnGYD9eOrlAByV8nqnUdn9TJgo0IumIwX4PWu8oMMfF-8Kj_8IZrTYsHqAdekwtWwuZCWAggGW7zJCikynY80Yp-l5dCz7BN-Y1CEVgp2-uZPixy4Zjt_umR-D-iWwFOrr6rXppaf3EpQ";

	private String idToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ii1LSTNROW5OUjdiUm9meG1lWm9YcWJIWkdldyJ9.eyJhdWQiOiI2NjQwOTgyYy02ZGM4LTQxOTMtYWQ3MC03NTU2N2YyMGM1ZTMiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vZDZlY2EyNzAtOGYyYy00YWU2LWFmOWEtY2IwOTc1MDI0NjlhL3YyLjAiLCJpYXQiOjE2NzU3NzQyMjYsIm5iZiI6MTY3NTc3NDIyNiwiZXhwIjoxNjc1Nzc4MTI2LCJlbWFpbCI6ImFiYmV5LmN1cnJ5QGxpbnNoYXJlLm9yZyIsIm9pZCI6ImUxNDRlYTQzLTJhYWUtNDM0NC1hNmIwLTE5ZTk0ZGYzZGIwOSIsInByZWZlcnJlZF91c2VybmFtZSI6ImFudG9uQHdib3VkaWNoZWxpbmFnb3JhLm9ubWljcm9zb2Z0LmNvbSIsInJoIjoiMC5BVTRBY0tMczFpeVA1a3F2bXNzSmRRSkdtaXlZUUdiSWJaTkJyWEIxVm44Z3hlT0RBRlEuIiwic3ViIjoiWjZxWmd0b25FSVFkMVlkT05aQ0xSeElScXNxSHE2OHN4WjRBOWRmejUwZyIsInRpZCI6ImQ2ZWNhMjcwLThmMmMtNGFlNi1hZjlhLWNiMDk3NTAyNDY5YSIsInV0aSI6ImNpaTJSOWIxMEVPTTdUSkZ1bkkwQVEiLCJ2ZXIiOiIyLjAiLCJkb21haW5fZGlzY3JpbWluYXRvciI6IkFETGluU2hhcmVEb21haW4iLCJsaW5zaGFyZV9hY2Nlc3MiOiJsaW5zaGFyZSIsImxpbnNoYXJlX2xvY2FsZSI6IkVOR0xJU0giLCJsaW5zaGFyZV9yb2xlIjoiQURNSU4iLCJmaXJzdF9uYW1lIjoiQW50b24iLCJsYXN0X25hbWUiOiJTaGVwaWxvdiJ9.aodtk6m1vok0SwNLejC1_VXoPnSdWkewq5k-ahTpPSRw_oihUcFVilVEle5KlejEfIp5fcjT2wF63DAKzWC8nFln8p9cLq3vJqHi3WmbBAzU2kG4hn6g9VGxq4XQwZ8_v5E9_XSmmy33rvfZAjkn9VboWTDptnSyQyP3ONpkkbEc3z-_lPFOC6UwhbinSjzmaZSsf_Z7LVfakv3mhHJT86Xaf5pAQe8DhQ0Oc5aF7RCDkkx20ywfwXu2UsIJUIQQoVh8F-7bfgM_npyz_YVcfGhCXx1-ylO6aP5zXNTTbGdWY2mFvhj4bcH_RHQod8L7LUC-JVQ2IEaiboN8XDYctQ";

	@Test
	@Disabled
	public void testAuthenticate() throws ParseException {
		//given jwt token
		OidcJwtAuthenticationToken token = new OidcJwtAuthenticationToken(authToken, idToken);
		JWT jwt = JWTParser.parse(authToken);

		//when
		Authentication authentication = subj.authenticate(token);

		//then
		Assertions.assertNotNull(authentication);
	}


}