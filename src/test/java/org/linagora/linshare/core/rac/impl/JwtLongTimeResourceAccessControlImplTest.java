/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2018-2021 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.rac.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

public class JwtLongTimeResourceAccessControlImplTest {

	private FunctionalityReadOnlyService functionalityService;
	private AccountRepository<Account> accountRepository;
	private DomainPermissionBusinessService domainPermissionBusinessService;
	private FunctionalityReadOnlyService functionalityReadOnlyService;
	private AbstractDomainService domainService;
	private JwtLongTimeResourceAccessControlImpl testee;

	@BeforeEach
	public void beforeEach() {
		functionalityService = mock(FunctionalityReadOnlyService.class);
		accountRepository = mock(AccountRepository.class);
		domainPermissionBusinessService = mock(DomainPermissionBusinessService.class);
		functionalityReadOnlyService = mock(FunctionalityReadOnlyService.class);
		domainService = mock(AbstractDomainService.class);
		testee = new JwtLongTimeResourceAccessControlImpl(functionalityService, accountRepository, domainPermissionBusinessService, functionalityReadOnlyService, domainService);
	}

	@Test
	public void hasListPermissionShouldReturnFalseWhenUserActivationPolicyStatusIsFalse() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy activationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(activationPolicy);

		when(activationPolicy.getStatus())
				.thenReturn(false);

		Account account = null;
		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, account, entry))
				.isFalse();
	}

	@Test
	public void hasListPermissionShouldReturnIsAdminOfThisDomainWhenAuthUserIsAdmin() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);
		when(authUser.hasAdminRole())
				.thenReturn(true);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy activationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(activationPolicy);

		when(activationPolicy.getStatus())
				.thenReturn(true);

		Account account = mock(Account.class);
		String domainId = "domainId";
		when(account.getDomainId())
				.thenReturn(domainId);
		when(domainService.findById(domainId))
				.thenReturn(domain);

		when(domainPermissionBusinessService.isAdminforThisDomain(authUser, domain))
				.thenReturn(false);

		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, account, entry))
				.isFalse();
	}

	@Test
	public void hasListPermissionShouldReturnIsAdminForThisDomainWhenAuthUserIsSuperAdmin() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);
		when(authUser.hasSuperAdminRole())
				.thenReturn(true);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy activationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(activationPolicy);

		when(activationPolicy.getStatus())
				.thenReturn(true);

		Account account = mock(Account.class);
		String domainId = "domainId";
		when(account.getDomainId())
				.thenReturn(domainId);
		when(domainService.findById(domainId))
				.thenReturn(domain);

		when(domainPermissionBusinessService.isAdminforThisDomain(authUser, domain))
				.thenReturn(false);

		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, account, entry))
				.isFalse();
	}

	@Test
	public void hasListPermissionShouldReturnTrueWhenAuthUserIsInternal() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);
		when(authUser.isInternal())
				.thenReturn(true);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);
		Policy userPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userPolicy);
		when(userPolicy.getStatus())
				.thenReturn(true);

		String domainId = "domainId";
		when(authUser.getDomainId())
				.thenReturn(domainId);
		when(domainService.findById(domainId))
				.thenReturn(domain);

		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, authUser, entry))
				.isTrue();
	}

	@Test
	public void hasListPermissionShouldReturnTrueWhenAuthUserIsGuest() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);
		when(authUser.isGuest())
				.thenReturn(true);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);
		Policy userPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userPolicy);
		when(userPolicy.getStatus())
				.thenReturn(true);

		String domainId = "domainId";
		when(authUser.getDomainId())
				.thenReturn(domainId);
		when(domainService.findById(domainId))
				.thenReturn(domain);

		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, authUser, entry))
				.isTrue();
	}

	@Test
	public void hasListPermissionShouldReturnFalseInOtherCases() {
		AbstractDomain domain = mock(AbstractDomain.class);
		Account authUser = mock(Account.class);
		when(authUser.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);
		Policy userPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userPolicy);
		when(userPolicy.getStatus())
				.thenReturn(true);

		String domainId = "domainId";
		when(authUser.getDomainId())
				.thenReturn(domainId);
		when(domainService.findById(any()))
				.thenReturn(domain);

		when(domainPermissionBusinessService.isAdminforThisDomain(authUser, domain))
				.thenReturn(false);

		PermanentToken entry = null;
		assertThat(testee.hasListPermission(authUser, authUser, entry))
				.isFalse();
	}

	@Test
	public void hasCreatePermissionShouldReturnFalseWhenAccountHasDelegationRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(true);
		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasCreatePermissionShouldReturnFalseWhenFunctionalityIsNotActive() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(false);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasCreatePermissionShouldReturnTrueWhenAccountHasSuperAdminRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(authUser, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasCreatePermissionShouldReturnTrueWhenUserIsInternalIsActivatedAndHasDelegationPolicy() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);
		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy userActivationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);

		Policy userDelegationPolicy = mock(Policy.class);
		when(userFunctionality.getDelegationPolicy())
				.thenReturn(userDelegationPolicy);
		when(userDelegationPolicy.getStatus())
				.thenReturn(true);

		when(account.isInternal())
				.thenReturn(true);

		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasCreatePermissionShouldReturnTrueWhenUserIsGuestIsActivatedAndHasDelegationPolicy() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);
		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy userActivationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);

		Policy userDelegationPolicy = mock(Policy.class);
		when(userFunctionality.getDelegationPolicy())
				.thenReturn(userDelegationPolicy);
		when(userDelegationPolicy.getStatus())
				.thenReturn(true);

		when(account.isInternal())
				.thenReturn(false);
		when(account.isGuest())
				.thenReturn(true);

		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasCreatePermissionShouldReturnFalseWhenUserIsNotInternalAndIsNotGuestIsActivatedAndHasDelegationPolicy() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);
		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);
		Functionality userFunctionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		Policy userActivationPolicy = mock(Policy.class);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);

		Policy userDelegationPolicy = mock(Policy.class);
		when(userFunctionality.getDelegationPolicy())
				.thenReturn(userDelegationPolicy);
		when(userDelegationPolicy.getStatus())
				.thenReturn(true);

		when(account.isInternal())
				.thenReturn(false);
		when(account.isGuest())
				.thenReturn(false);

		PermanentToken permanentToken = null;
		assertThat(testee.hasCreatePermission(account, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasReadPermissionShouldReturnFalseWhenAccountHasDelegationRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasReadPermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasReadPermissionShouldReturnFalseWhenAccountActivationPolicyIsFalse() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(false);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasReadPermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasReadPermissionShouldReturnTrueWhenAccountHasSuperAdminRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasReadPermission(authUser, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasReadPermissionShouldReturnFalseWhenAccountHasAdminRoleAndHasConfigurationPolicyAndIsNotAdminForThisDomain() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(true);
		Policy configurationPolicy = mock(Policy.class);
		when(configurationPolicy.getStatus())
				.thenReturn(true);
		when(functionality.getConfigurationPolicy())
				.thenReturn(configurationPolicy);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity entryDomain = mock(GenericLightEntity.class);
		String uuid = "91287ccf-a8dc-4f4d-9283-78cb1a799148";
		when(entryDomain.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getDomain())
				.thenReturn(entryDomain);
		when(domainService.findById(eq(uuid)))
				.thenReturn(domain);
		when(domainPermissionBusinessService.isAdminforThisDomain(eq(account), eq(domain)))
				.thenReturn(false);

		Account authUser = null;
		assertThat(testee.hasReadPermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasReadPermissionShouldReturnTrueWhenUserFunctionalityIsActiveAndUserIsInternal() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isInternal())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasReadPermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasReadPermissionShouldReturnFalseWhenUserFunctionalityIsActiveAndUserIsGuest() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isGuest())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasReadPermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasUpdatePermissionShouldReturnFalseWhenAccountHasDelegationRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasUpdatePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasUpdatePermissionShouldReturnFalseWhenAccountActivationPolicyIsFalse() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(false);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasUpdatePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasUpdatePermissionShouldReturnTrueWhenAccountHasSuperAdminRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasUpdatePermission(authUser, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasUpdatePermissionShouldReturnFalseWhenAccountHasAdminRoleAndHasConfigurationPolicyAndIsNotAdminForThisDomain() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(true);
		Policy configurationPolicy = mock(Policy.class);
		when(configurationPolicy.getStatus())
				.thenReturn(true);
		when(functionality.getConfigurationPolicy())
				.thenReturn(configurationPolicy);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity entryDomain = mock(GenericLightEntity.class);
		String uuid = "91287ccf-a8dc-4f4d-9283-78cb1a799148";
		when(entryDomain.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getDomain())
				.thenReturn(entryDomain);
		when(domainService.findById(eq(uuid)))
				.thenReturn(domain);
		when(domainPermissionBusinessService.isAdminforThisDomain(eq(account), eq(domain)))
				.thenReturn(false);

		Account authUser = null;
		assertThat(testee.hasUpdatePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasUpdatePermissionShouldReturnTrueWhenUserFunctionalityIsActiveAndUserIsInternal() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isInternal())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasUpdatePermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasUpdatePermissionShouldReturnFalseWhenUserFunctionalityIsActiveAndUserIsGuest() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isGuest())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasUpdatePermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasDeletePermissionShouldReturnFalseWhenAccountHasDelegationRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasDeletePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasDeletePermissionShouldReturnFalseWhenAccountActivationPolicyIsFalse() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(false);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasDeletePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasDeletePermissionShouldReturnTrueWhenAccountHasSuperAdminRole() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(true);

		Account authUser = null;
		PermanentToken permanentToken = null;
		assertThat(testee.hasDeletePermission(authUser, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasDeletePermissionShouldReturnFalseWhenAccountHasAdminRoleAndHasConfigurationPolicyAndIsNotAdminForThisDomain() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(true);
		Policy configurationPolicy = mock(Policy.class);
		when(configurationPolicy.getStatus())
				.thenReturn(true);
		when(functionality.getConfigurationPolicy())
				.thenReturn(configurationPolicy);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity entryDomain = mock(GenericLightEntity.class);
		String uuid = "91287ccf-a8dc-4f4d-9283-78cb1a799148";
		when(entryDomain.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getDomain())
				.thenReturn(entryDomain);
		when(domainService.findById(eq(uuid)))
				.thenReturn(domain);
		when(domainPermissionBusinessService.isAdminforThisDomain(eq(account), eq(domain)))
				.thenReturn(false);

		Account authUser = null;
		assertThat(testee.hasDeletePermission(authUser, account, permanentToken))
				.isFalse();
	}

	@Test
	public void hasDeletePermissionShouldReturnTrueWhenUserFunctionalityIsActiveAndUserIsInternal() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isInternal())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasDeletePermission(account, account, permanentToken))
				.isTrue();
	}

	@Test
	public void hasDeletePermissionShouldReturnFalseWhenUserFunctionalityIsActiveAndUserIsGuest() {
		Account account = mock(Account.class);
		when(account.hasDelegationRole())
				.thenReturn(false);
		AbstractDomain domain = mock(AbstractDomain.class);
		when(account.getDomain())
				.thenReturn(domain);

		Functionality functionality = mock(Functionality.class);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(functionality);

		Policy activationPolicy = mock(Policy.class);
		when(functionality.getActivationPolicy())
				.thenReturn(activationPolicy);
		when(activationPolicy.getStatus())
				.thenReturn(true);

		when(account.hasSuperAdminRole())
				.thenReturn(false);

		when(account.hasAdminRole())
				.thenReturn(false);

		Functionality userFunctionality = mock(Functionality.class);
		Policy userActivationPolicy = mock(Policy.class);
		when(userActivationPolicy.getStatus())
				.thenReturn(true);
		when(userFunctionality.getActivationPolicy())
				.thenReturn(userActivationPolicy);
		when(functionalityReadOnlyService.getJwtLongTimeFunctionality(domain))
				.thenReturn(userFunctionality);

		when(account.isGuest())
				.thenReturn(true);

		PermanentToken permanentToken = mock(PermanentToken.class);
		GenericLightEntity actor = mock(GenericLightEntity.class);
		String uuid = "6180dbc8-9966-4f6d-b1cd-34e2d1be7c10";
		when(actor.getUuid())
				.thenReturn(uuid);
		when(permanentToken.getActor())
				.thenReturn(actor);
		when(accountRepository.findByLsUuid(uuid))
				.thenReturn(account);

		assertThat(testee.hasDeletePermission(account, account, permanentToken))
				.isTrue();
	}
}
