/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailContentLangRepository;
import org.linagora.linshare.core.repository.MailContentRepository;
import org.linagora.linshare.core.repository.MailFooterLangRepository;
import org.linagora.linshare.core.repository.MailFooterRepository;
import org.linagora.linshare.core.repository.MailLayoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class MailConfigServiceImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(FunctionalityServiceImplTest.class);

	@Autowired
	private MailContentRepository mailContentRepository;

	@Autowired
	private MailContentLangRepository mailContentLangRepository;

	@Autowired
	private MailFooterRepository mailFooterRepository;

	@Autowired
	private MailFooterLangRepository mailFooterLangRepository;

	@Autowired
	private MailLayoutRepository mailLayoutRepository;

	private static int NB_LANG = 3;

	private static int NB_CONTENT = 40;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private static final Set<Language> supportedLangs = ImmutableSet.of(
			Language.ENGLISH, Language.FRENCH);

	private AbstractDomain rootDomain;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlRootDomain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	/**
	 * Mail configurations are duplicated from root domain when created. As
	 * such, this mail configuration should is the reference.
	 */
	@Test
	public void testMailConfigDataIntegrity() {
		MailConfig current = rootDomain.getCurrentMailConfiguration();

		/*
		 * Validate mail contents
		 */
		Set<MailContentLang> contents = current.getMailContentLangs();
		Assertions.assertNotNull(contents);
		/*
		 * iterate over mailcontent langs, searching for all <Language,
		 * MailContentType> pair
		 */
		for (MailContentType type : getMailContentTypes()) {
			for (Language lang : supportedLangs) {
				boolean found = false;

				for (MailContentLang c : contents) {
					if (c.getMailContentType() == type.toInt()
							&& c.getLanguage() == lang.toInt())
						found = true;
				}
				Assertions.assertTrue(found, "Missing MailContentLang in root domain mail config : lang="
						+ lang.toString() + ";type=" + type.toString());
			}
		}

		/*
		 * Validate mail footers
		 */
		Map<Integer, MailFooterLang> footers = current.getMailFooters();
		Assertions.assertNotNull(current.getMailFooters());
		for (Language lang : supportedLangs) {
			Assertions.assertNotNull(footers.get(lang.toInt()), "Missing MailFooter in root domain mail config : lang="
					+ lang.toString());
		}

		Assertions.assertNotNull(current.getMailLayoutHtml());
	}

	@Test
	public void testMailLayoutCount() {
		List<MailLayout> findAll = mailLayoutRepository.findAll();
		Assertions.assertEquals(1, findAll.size());
	}

	@Test
	public void testMailContentCount() {
		List<MailContent> findAll = mailContentRepository.findAll();
		Assertions.assertEquals(NB_CONTENT, findAll.size());
	}

	@Test
	public void testMailContentLangCount() {
		List<MailContentLang> findAll = mailContentLangRepository.findAll();
		Assertions.assertEquals(NB_LANG * NB_CONTENT, findAll.size());
	}

	@Test
	public void testMailFooterCount() {
		List<MailFooter> findAll = mailFooterRepository.findAll();
		Assertions.assertEquals(1, findAll.size());
	}

	@Test
	public void testMailFooterLangCount() {
		List<MailFooterLang> findAll = mailFooterLangRepository.findAll();
		Assertions.assertEquals(NB_LANG, findAll.size());
	}

	private List<MailContentType> getMailContentTypes() {
		MailContentType[] list = MailContentType.values();
		List<MailContentType> values = Lists.newArrayList();
		for (int i = 0; i < list.length; i++) {
			MailContentType mailContentType = list[i];
			if (!values.contains(mailContentType)) {
				values.add(mailContentType);
			}
		}
		return values;
	}

	@Test
	public void testCreateMailConfig() {
		// TODO implement tests
	}

	@Test
	public void testUpdateMailConfig() {
		// TODO implement tests
	}

	@Test
	public void testDeleteMailConfig() {
		// TODO implement tests
	}
}
