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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.jclouds.crypto.Pems;
import org.jclouds.ssh.SshKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemRsaKeyHelper {

	final private static Logger logger = LoggerFactory.getLogger(PemRsaKeyHelper.class);

	public static RSAPrivateKey loadPrivateKey(String pemPrivateKeyPath) throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		String pem = loadPemKey(pemPrivateKeyPath);
		if (pem == null) {
			logger.info("Private key '" + pemPrivateKeyPath + "' was not found. (check read access)");
			return null;
		}
		KeySpec privateKeySpec = Pems.privateKeySpec(pem);
		RSAPrivateKey privatevKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		return privatevKey;
	}

	public static RSAPublicKey loadPublicKey(String pemPublicKeyPath) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		String pem = loadPemKey(pemPublicKeyPath);
		if (pem == null) {
			logger.info("Public key '" + pemPublicKeyPath + "' was not found. (check read access)");
			return null;
		}
		RSAPublicKeySpec publicKeySpec = SshKeys.publicKeySpecFromOpenSSH(pem);
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}

	public static KeyPair loadKeys(String pemPrivateKeyPath, String pemPublicKeyPath) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		RSAPrivateKey privatevKey = PemRsaKeyHelper.loadPrivateKey(pemPrivateKeyPath);
		RSAPublicKey publicKey = PemRsaKeyHelper.loadPublicKey(pemPublicKeyPath);
		KeyPair kp = new KeyPair(publicKey, privatevKey);
		return kp;
	}

	protected static String loadPemKey(String pemKeyPath) {
		try (BufferedReader in = new BufferedReader(new FileReader(pemKeyPath))) {
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = in.readLine()) != null) {
				sb.append(str + '\n');
			}
			String pem = sb.toString();
			logger.debug("loadPemKey : " + pem);
			return pem;
		} catch (IOException e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

}
