package org.linagora.linshare.service;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailContentType;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.MailConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

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
@Ignore
public class MailConfigServiceImplTest extends AbstractJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(FunctionalityServiceImplTest.class);

	@Autowired
	private RootUserRepository rootUserRepository;

	@Autowired
	private MailConfigService mailConfigService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private User actor;

	private AbstractDomain rootDomain;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		actor = rootUserRepository
				.findByLsUuid("root@localhost.localdomain@test");
		rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.rootDomainName);
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
		Set<MailContentLang> contents = current.getMailContents();
		Assert.assertNotNull(contents);
		/*
		 * iterate over mailcontent langs, searching for all <Language,
		 * MailContentType> pair
		 */
		for (MailContentType type : MailContentType.values()) {
			for (Language lang : Language.values()) {
				boolean found = false;

				for (MailContentLang c : contents) {
					if (c.getMailContentType() == type.toInt()
							&& c.getLanguage() == lang.toInt())
						found = true;
				}
				Assert.assertFalse(
						"Missing MailContentLang in root domain mail config : "
								+ lang.toString() + " " + type.toString(),
						found);
			}
		}

		/*
		 * Validate mail footers
		 */
		Map<Integer, MailFooterLang> footers = current.getMailFooters();
		Assert.assertNotNull(current.getMailFooters());
		for (Language lang : Language.values()) {
			Assert.assertNotNull(
					"Missing MailFooter in root domain mail config : "
							+ lang.toString(), footers.get(lang.toInt()));
		}

		Assert.assertNotNull(current.getMailLayoutHtml());
		Assert.assertNotNull(current.getMailLayoutText());
	}

	@Test
	@DirtiesContext
	public void testCreateMailConfig() {
		// TODO
	}

	@Test
	@DirtiesContext
	public void testUpdateMailConfig() {
		// TODO
	}

	@Test
	@DirtiesContext
	public void testDeleteMailConfig() {
		// TODO
	}
}
