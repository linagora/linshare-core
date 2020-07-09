/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.PublicKeyService;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoPublicKeySigningKeyResolver extends AbstractSigningKeyResolver {

	final private static Logger logger = LoggerFactory.getLogger(MongoPublicKeySigningKeyResolver.class);

	protected PublicKeyService publicKeyService;

	public MongoPublicKeySigningKeyResolver(PublicKey globalPublicKey, String issuer,
			PublicKeyService publicKeyService) {
		super();
		super.globalPublicKey = globalPublicKey;
		super.issuer = issuer;
		this.publicKeyService = publicKeyService;
	}

	@Override
	protected Key getExtraPublicKey(String issuer) {
		PublicKeyLs publicKeyLs = publicKeyService.findByIssuer(issuer);
		if (publicKeyLs == null) {
			logger.error("unkown issuer.", issuer);
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_UNKOWN_ISSUER,
					"Can not find the stored public key to verify the jwt token.");
		}
		try {
			RSAPublicKey publicKey = null;
			if (publicKeyLs.getFormat().equals(PublicKeyFormat.SSH)) {
				publicKey = PemRsaKeyHelper.loadSSHPublicKey(publicKeyLs.getPublicKey());
			} else {
				publicKey = PemRsaKeyHelper.loadPEMpublicKey(publicKeyLs.getPublicKey());
			}
			return publicKey;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			logger.debug(e.getMessage(), e);
			logger.error("Can not load a public key ", publicKeyLs.getUuid());
			throw new BusinessException(BusinessErrorCode.INVALID_CONFIGURATION, "Can not load a public key.");
		}
	}
}
