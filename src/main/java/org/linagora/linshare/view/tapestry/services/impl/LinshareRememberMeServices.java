/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.view.tapestry.services.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linagora.linshare.core.domain.entities.Cookie;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.CookieRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * Service extending PersistentTokenBasedRememberMeServices to allow linshare
 * to delete the old cookie in database after user logout.
 * 
 * @author sduprey
 */
public class LinshareRememberMeServices extends
		PersistentTokenBasedRememberMeServices {

	public LinshareRememberMeServices(String key,
			UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
	}

	private CookieRepository cookieRepository;

	public CookieRepository getCookieRepository() {
		return cookieRepository;
	}

	public void setCookieRepository(CookieRepository cookieRepository) {
		this.cookieRepository = cookieRepository;
	}

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		String httpCookieValue = null;
		javax.servlet.http.Cookie[] httpCookies = request.getCookies();

		// find the right cookie
		if (httpCookies != null && httpCookies.length > 0) {
			for (javax.servlet.http.Cookie cookie : httpCookies) {
				if (cookie.getName().equals(
						SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)) {
					httpCookieValue = cookie.getValue();
					break;
				}
			}
		}
		if (httpCookieValue != null) {
			List<Cookie> linshareCookies = null;
			
			if (authentication != null && authentication.getName() != null && authentication.getName().length() > 0) {
				linshareCookies = cookieRepository.findByUserName(authentication.getName());
			} else {
				linshareCookies = cookieRepository.findAll();
			}
			
			/*
			 * A login can match more than one cookie in database
			 * because a user can connect to linShare from multiple computer 
			 */
			if (linshareCookies != null && linshareCookies.size() > 0) {
				for (Cookie cookie : linshareCookies) {
					String[] token = new String[] { cookie.getIdentifier(), cookie.getValue() };
					String encodeDBCookie = encodeCookie(token);
					
					if (httpCookieValue.equals(encodeDBCookie)) { 
						try {
							cookieRepository.delete(cookie);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (BusinessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		/*
		 * Do the superclass logout : delete the cookie of user web browser
		 */
		super.logout(request, response, authentication);
	}
}
