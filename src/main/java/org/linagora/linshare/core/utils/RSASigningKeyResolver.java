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

public class RSASigningKeyResolver extends AbstractSigningKeyResolver {

	final private static Logger logger = LoggerFactory.getLogger(RSASigningKeyResolver.class);

	protected PublicKey extraPublicKey;

	public RSASigningKeyResolver(PublicKey globalPublicKey, String issuer, PublicKey extraPublicKey) {
		super();
		super.globalPublicKey = globalPublicKey;
		this.extraPublicKey = extraPublicKey;
		super.issuer = issuer;
	}

	@Override
	protected Key getExtraPublicKey(String issuer) {
		if (extraPublicKey == null) {
			logger.error("Can not find a public key to verify the JWT token.");
			throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION,
					"Can not find a public key to verify the JWT token.");
		}
		return extraPublicKey;
	}
}
