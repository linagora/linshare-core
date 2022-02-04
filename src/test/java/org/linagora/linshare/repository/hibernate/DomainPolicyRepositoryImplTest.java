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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DomainPolicyRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	private static String domainePolicyName0 = "TestPolicy0";

	@Test
	public void testCreateDomainPolicy1() throws BusinessException{
		DomainAccessPolicy accessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(accessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName0, accessPolicy);

		logger.debug("Current accessPolicy : " + accessPolicy.toString());
		logger.debug("Current policy : " + policy.toString());


		domainPolicyRepository.create(policy);
		Assertions.assertNotNull(policy.getPersistenceId());

		DomainPolicy entityPolicy = domainPolicyRepository.findById(policy.getUuid());

		Assertions.assertTrue(entityPolicy != null );
		logger.debug("My name is : " + entityPolicy.getUuid());
		logger.debug(entityPolicy.getDomainAccessPolicy().toString());

		domainPolicyRepository.delete(entityPolicy);
	}
}
