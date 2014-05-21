package org.linagora.linshare.service;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.MailConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.ImmutableSet;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-test.xml"
		})
public class MailConfigServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(FunctionalityServiceImplTest.class);

	@Autowired
	private MailConfigService mailConfigService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private static final Set<Language> supportedLangs = ImmutableSet.of(
			Language.DEFAULT, Language.FRENCH);

	private AbstractDomain rootDomain;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlRootDomain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
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
		Assert.assertNotNull(contents);
		/*
		 * iterate over mailcontent langs, searching for all <Language,
		 * MailContentType> pair
		 */
		for (MailContentType type : MailContentType.values()) {
			for (Language lang : supportedLangs) {
				boolean found = false;

				for (MailContentLang c : contents) {
					if (c.getMailContentType() == type.toInt()
							&& c.getLanguage() == lang.toInt())
						found = true;
				}
				Assert.assertTrue(
						"Missing MailContentLang in root domain mail config : lang="
								+ lang.toString() + ";type=" + type.toString(),
						found);
			}
		}

		/*
		 * Validate mail footers
		 */
		Map<Integer, MailFooterLang> footers = current.getMailFooters();
		Assert.assertNotNull(current.getMailFooters());
		for (Language lang : supportedLangs) {
			Assert.assertNotNull(
					"Missing MailFooter in root domain mail config : lang="
							+ lang.toString(), footers.get(lang.toInt()));
		}

		Assert.assertNotNull(current.getMailLayoutHtml());
		Assert.assertNotNull(current.getMailLayoutText());
	}

	@Test
	public void testCreateMailConfig() {
		// TODO
	}

	@Test
	public void testUpdateMailConfig() {
		// TODO
	}

	@Test
	public void testDeleteMailConfig() {
		// TODO
	}
}
