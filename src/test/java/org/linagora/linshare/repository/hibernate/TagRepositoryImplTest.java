package org.linagora.linshare.repository.hibernate;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.TagRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;




@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class TagRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	
	private static final Logger logger = LoggerFactory.getLogger(TagRepositoryImplTest.class);
	
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private AccountRepository<Account> accountRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	
	
	private static final String LOGIN = "login";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";

    
    
	// Members
    private AbstractDomain rootDomain;
    private Account account;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		account = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true, "comment");
		account.setDomain(rootDomain);
		account.setCreationDate(new Date());
		account.setLocale(rootDomain.getDefaultLocale());
		accountRepository.create(account);
		
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		
		accountRepository.delete(account);
		
		logger.debug("End tearDown");
	}
	
	
	@Test
	public void testCreateTag() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Tag tag = new Tag(account, "toto");
		
		tagRepository.create(tag);
		tagRepository.delete(tag);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testFindTag() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Tag tag = new Tag(account, "toto");
		
		tagRepository.create(tag);
		
		Assert.assertNull(tagRepository.findByOwnerAndName(account, "tota"));
		Assert.assertNotNull(tagRepository.findByOwnerAndName(account, "toto"));
		
		tagRepository.delete(tag);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
}
