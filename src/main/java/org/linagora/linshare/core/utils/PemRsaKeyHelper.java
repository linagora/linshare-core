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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.jclouds.crypto.Pems;
import org.jclouds.ssh.SshKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

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
		logger.info("Private key '" + pemPrivateKeyPath + "' was loaded");
		return privatevKey;
	}

	public static RSAPublicKey loadSSHPublicKeyFromFile(String pemPublicKeyPath)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		String pem = loadPemKey(pemPublicKeyPath);
		if (pem == null) {
			logger.info("Public key '" + pemPublicKeyPath + "' was not found. (check read access)");
			return null;
		}
		RSAPublicKey publicKey = null;
		if (pem.startsWith("ssh-rsa")) {
			publicKey = loadSSHPublicKey(pem);
		} else {
			publicKey = loadPEMpublicKey(pem);
		}
		logger.info("Public key '" + pemPublicKeyPath + "' was loaded");
		return publicKey;
	}

	public static RSAPublicKey loadSSHPublicKey(String publicKey) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec publicKeySpec = SshKeys.publicKeySpecFromOpenSSH(publicKey);
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
		return key;
	}

	public static KeyPair loadKeys(String pemPrivateKeyPath, String pemPublicKeyPath) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		RSAPrivateKey privatevKey = PemRsaKeyHelper.loadPrivateKey(pemPrivateKeyPath);
		PublicKey publicKey = PemRsaKeyHelper.loadSSHPublicKeyFromFile(pemPublicKeyPath);
		KeyPair kp = new KeyPair(publicKey, privatevKey);
		return kp;
	}

	public static RSAPublicKey loadPEMpublicKey(String pem) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		ByteSource asByteSource = ByteSource.wrap(pem.getBytes(Charsets.UTF_8));
		return loadPEMpublicKey(asByteSource);
	}

	public static RSAPublicKey loadPEMpublicKey(ByteSource asByteSource) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		KeySpec ks = Pems.publicKeySpec(asByteSource);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(ks);
		return pubKey;
	}
	
	protected static String loadPemKey(String pemKeyPath) {
		try (BufferedReader in = new BufferedReader(new FileReader(pemKeyPath))) {
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = in.readLine()) != null) {
				sb.append(str + '\n');
			}
			String pem = sb.toString();
			logger.debug("loadPemKey : " + pem.substring(0, 100));
			if (logger.isTraceEnabled()) {
				logger.trace("loadPemKey : " + pem);
			}
			return pem;
		} catch (IOException e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}
}
