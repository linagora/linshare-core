package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;
import org.linagora.linshare.core.domain.entities.PlatformQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.QuotaFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.EnsembleQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PlatformQuotaDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountQuotaService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainQuotaService;
import org.linagora.linshare.core.service.EnsembleQuotaService;
import org.linagora.linshare.core.service.PlatformQuotaService;

public class QuotaFacadeImpl extends AdminGenericFacadeImpl implements QuotaFacade {

	private AccountQuotaService accountQuotaService;
	private DomainQuotaService domainQuotaService;
	private EnsembleQuotaService ensembleQuotaService;
	private PlatformQuotaService platformQuotaService;
	private AbstractDomainService abstractDomainService;

	public QuotaFacadeImpl(AccountService accountService, AccountQuotaService accountQuotaService,
			DomainQuotaService domainQuotaService, EnsembleQuotaService ensembleQuotaService,
			PlatformQuotaService platformQuotaService, AbstractDomainService abstractDomainService) {
		super(accountService);
		this.accountQuotaService = accountQuotaService;
		this.domainQuotaService = domainQuotaService;
		this.ensembleQuotaService = ensembleQuotaService;
		this.platformQuotaService = platformQuotaService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public AccountQuotaDto create(AccountQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "AccountQuotaDto must be set.");
		Validate.notNull(entity.getAccount(), "Account in AccountQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in AccountQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in AccountQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in AccountQuotaDto must be set.");
		Account owner = accountService.findByLsUuid(entity.getAccount().getUuid());
		AbstractDomain domain = abstractDomainService.findById(entity.getDomain().getIdentifier());
		AbstractDomain parentDomain = abstractDomainService.findById(entity.getParentDomain().getIdentifier());
		AccountQuota accountQuota = new AccountQuota(owner, domain, parentDomain, entity.getQuota(),
				entity.getQuotaWarning(), entity.getFileSizeMax(), entity.getCurrentValue(), entity.getLastValue());
		return new AccountQuotaDto(accountQuotaService.create(actor, owner, accountQuota));
	}

	@Override
	public DomainQuotaDto create(DomainQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "DomainQuotaDto must be set.");
		Validate.notNull(entity.getDomain(), "Domain in DomainQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in DomainQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in DomainQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in DomainQuotaDto must be set.");
		AbstractDomain domain = abstractDomainService.findById(entity.getDomain().getIdentifier());
		AbstractDomain parentDomain = abstractDomainService.findById(entity.getParentDomain().getIdentifier());
		DomainQuota domainQuota = new DomainQuota(domain, parentDomain, entity.getQuota(), entity.getQuotaWarning(),
				entity.getFileSizeMax(), entity.getCurrentValue(), entity.getLastValue());
		return new DomainQuotaDto(domainQuotaService.create(actor, domain, domainQuota));
	}

	@Override
	public EnsembleQuotaDto create(EnsembleQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getEnsembleType(), "EnsembleType in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getDomain(), "Domain in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in EnsembleQuotaDto must be set.");
		AbstractDomain domain = abstractDomainService.findById(entity.getDomain().getIdentifier());
		AbstractDomain parentDomain = abstractDomainService.findById(entity.getParentDomain().getIdentifier());
		EnsembleQuota ensembleQuota = new EnsembleQuota(domain, parentDomain, entity.getQuota(),
				entity.getQuotaWarning(), entity.getFileSizeMax(), entity.getCurrentValue(), entity.getLastValue(),
				entity.getEnsembleType());
		return new EnsembleQuotaDto(ensembleQuotaService.create(actor, domain, ensembleQuota));
	}

	@Override
	public PlatformQuotaDto create(PlatformQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "PlatformQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in PlatformQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in PlatformQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in PlatformQuotaDto must be set.");
		PlatformQuota platformQuota = new PlatformQuota(entity.getQuota(), entity.getQuotaWarning(),
				entity.getFileSizeMax(), entity.getCurrentValue(), entity.getLastValue());
		return new PlatformQuotaDto(platformQuotaService.create(actor, platformQuota));
	}

	@Override
	public AccountQuotaDto update(AccountQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "AccountQuotaDto must be set.");
		Validate.notNull(entity.getAccount(), "Account in AccountQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in AccountQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in AccountQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in AccountQuotaDto must be set.");
		Account owner = accountService.findByLsUuid(entity.getAccount().getUuid());
		AccountQuota accountQuota = accountQuotaService.find(actor, owner);
		accountQuota.setQuota(entity.getQuota());
		accountQuota.setQuotaWarning(entity.getQuotaWarning());
		accountQuota.setFileSizeMax(entity.getFileSizeMax());
		return new AccountQuotaDto(accountQuotaService.update(actor, owner, accountQuota));
	}

	@Override
	public DomainQuotaDto update(DomainQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "DomainQuotaDto must be set.");
		Validate.notNull(entity.getDomain(), "Domain in DomainQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in DomainQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in DomainQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in DomainQuotaDto must be set.");
		AbstractDomain domain = abstractDomainService.findById(entity.getDomain().getIdentifier());
		DomainQuota domainQuota = domainQuotaService.find(actor, domain);
		domainQuota.setQuota(entity.getQuota());
		domainQuota.setQuotaWarning(entity.getQuotaWarning());
		domainQuota.setFileSizeMax(entity.getFileSizeMax());
		return new DomainQuotaDto(domainQuotaService.update(actor, domain, domainQuota));
	}

	@Override
	public EnsembleQuotaDto update(EnsembleQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(entity, "EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getEnsembleType(), "EnsembleType in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getDomain(), "Domain in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in EnsembleQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in EnsembleQuotaDto must be set.");
		AbstractDomain domain = abstractDomainService.findById(entity.getDomain().getIdentifier());
		EnsembleQuota ensembleQuota = ensembleQuotaService.find(actor, domain, entity.getEnsembleType());
		ensembleQuota.setQuota(entity.getQuota());
		ensembleQuota.setQuotaWarning(entity.getQuotaWarning());
		ensembleQuota.setFileSizeMax(entity.getFileSizeMax());
		return new EnsembleQuotaDto(ensembleQuotaService.update(actor, domain, ensembleQuota));
	}

	@Override
	public PlatformQuotaDto update(PlatformQuotaDto entity) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(entity, "PlatformQuotaDto must be set.");
		Validate.notNull(entity.getQuota(), "Quota in PlatformQuotaDto must be set.");
		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in PlatformQuotaDto must be set.");
		Validate.notNull(entity.getFileSizeMax(), "FileSizeMax in PlatformQuotaDto must be set.");
		PlatformQuota platformQuota = platformQuotaService.find(actor);
		platformQuota.setQuota(entity.getQuota());
		platformQuota.setQuotaWarning(entity.getQuotaWarning());
		platformQuota.setFileSizeMax(entity.getFileSizeMax());
		return new PlatformQuotaDto(platformQuotaService.update(actor, platformQuota));
	}

	@Override
	public AccountQuotaDto findAccountQuota(String accountUuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(accountUuid, "accountUuid must be set.");
		Account owner = accountService.findByLsUuid(accountUuid);
		AccountQuota accountQuota = accountQuotaService.find(actor, owner);
		return new AccountQuotaDto(accountQuota);
	}

	@Override
	public DomainQuotaDto findDomainQuota(String domain) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(domain, "domain identifier must be set.");
		AbstractDomain abstractDomain = abstractDomainService.findById(domain);
		DomainQuota domainQuota = domainQuotaService.find(actor, abstractDomain);
		return new DomainQuotaDto(domainQuota);
	}

	@Override
	public EnsembleQuotaDto findEnsembleQuota(String domain, String ensembleType) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(domain, "domain identifier must be set.");
		Validate.notNull(ensembleType, "ensembleType must be set.");
		AbstractDomain abstractDomain = abstractDomainService.findById(domain);
		EnsembleType ensembleTypeObject = EnsembleType.fromString(ensembleType);
		EnsembleQuota ensembleQuota = ensembleQuotaService.find(actor, abstractDomain, ensembleTypeObject);
		return new EnsembleQuotaDto(ensembleQuota);
	}

	@Override
	public PlatformQuotaDto findPlatformQuota() throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		PlatformQuota platformQuota = platformQuotaService.find(actor);
		return new PlatformQuotaDto(platformQuota);
	}
}
