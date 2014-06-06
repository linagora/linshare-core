package org.linagora.linshare.service;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.service.MailConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.ImmutableSet;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml" })
public class MailConfigServiceImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(FunctionalityServiceImplTest.class);

	@Autowired
	private MailConfigService mailConfigService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private static final Set<Language> supportedLangs = ImmutableSet.of(
			Language.ENGLISH, Language.FRENCH);

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
		for (final MailContentType type : MailContentType.values()) {
			for (final Language lang : supportedLangs) {
				boolean empty = CollectionUtils.select(contents,
						new Predicate() {

							@Override
							public boolean evaluate(Object arg0) {
								MailContentLang m = (MailContentLang) arg0;
								return m.getLanguage() == lang.toInt()
										&& m.getMailContentType() == type
												.toInt();
							}
						}).isEmpty();
				Assert.assertFalse(
						"Missing MailContentLang in root domain mail config : lang="
								+ lang.toString() + ";type=" + type.toString(),
						empty);
			}
		}

		/*
		 * Validate mail footers
		 */
		Set<MailFooterLang> footers = current.getMailFooterLangs();
		Assert.assertNotNull(footers);
		for (final Language lang : supportedLangs) {
			boolean empty = CollectionUtils.select(footers, new Predicate() {

				@Override
				public boolean evaluate(Object arg0) {
					MailFooterLang m = (MailFooterLang) arg0;
					return m.getLanguage() == lang.toInt();
				}
			}).isEmpty();
			Assert.assertFalse(
					"Missing MailFooter in root domain mail config : lang="
							+ lang.toString(), empty);
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
