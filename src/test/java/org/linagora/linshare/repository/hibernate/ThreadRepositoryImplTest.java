package org.linagora.linshare.repository.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class ThreadRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String UID = "uid";
     
    
    @Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;
    
    
	@Autowired
	@Qualifier("threadRepository")
	private ThreadRepository<Thread> threadRepository;
	
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	
	
	private AbstractDomain domain;
	
	private User internal;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal( FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setDomain(domain);
		
		accountRepository.create(internal);
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		accountRepository.delete(internal);
		logger.debug("End tearDown");
	}
	
	
	@Test
	public void testCreateThread() throws BusinessException{
		
		Thread t = new Thread(domain, internal, "myThread");
		threadRepository.create(t);
	}
	
//	@Rollback(false)
	@Test
	public void testCreateThreadAndMember() throws BusinessException{
		
		Thread t = new Thread(domain, internal, "myThread");
		threadRepository.create(t);
		
		ThreadMember m = new ThreadMember(true,true,internal,t);
		t.getMyMembers().add(m);
		threadRepository.update(t);
		
		logger.info("user id :" + internal.getId());
		logger.info("thread id :" + t.getId());
		logger.info("member id :" + m.getId());
		
		logger.debug("count : " + threadRepository.findAll().size());
		
	}
	
	
	
}
