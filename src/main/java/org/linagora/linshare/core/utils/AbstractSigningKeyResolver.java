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
