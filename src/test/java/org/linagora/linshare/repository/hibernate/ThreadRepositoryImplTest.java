/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.repository.hibernate;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class ThreadRepositoryImplTest {

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
	@Qualifier("threadRepository")
	private ThreadRepository threadRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	private AbstractDomain domain;
	
	private User internal;
	
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal( FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setLocale(domain.getDefaultTapestryLocale());
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);
		
		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		
		accountRepository.delete(internal);
		
		logger.debug("End tearDown");
	}
	
	@Test
	public void testCreateThread() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		WorkGroup t = new WorkGroup(domain, internal, "myThread");
		t.setLocale(domain.getDefaultTapestryLocale());
		t.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		threadRepository.create(t);
		
		logger.info(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testCreateThreadAndMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		WorkGroup t = new WorkGroup(domain, internal, "myThread");
		t.setLocale(domain.getDefaultTapestryLocale());
		t.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		threadRepository.create(t);
		
		WorkgroupMember m = new WorkgroupMember(true,true,internal,t);
		t.getMyMembers().add(m);
		threadRepository.update(t);
		
		logger.info("user id :" + internal.getId());
		logger.info("thread id :" + t.getId());
		logger.info("member id :" + m.getId());
		logger.debug("count : " + threadRepository.findAll().size());
		
		logger.info(LinShareTestConstants.END_TEST);
	}
	
}
