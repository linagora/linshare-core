/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.UploadPropositionExceptionRuleFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadPropositionExceptionRuleService;
import org.linagora.linshare.mongo.entities.UploadPropositionExceptionRule;

import com.google.common.base.Strings;

public class UploadPropositionExceptionRuleFacadeImpl extends GenericFacadeImpl implements UploadPropositionExceptionRuleFacade {

	private final UploadPropositionExceptionRuleService exceptionRuleService;

	public UploadPropositionExceptionRuleFacadeImpl(AccountService accountService,
			UploadPropositionExceptionRuleService exceptionRuleService) {
		super(accountService);
		this.exceptionRuleService = exceptionRuleService;
	}

	@Override
	public UploadPropositionExceptionRule find(String actorUuid, String uuid) {
		Validate.notEmpty(uuid, "uuid cannot be empty");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return exceptionRuleService.find(authUser, actor, uuid);
	}

	@Override
	public List<UploadPropositionExceptionRule> findByExceptionRuleType(String actorUuid,
			UploadPropositionExceptionRuleType exceptionRuleType) {
		Validate.notNull(exceptionRuleType);
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return exceptionRuleService.findByExceptionRule(authUser, actor, exceptionRuleType);
	}

	@Override
	public UploadPropositionExceptionRule create(String actorUuid, UploadPropositionExceptionRule exceptionRule) {
		Validate.notNull(exceptionRule);
		Validate.notEmpty(exceptionRule.getDomainUuid(), "DomainUuid cannot be empty");
		Validate.notEmpty(exceptionRule.getMail(), "Mail cannot be empty");
		Validate.notEmpty(exceptionRule.getAccountUuid(), "Account Uuid cannot be empty");
		Validate.notNull(exceptionRule.getExceptionRuleType(), "ExceptionRule Type cannot be null");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return exceptionRuleService.create(authUser, actor, exceptionRule);
	}

	@Override
	public UploadPropositionExceptionRule delete(String actorUuid, String uuid,
			UploadPropositionExceptionRule exceptionRule) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			exceptionRule = exceptionRuleService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(exceptionRule, "ExceptionRule must be set");
			Validate.notEmpty(exceptionRule.getUuid(), "ExceptionRule uuid must be set");
			exceptionRule = exceptionRuleService.find(authUser, actor, exceptionRule.getUuid());
		}
		
		return exceptionRuleService.delete(authUser, actor, exceptionRule);
	}

	@Override
	public UploadPropositionExceptionRule update(String actorUuid, String uuid, UploadPropositionExceptionRule exceptionRule) {
		Validate.notNull(exceptionRule, "ExceptionRule must be set");
		Validate.notEmpty(exceptionRule.getDomainUuid(), "DomainUuid cannot be empty");
		Validate.notEmpty(exceptionRule.getMail(), "Mail cannot be empty");
		Validate.notEmpty(exceptionRule.getAccountUuid(), "Account Uuid cannot be empty");
		Validate.notNull(exceptionRule.getExceptionRuleType(), "ExceptionRule Type cannot be null");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			exceptionRule.setUuid(uuid);
		}
		Validate.notEmpty(exceptionRule.getUuid(), "ExceptionRule uuid must be set");
		return exceptionRuleService.update(authUser, actor, exceptionRule);
	}
}
