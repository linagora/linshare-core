/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.services.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linagora.linShare.core.domain.entities.Cookie;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.CookieRepository;
import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.PersistentTokenBasedRememberMeServices;

/**
 * Service extending PersistentTokenBasedRememberMeServices to allow linshare
 * to delete the old cookie in database after user logout.
 * 
 * @author sduprey
 */
public class LinshareRememberMeServices extends
		PersistentTokenBasedRememberMeServices {

	private CookieRepository cookieRepository;

	public LinshareRememberMeServices(CookieRepository cookieRepository)
			throws Exception {
		super();
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
