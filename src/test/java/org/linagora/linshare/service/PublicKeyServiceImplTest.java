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

package org.linagora.linshare.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.PublicKeyService;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
				"classpath:springContext-datasource.xml",
				"classpath:springContext-repository.xml",
				"classpath:springContext-dao.xml",
				"classpath:springContext-service.xml",
				"classpath:springContext-business-service.xml",
				"classpath:springContext-rac.xml",
				"classpath:springContext-mongo-java-server.xml",
				"classpath:springContext-storage-jcloud.xml",
				"classpath:springContext-service-miscellaneous.xml",
				"classpath:springContext-test.xml",
				"classpath:springContext-ldap.xml" })
@Transactional
public class PublicKeyServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(PublicKeyServiceImplTest.class);

	@Autowired
	private PublicKeyService publicKeyService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private Account root;

	private Account jane;

	private AbstractDomain rootDomain;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		rootDomain = root.getDomain();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreatePublicKey() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("New application");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		Assertions.assertNotNull(pubKey);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreatePEMPublicKey() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initPEMPublicKeys();
		pubKey.setIssuer("app with pem public key");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		Assertions.assertNotNull(pubKey);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDuplicatePublicKey() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("app for key test duplication");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		try {
			PublicKeyLs pubKey2 = initSSHPublicKeys();
			pubKey2.setIssuer("app for key test duplication");
			pubKey2 = publicKeyService.create(root, pubKey2, rootDomain);
			Assertions.assertNotNull(pubKey2);
		} catch (BusinessException ex) {
			Assertions.assertTrue(ex.getErrorCode().equals(BusinessErrorCode.PUBLIC_KEY_ALREADY_EXIST));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateWithInvalidPublicKey() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("My app");
		pubKey.setPublicKey("Invalid public key format");
		try {
			pubKey = publicKeyService.create(root, pubKey, rootDomain);
			Assertions.assertNotNull(pubKey);
		} catch (BusinessException ex) {
			Assertions.assertTrue(ex.getErrorCode().equals(BusinessErrorCode.PUBLIC_KEY_INVALID_FORMAT));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindPublicKeyByUuid() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("New Application");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		Assertions.assertNotNull(pubKey);
		PublicKeyLs found = publicKeyService.find(root, pubKey.getUuid());
		Assertions.assertNotNull(found);
		Assertions.assertEquals(pubKey.getUuid(), found.getUuid());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbiddenFindPublicKeyByUuid() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		try {
			PublicKeyLs pubKey = initSSHPublicKeys();
			pubKey.setIssuer("application test");
			pubKey = publicKeyService.create(root, pubKey, rootDomain);
			Assertions.assertNotNull(pubKey);
			publicKeyService.find(jane, pubKey.getUuid());
		} catch (BusinessException ex) {
			Assertions.assertTrue(ex.getErrorCode().equals(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindPublicKeyByDomain() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		List<PublicKeyLs> oldPublicKeys = publicKeyService.findAll(root, rootDomain);
		int oldSize = oldPublicKeys.size();
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("Application");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		PublicKeyLs pubKey2 = initSSHPublicKeys();
		pubKey2.setIssuer("General Application");
		pubKey2 = publicKeyService.create(root, pubKey2, rootDomain);
		Assertions.assertNotNull(pubKey);
		Assertions.assertNotNull(pubKey2);
		List<PublicKeyLs> publicKeys = publicKeyService.findAll(root, rootDomain);
		Assertions.assertEquals(oldSize + 2, publicKeys.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeletePublicKey() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("linshare");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		Assertions.assertNotNull(pubKey);
		PublicKeyLs found = publicKeyService.find(root, pubKey.getUuid());
		Assertions.assertNotNull(found);
		publicKeyService.delete(root, found);
		try {
			publicKeyService.find(root, found.getUuid());
		} catch (BusinessException ex) {
			Assertions.assertTrue(ex.getErrorCode().equals(BusinessErrorCode.PUBLIC_KEY_NOT_FOUND));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindByIssuer() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		PublicKeyLs pubKey = initSSHPublicKeys();
		pubKey.setIssuer("OpenPass");
		pubKey = publicKeyService.create(root, pubKey, rootDomain);
		Assertions.assertNotNull(pubKey);
		PublicKeyLs found = publicKeyService.findByIssuer("OpenPass");
		Assertions.assertNotNull(found);
		Assertions.assertEquals("OpenPass", found.getIssuer());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private PublicKeyLs initSSHPublicKeys() {
		PublicKeyLs publicKeyLs = new PublicKeyLs();
		publicKeyLs.setDomainUuid(rootDomain.getUuid());
		publicKeyLs.setFormat(PublicKeyFormat.SSH);
		publicKeyLs.setPublicKey(
				"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDG3WzeguQTjxpS9GpYnmLRyrRWZUV1BEXDjYR6uZSlTFcwVVfQnKigf0WOJsK2xIJ8dWbI8+75vDNJ1binQ38qaytAUyIDC7a89r+0T7R9MhWcxW1B7B3dkwaOM+HR1lGnMBxi7WTz86DxhRDdhKgCCsGtQex3ZDTyEhwlvV0qvj/HQhqCEuYSuok+C+eWrYbzlDK2y5CODPiV8vclwoVZ4pbq0UZcqt9WfldVdtGijaNqtCTKZXWtvDCC2rURyWsUfgYs0UHg1gvD+PA07/2GhcmFwv6Ap4LuliTbsWSQfZu2/05U5INmqWpOrm3oxXzppm1hs7UNGaWVYN82/XJp fred@fredarc");
		return publicKeyLs;
	}

	private PublicKeyLs initPEMPublicKeys() {
		PublicKeyLs publicKeyLs = new PublicKeyLs();
		publicKeyLs.setDomainUuid(rootDomain.getUuid());
		publicKeyLs.setFormat(PublicKeyFormat.PEM);
		publicKeyLs.setPublicKey("-----BEGIN PUBLIC KEY-----\n" +
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtUIZXEawBZ6GEZlNcLEf\n" +
				"yekEkd944Hn+kmEAG0awY0raGoKgm/Dj9e+aqWgF2xkiZfRWxYrpXkHU7mnqK3FJ\n" +
				"GsoNZ2tk6pxFH4JkiGtocHYwX1lsYkNr95StX0zyV3a1psTeoTqaXxblqx7tNvc4\n" +
				"oD2HW6NIMlgBL3yGFppMa2s6tk+LwsrPRrHDYVc0t6RrN0h6ZvkFCZAZ98INHD6K\n" +
				"ZoD2wVauviYRObgeoLMv1MJ1NVZjUvdXlMEojzbpSgpmkttfmEtgO9oGlmyNpe8s\n" +
				"7D7Pi+FUcHTMplaTU46EkVPGaNqGGgnbh+6ixboHJEr2W6BovXCk4lXuSdEXoNNu\n" +
				"PwIDAQAB\n" +
				"-----END PUBLIC KEY-----\n" +
				"");
		return publicKeyLs;
	}
}
