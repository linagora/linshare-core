package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadPropositionFilterFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.linagora.linshare.webservice.dto.UploadPropositionFilterDto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadPropositionFilterFacadeImpl extends AdminGenericFacadeImpl
		implements UploadPropositionFilterFacade {

	private final UploadPropositionFilterService service;

	public UploadPropositionFilterFacadeImpl(AccountService accountService,
			UploadPropositionFilterService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public List<UploadPropositionFilterDto> findAll() throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		List<UploadPropositionFilter> all = service.findAll(actor);
		return Lists.transform(ImmutableList.copyOf(all), UploadPropositionFilterDto.toVo());
	}

	@Override
	public UploadPropositionFilterDto find(String uuid)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = service.find(actor, uuid);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto create(UploadPropositionFilterDto dto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = UploadPropositionFilterDto.toEntity().apply(dto);
		filter = service.create(actor, filter);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto update(UploadPropositionFilterDto dto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = UploadPropositionFilterDto.toEntity().apply(dto);
		filter = service.update(actor, filter);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		service.delete(actor, uuid);
	}

}
