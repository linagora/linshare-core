package org.linagora.linshare.repository.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml"
		})
@Transactional
public class AnonymousUrlRepositoryImplTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	
	private Account john;
	
	@Autowired
	private AnonymousUrlRepository anonymousUrlRepository;
	
	@Autowired
	private ShareEntryGroupRepository shareEntryGroupRepository;
	
	@Autowired
	private DocumentEntryService documentEntryService;

	@Autowired
	private AnonymousShareEntryRepository anonymousShareEntryRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		LoadingServiceTestDatas datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testFindAllExpiredEntries() throws BusinessException, IOException {
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.transferTo(stream, tempFile);

		DocumentEntry documentEntry = documentEntryService.create(john, john, tempFile, tempFile.getName(), "comment",
				false, null);
		assertNotNull(documentEntry);
		Contact contact = contactRepository.create(new Contact("test@mail.com"));
		Contact contact_2 = contactRepository.create(new Contact("test2@mail.com"));
		Contact contact_3 = contactRepository.create(new Contact("test3@mail.com"));
		AnonymousUrl anonymousUrl = anonymousUrlRepository.create(new AnonymousUrl("urlPath", contact));
		AnonymousUrl anonymousUrl_2 = anonymousUrlRepository.create(new AnonymousUrl("urlPath", contact_2));
		// Create 3rd anonymousUrl that will not be added to anonymousShareEntry
		anonymousUrlRepository.create(new AnonymousUrl("urlPath", contact_3));
		ShareEntryGroup shareEntryGroup = shareEntryGroupRepository.create(new ShareEntryGroup((User) john, "subject"));
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.set(Calendar.YEAR, 2019);
		anonymousShareEntryRepository
				.create(new AnonymousShareEntry(john, documentEntry.getName(), documentEntry.getComment(),
						documentEntry, anonymousUrl, documentEntry.getExpirationDate(), shareEntryGroup));
		anonymousShareEntryRepository
				.create(new AnonymousShareEntry(john, documentEntry.getName(), documentEntry.getComment(),
						documentEntry, anonymousUrl_2, documentEntry.getExpirationDate(), shareEntryGroup));
		assertEquals(anonymousUrlRepository.findAll().size(), 3);
		List<String> uuids = anonymousUrlRepository.findAllExpiredEntries();
		assertEquals(uuids.size(), 1);
	}

}
