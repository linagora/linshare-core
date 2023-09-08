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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailActivationFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailActivationAdminDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailActivationService;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class MailActivationFacadeImpl extends AdminGenericFacadeImpl implements
		MailActivationFacade {

	protected final MailActivationService service;

	public MailActivationFacadeImpl(AccountService accountService,
			MailActivationService mailActivationService) {
		super(accountService);
		this.service = mailActivationService;
	}

	@Override
	public List<MailActivationAdminDto> findAll(String domainId)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		if (domainId == null)
			domainId = authUser.getDomainId();
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Iterable<MailActivation> entities = service.findAll(authUser, domainId);
		Iterable<MailActivationAdminDto> transform = Iterables.transform(
				entities, MailActivationAdminDto.toDto());
		// Copy is made because the transaction is closed at the end of every
		// method in facade classes.
		return Ordering.natural().immutableSortedCopy(transform);
	}

	@Override
	public MailActivationAdminDto find(String domainId, String mailActivationId)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(mailActivationId,
				"functionality identifier must be set.");
		MailActivation ma = service.find(authUser, domainId, mailActivationId);
		return MailActivationAdminDto.toDto().apply(ma);
	}

	@Override
	public MailActivationAdminDto update(MailActivationAdminDto mailActivation)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(mailActivation.getDomain(),
				"domain identifier must be set.");
		Validate.notEmpty(mailActivation.getIdentifier(),
				"mailActivation identifier must be set.");
		MailActivation entity = service.find(authUser, mailActivation.getDomain(),
				mailActivation.getIdentifier());

		// copy of activation policy.
		String ap = mailActivation.getActivationPolicy().getPolicy().trim()
				.toUpperCase();
		entity.getActivationPolicy().setPolicy(Policies.valueOf(ap));
		entity.getActivationPolicy().setStatus(
				mailActivation.getActivationPolicy().getStatus());

		// copy of configuration policy.
		String cp = mailActivation.getConfigurationPolicy().getPolicy().trim()
				.toUpperCase();
		entity.getConfigurationPolicy().setPolicy(Policies.valueOf(cp));
		entity.getConfigurationPolicy().setStatus(
				mailActivation.getConfigurationPolicy().getStatus());

		// copy of configuration policy.
		String dp = mailActivation.getDelegationPolicy().getPolicy().trim()
				.toUpperCase();
		entity.getDelegationPolicy().setPolicy(Policies.valueOf(dp));
		entity.getDelegationPolicy().setStatus(
				mailActivation.getDelegationPolicy().getStatus());

		// copy of parameters.
		entity.setEnable(mailActivation.getEnable());
		MailActivation update = service.update(authUser,
				mailActivation.getDomain(), entity);
		return new MailActivationAdminDto(update);
	}

	@Override
	public void delete(MailActivationAdminDto mailActivation)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(mailActivation.getDomain(),
				"domain identifier must be set.");
		Validate.notEmpty(mailActivation.getIdentifier(),
				"mailActivation identifier must be set.");
		service.delete(authUser, mailActivation.getDomain(),
				mailActivation.getIdentifier());
	}

}
