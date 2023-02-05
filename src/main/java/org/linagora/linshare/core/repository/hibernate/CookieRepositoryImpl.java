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
package org.linagora.linshare.core.repository.hibernate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Cookie;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.CookieRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class CookieRepositoryImpl extends AbstractRepositoryImpl<Cookie>
		implements CookieRepository, PersistentTokenRepository {

	public CookieRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	protected DetachedCriteria getNaturalKeyCriteria(Cookie entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Cookie.class).add(
				Restrictions.eq("identifier", entity.getIdentifier()));
		return det;
	}

	public Cookie findById(String identifier) {
		List<Cookie> cookies = findByCriteria(Restrictions.eq("identifier", identifier));
		
		if (cookies == null || cookies.isEmpty()) {
			return null;
		} else if (cookies.size() == 1) {
			return cookies.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.ui.rememberme.PersistentTokenRepository#createNewToken(org.springframework.security.ui.rememberme.PersistentRememberMeToken)
	 */
	public void createNewToken(PersistentRememberMeToken token) {
		Cookie cookie = new Cookie();
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(token.getDate());
		
		cookie.setLastUse(calendar);
		cookie.setIdentifier(token.getSeries());
		cookie.setUserName(token.getUsername());
		cookie.setValue(token.getTokenValue());
		
		try {
			create(cookie);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.ui.rememberme.PersistentTokenRepository#getTokenForSeries(java.lang.String)
	 */
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		Cookie cookie = findById(seriesId);
		
		if (cookie == null)
			return null;
		
		PersistentRememberMeToken token = new PersistentRememberMeToken(cookie
				.getUserName(), cookie.getIdentifier(), cookie.getValue(),
				cookie.getLastUse().getTime());
		
		return token;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.ui.rememberme.PersistentTokenRepository#removeUserTokens(java.lang.String)
	 */
	public void removeUserTokens(String username) {
		List<Cookie> cookies = findByUserName(username);
		
		for (Cookie cookie : cookies) {
			try {
				delete(cookie);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.ui.rememberme.PersistentTokenRepository#updateToken(java.lang.String, java.lang.String, java.util.Date)
	 */
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		Cookie cookie = findById(series);
		
		if (cookie != null) {
			cookie.setValue(tokenValue);
			
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(lastUsed);
			cookie.setLastUse(calendar);
			
			try {
				update(cookie);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Cookie> findByUserName(String userName) {
		DetachedCriteria det = DetachedCriteria.forClass(Cookie.class).add(
				Restrictions.eq("userName", userName));
		List<Cookie> cookies = findByCriteria(det);
		return cookies;
	}
}
