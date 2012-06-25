package org.linagora.linshare.repository.hibernate;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class AccountRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	
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
	private AbstractDomainRepository abstractDomainRepository;

	@Test
	public void testCreateInternalUser() throws BusinessException{
		AbstractDomain domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		
		User u = new Internal( FIRST_NAME, LAST_NAME, MAIL, UID);
		String uid = UID + "/" + DOMAIN_IDENTIFIER;
		u.setLsUid(uid);
		u.setDomain(domain);
		
		accountRepository.create(u);
	}
	
}
