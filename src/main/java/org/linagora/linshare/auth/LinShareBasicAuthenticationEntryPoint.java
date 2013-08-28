package org.linagora.linshare.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class LinShareBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		String authenticationHeader = request.getHeader("WWW-No-Authenticate");
		if(authenticationHeader == null) {
			response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.getRealmName() + "\"");
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}
}
