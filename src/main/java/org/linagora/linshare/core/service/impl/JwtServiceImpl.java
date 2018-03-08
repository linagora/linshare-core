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

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.core.utils.PemRsaKeyHelper;
import org.linagora.linshare.core.utils.RSASigningKeyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

public class JwtServiceImpl implements JwtService {

	final private static Logger logger = LoggerFactory
			.getLogger(JwtServiceImpl.class);

	protected Clock clock = DefaultClock.INSTANCE;

	protected String issuer;

	protected Long expiration;

	protected Long maxLifeTime;

	protected KeyPair globalKey;

	protected RSAPublicKey extraPublicKey;

	public JwtServiceImpl(Long expiration, Long maxLifeTime, String issuer, String pemPrivateKeyPath,
			String pemPublicKeyPath, String pemExtraPublicKeyPath)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		super();
		this.expiration = expiration;
		this.issuer = issuer;
		this.maxLifeTime = maxLifeTime;
		this.globalKey = PemRsaKeyHelper.loadKeys(pemPrivateKeyPath, pemPublicKeyPath);
		this.extraPublicKey = PemRsaKeyHelper.loadPublicKey(pemExtraPublicKeyPath);
	}

	@Override
	public String generateToken(Account actor) {
		final Date createdDate = clock.now();
		final Date expirationDate = getExpirationDate(createdDate);

		// extra claims
		Map<String, Object> claims = new HashMap<>();
		claims.put("domain", actor.getDomainId());

		PrivateKey pk = globalKey.getPrivate();
		if (pk == null) {
			logger.error("Can not generate a JWT toekn. Can not read global private key.");
			throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION, "JWT private key was not set properly.");
		}
		String compact = Jwts.builder()
				.setClaims(claims)
				.setSubject(actor.getMail())
				.setIssuedAt(createdDate)
				.setIssuer(issuer)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.RS512, pk)
				.compact();
		return compact;
	}

	@Override
	public Claims decode(String token) {
		Jws<Claims> jws = Jwts.parser()
				.setSigningKeyResolver(new RSASigningKeyResolver(globalKey.getPublic(), extraPublicKey, issuer))
				.parseClaimsJws(token);
		Claims claims = jws.getBody();
		return claims;
	}

	@Override
	public boolean hasValidLiveTime(Claims claims) {
		Date issuedAt = claims.getIssuedAt();
		Date expireAt = claims.getExpiration();
		long duration = expireAt.getTime() - issuedAt.getTime();
		if (duration <= 0) {
			return false;
		}
		if (duration > maxLifeTime * 1000) {
			return false;
		}
		return true;
	}

	private Date getExpirationDate(Date fromDate) {
		return new Date(fromDate.getTime() + expiration * 1000);
	}
}
