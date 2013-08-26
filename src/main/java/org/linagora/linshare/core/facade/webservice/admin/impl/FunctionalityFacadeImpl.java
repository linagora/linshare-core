package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements FunctionalityFacade {

	private AbstractDomainService abstractDomainService;
	private FunctionalityService functionalityService;

	public FunctionalityFacadeImpl(final AccountService accountService, final AbstractDomainService abstractDomainService,
			final FunctionalityService functionalityService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
	}

	@Override
	public FunctionalityDto get(String domain, String identifier) throws BusinessException {
//		Functionality func = functionalityService.getFunctionalityByIdentifiers(domain, identifier);
//		return new FunctionalityDto(func);
		return null;
	}

	@Override
	public List<FunctionalityDto> getAll(String domain) throws BusinessException {
		Set<Functionality> entities = functionalityService.getAllFunctionalities(domain);
		
		List<FunctionalityDto> ret = new ArrayList<FunctionalityDto>();

		for (Functionality e : entities) {
			ret.add(new FunctionalityDto(e));
		}
		return ret;
	}

	@Override
	public void update(String domain, FunctionalityDto func) throws BusinessException {
//		Functionality entity = functionalityService.getFunctionalityByIdentifiers(func.getDomain(), func.getIdentifier());

//		functionalityService.update(domain, entity);
		
	}
}