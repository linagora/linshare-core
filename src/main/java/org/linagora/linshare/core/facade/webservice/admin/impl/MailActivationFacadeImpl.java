package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
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
		User actor = checkAuthentication(Role.ADMIN);
		if (domainId == null)
			domainId = actor.getDomainId();
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Iterable<MailActivation> entities = service.findAll(actor, domainId);
		Iterable<MailActivationAdminDto> transform = Iterables.transform(
				entities, MailActivationAdminDto.toDto());
		// Copy is made because the transaction is closed at the end of every
		// method in facade classes.
		return Ordering.natural().immutableSortedCopy(transform);
	}

	@Override
	public MailActivationAdminDto find(String domainId, String mailActivationId)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(mailActivationId,
				"functionality identifier must be set.");
		MailActivation ma = service.find(actor, domainId, mailActivationId);
		return MailActivationAdminDto.toDto().apply(ma);
	}

	@Override
	public MailActivationAdminDto update(MailActivationAdminDto mailActivation)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(mailActivation.getDomain(),
				"domain identifier must be set.");
		Validate.notEmpty(mailActivation.getIdentifier(),
				"mailActivation identifier must be set.");
		MailActivation entity = service.find(actor, mailActivation.getDomain(),
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
		MailActivation update = service.update(actor,
				mailActivation.getDomain(), entity);
		return new MailActivationAdminDto(update);
	}

	@Override
	public void delete(MailActivationAdminDto mailActivation)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(mailActivation.getDomain(),
				"domain identifier must be set.");
		Validate.notEmpty(mailActivation.getIdentifier(),
				"mailActivation identifier must be set.");
		service.delete(actor, mailActivation.getDomain(),
				mailActivation.getIdentifier());
	}

}
