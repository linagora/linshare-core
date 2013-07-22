package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

public class FunctionalityFacadeImpl extends
		GenericFacadeImpl implements FunctionalityFacade {

	private AbstractDomainService abstractDomainService;
	private FunctionalityService functionalityService;

	public FunctionalityFacadeImpl(
			final AccountService accountService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityService functionalityService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
	}

	@Override
	public FunctionalityDto get(String domain, String identifier)
			throws BusinessException {
		Functionality func = functionalityService
				.getFunctionalityByIdentifiers(domain, identifier);

		return (FunctionalityDto) toDto().transform(func);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FunctionalityDto> getAll(String domain)
			throws BusinessException {
		List<Functionality> funcs = functionalityService
				.getAllFunctionalities(domain);

		return ListUtils.transformedList(funcs, toDto());
	}

	@Override
	public void update(String domain, FunctionalityDto func)
			throws BusinessException {
		Functionality entity = (Functionality) toEntity().transform(func);

		functionalityService.update(domain, entity);
	}
	
	/*
	 * Helpers / Transformers
	 */

	private Transformer toDto() {
		return new Transformer() {
			@Override
			public Object transform(Object input) {
				return new FunctionalityDto((Functionality) input);
			}
		};
	}

	private Transformer toEntity() {
		return new Transformer() {
			@Override
			public Object transform(Object input) {
				FunctionalityDto dto = (FunctionalityDto) input;

				return functionalityService.getFunctionalityByIdentifiers(
						dto.getDomain(), dto.getIdentifier());
			}
		};
	}
}
