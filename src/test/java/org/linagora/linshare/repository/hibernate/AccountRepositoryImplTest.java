/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.repository.hibernate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations={
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class AccountRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
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
		
		User u = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		u.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		u.setDomain(domain);
		accountRepository.create(u);
	}
	
}
