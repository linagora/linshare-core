package org.linagora.linshare.core.facade.webservice.uploadproposition.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleFieldType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleOperatorType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadproposition.UploadPropositionFacade;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionActionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionFilterDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionRuleDto;
import org.linagora.linshare.core.service.AccountService;

import com.google.common.collect.Lists;

public class UploadPropositionFacadeImpl extends
		UploadPropositionGenericFacadeImpl implements UploadPropositionFacade {

	public UploadPropositionFacadeImpl(AccountService accountService) {
		super(accountService);
	}

	@Override
	public List<UploadPropositionFilterDto> findAll() throws BusinessException {
		User actor = this.checkAuthentication();
		List<UploadPropositionFilterDto> filters = Lists.newArrayList();
		UploadPropositionActionDto action = new UploadPropositionActionDto(
				"ee1bf0ab-21ad-4a69-914d-d792eb2b36d7",
				UploadPropositionActionType.ACCEPT, null);
		UploadPropositionRuleDto rule = new UploadPropositionRuleDto(
				"3f52d026-9719-4f0b-bf4b-f5e9252a71a7",
				UploadPropositionRuleOperatorType.TRUE,
				UploadPropositionRuleFieldType.SENDER_EMAIL, null);
		UploadPropositionFilterDto filter = new UploadPropositionFilterDto(
				"5724946a-eebe-450b-bb84-0d8af480f3f6", "default filter 1",
				true);
		filter.getUploadPropositionActions().add(action);
		filter.getUploadPropositionRules().add(rule);
		filters.add(filter);
		return filters;
	}

	@Override
	public boolean checkIfValidRecipeint(String userMail, String userDomain) {
		List<String> list = Lists.newArrayList();
		list.add("bart.simpson@int1.linshare.dev");
		return list.contains(userMail);
	}

	@Override
	public void create(UploadPropositionDto dto) throws BusinessException {
		// TODO Auto-generated method stub
	}
}
