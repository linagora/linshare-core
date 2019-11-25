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
package org.linagora.linshare.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.utils.PemRsaKeyHelper;
import org.linagora.linshare.utils.LoggerParent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-test.xml" })
public class PemRsaKeyHelperTest extends LoggerParent {

	protected String pemPrivateKeyPath = "src/test/resources/sshkeys/id_rsa";

	protected String pemPublicKeyPath = "src/test/resources/sshkeys/id_rsa.pub";

	protected String pemPEMPublicKeyPath = "src/test/resources/sshkeys/id_rsa.pub.pem";

	@BeforeEach
	public void setUp() throws NoSuchAlgorithmException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void testLoadPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		RSAPrivateKey key = PemRsaKeyHelper.loadPrivateKey(pemPrivateKeyPath);
		logger.info("PrivateExponent: " + key.getPrivateExponent());
		logger.info("Modulus: " + key.getModulus());
		assertEquals(new BigInteger("8816886242975337577767310103085565725257646728918582646294672135552177074119534996738303289599076095726512360868132659521727173557508823636266500197695076759392251092847705536629203310152264563034947109698925380835444257320126964790392692740440626507638162597484329867325999733113513337983713529973799183810245961729070555446662982948847548348452014731561076755717348821893151091559163647566207413761068029509675950839872286719670842863863408176543822674947680481490754848547956092328823826239628506816318912052138147099599088390304371019473028069358690171438090352507768502768946719228607856914481935900567080631505"),
				key.getPrivateExponent());
		assertEquals(new BigInteger("22881727862268827419876299933707627645674192914629436141853004702319844331626815233090649930323314065086462622033612248329918574958953620348077362034464904984765768814475906932010774851995761401323459736599155454156437266542159778698291921915426180629275027210681128044784542213165181310396334440022685507043905640754599029562152270690290389632827478860121181639385946303970377658894299917456684973403304238258391550102226682708436567989164369705227402356982884331148889485390229524386759321826094788179888336126222875794522216456940412869264978711355179971990189887010262591388283764478422627271126312621810287603263"),
				key.getModulus());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMissingPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		RSAPrivateKey key = PemRsaKeyHelper.loadPrivateKey(pemPrivateKeyPath + "foo");
		assertEquals(null, key);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testLoadSSHPublicKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		RSAPublicKey key = PemRsaKeyHelper.loadSSHPublicKeyFromFile(pemPublicKeyPath);
		logger.info("PublicExponent: " + key.getPublicExponent());
		logger.info("Modulus: " + key.getModulus());
		assertEquals(new BigInteger("65537"), key.getPublicExponent());
		assertEquals(new BigInteger("22881727862268827419876299933707627645674192914629436141853004702319844331626815233090649930323314065086462622033612248329918574958953620348077362034464904984765768814475906932010774851995761401323459736599155454156437266542159778698291921915426180629275027210681128044784542213165181310396334440022685507043905640754599029562152270690290389632827478860121181639385946303970377658894299917456684973403304238258391550102226682708436567989164369705227402356982884331148889485390229524386759321826094788179888336126222875794522216456940412869264978711355179971990189887010262591388283764478422627271126312621810287603263"),
				key.getModulus());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testLoadPEMPublicKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		ByteSource asByteSource = Files.asByteSource(new File(pemPEMPublicKeyPath));
		RSAPublicKey pubKey = PemRsaKeyHelper.loadPEMpublicKey(asByteSource);
		assertNotNull(pubKey);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMissingPublicKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		RSAPublicKey key = PemRsaKeyHelper.loadSSHPublicKeyFromFile(pemPublicKeyPath + "foo");
		assertEquals(null, key);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testKeyPair() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		KeyPair keys = PemRsaKeyHelper.loadKeys(pemPrivateKeyPath, pemPublicKeyPath);
		assertNotNull(keys);
		assertNotNull(keys.getPrivate());
		assertNotNull(keys.getPublic());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMissingKeyPair() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		KeyPair keys = PemRsaKeyHelper.loadKeys(pemPrivateKeyPath+"foo", pemPublicKeyPath);
		assertNotNull(keys);
		assertNull(keys.getPrivate());
		assertNotNull(keys.getPublic());
		logger.info(LinShareTestConstants.END_TEST);
	}
}
