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

package org.linagora.linshare.core.utils;

import java.security.Key;
import java.security.PublicKey;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.lang.Assert;

public abstract class AbstractSigningKeyResolver implements SigningKeyResolver {

	final private static Logger logger = LoggerFactory.getLogger(AbstractSigningKeyResolver.class);

	protected PublicKey globalPublicKey;

	protected String issuer;

	protected abstract Key getExtraPublicKey(String issuer);

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
		SignatureAlgorithm alg = SignatureAlgorithm.forName(header.getAlgorithm());
		logger.debug("JwsHeader : " + header.toString());
		Assert.isTrue(alg.isRsa(), "The resolveSigningKey(JwsHeader, Claims) implementation can be "
				+ "used only for asymmetric key algorithms (RSA). ");
		String issuer = claims.getIssuer();
		Assert.hasText(issuer);
		if (issuer.equals(this.issuer)) {
			if (globalPublicKey == null) {
				logger.error("Can not find global public key to verify the JWT token.");
				throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION,
						"Can not find global public key to verify the JWT token.");
			}
			return globalPublicKey;
		}
		return getExtraPublicKey(issuer);
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, String plaintext) {
		logger.error("Format plaintext unsupported, you need to use json format.");
		throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION,
				"Format plaintext unsupported, you need to use json format.");
	}
}
