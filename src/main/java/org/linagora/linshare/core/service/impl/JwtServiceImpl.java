/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.service.JwtService;

import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

public class JwtServiceImpl implements JwtService {

	protected Clock clock = DefaultClock.INSTANCE;

	protected String secret;

	protected Long expiration;

	public JwtServiceImpl(String secret, Long expiration) {
		super();
		this.secret = secret;
		this.expiration = expiration;
	}

	@Override
	public String generateToken(Account actor) {
		final Date createdDate = clock.now();
		final Date expirationDate = getExpirationDate(createdDate);

		// extra claims
		Map<String, Object> claims = new HashMap<>();
		claims.put("linshareDomain", actor.getDomainId());
		claims.put("accountUuid", actor.getLsUuid());

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(actor.getMail())
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	private Date getExpirationDate(Date fromDate) {
		return new Date(fromDate.getTime() + expiration * 1000);
	}
}
