/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.core.service.PublicKeyService;
import org.linagora.linshare.core.utils.MongoPublicKeySigningKeyResolver;
import org.linagora.linshare.core.utils.PemRsaKeyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtServiceImpl implements JwtService {

	final private static Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

	protected String issuer;

	protected Long expiration;

	protected Long maxLifeTime;

	protected KeyPair globalKey;

	protected PublicKeyService publicKeyService;

	public JwtServiceImpl(Long expiration,
			Long maxLifeTime,
			String issuer,
			String pemPrivateKeyPath,
			String pemPublicKeyPath,
			PublicKeyService publicKeyService) 
					throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		super();
		this.expiration = expiration;
		this.issuer = issuer;
		this.maxLifeTime = maxLifeTime;
		this.globalKey = PemRsaKeyHelper.loadKeys(pemPrivateKeyPath, pemPublicKeyPath);
		this.publicKeyService = publicKeyService;
	}

	@Override
	public String generateToken(Account actor) {
		return generateToken(actor, null, null);
	}

	@Override
	public String generateToken(Account actor, String tokenUuid, Date creationDate) {
		Date createdDate = new Date();
		Date expirationDate = getExpirationDate(createdDate);

		// extra claims
		Map<String, Object> claims = new HashMap<>();
		claims.put("domain", actor.getDomainId());
		PrivateKey pk = globalKey.getPrivate();
		if (pk == null) {
			logger.error("Can not generate a JWT token. Can not read global private key.");
			throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION, "JWT private key was not set properly.");
		}
		// long time token
		if (tokenUuid != null && creationDate != null) {
			claims.put("uuid", tokenUuid);
			createdDate = creationDate;
			expirationDate = null;
 		}
		String compact = Jwts.builder()
				.setClaims(claims)
				.setSubject(actor.getMail())
				.setIssuedAt(createdDate)
				.setIssuer(issuer)
				.setExpiration(expirationDate)
				.signWith(pk, SignatureAlgorithm.RS512)
				.compact();
		return compact;
	}

	@Override
	public Claims decode(String token) {
		Jws<Claims> jws = Jwts.parser()
				.setSigningKeyResolver(new MongoPublicKeySigningKeyResolver(globalKey.getPublic(), issuer, publicKeyService))
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
