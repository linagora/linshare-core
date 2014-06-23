package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.webservice.dto.TechnicalAccountDto;

import com.google.common.collect.Sets;

public class TechnicalAccountFacadeImpl extends AdminGenericFacadeImpl
		implements TechnicalAccountFacade {

	private final TechnicalAccountService technicalAccountService;

	public TechnicalAccountFacadeImpl(final AccountService accountService,
			final TechnicalAccountService technicalAccountService) {
		super(accountService);
		this.technicalAccountService = technicalAccountService;
	}

	@Override
	public TechnicalAccountDto create(TechnicalAccountDto dto)
			throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(dto.getName(), "name must be set.");
		Validate.notEmpty(dto.getMail(), "mail must be set.");
		TechnicalAccount technicalAccount = new TechnicalAccount(dto);
		return new TechnicalAccountDto(technicalAccountService.create(actor,
				technicalAccount));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(actor, uuid);
		technicalAccountService.delete(actor, account);
	}

	@Override
	public void delete(TechnicalAccountDto dto) throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(actor, dto.getUuid());
		technicalAccountService.delete(actor, account);
	}

	@Override
	public TechnicalAccountDto find(String uuid) throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccount account = technicalAccountService.find(actor, uuid);
		return new TechnicalAccountDto(account);
	}

	@Override
	public Set<TechnicalAccountDto> findAll() throws BusinessException {
		User actor = checkAuth();
		Set<TechnicalAccountDto> res = Sets.newHashSet();
		Set<TechnicalAccount> all = technicalAccountService.findAll(actor);
		for (TechnicalAccount entity : all) {
			res.add(new TechnicalAccountDto(entity));
		}
		return res;
	}

	@Override
	public TechnicalAccountDto update(TechnicalAccountDto dto)
			throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		Validate.notEmpty(dto.getName(), "name must be set.");
		Validate.notEmpty(dto.getMail(), "mail must be set.");
		TechnicalAccount account = technicalAccountService.update(actor,
				new TechnicalAccount(dto));
		return new TechnicalAccountDto(account);
	}

	/**
	 * Helpers
	 */
	
	private User checkAuth() throws BusinessException {
		return checkAuthentication(Role.SUPERADMIN);
	}
}
