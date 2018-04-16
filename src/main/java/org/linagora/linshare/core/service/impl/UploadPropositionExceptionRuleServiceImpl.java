/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.business.service.UploadPropositionExceptionRuleBusinessService;
import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.rac.UploadPropositionExceptionRuleResourceAccessControl;
import org.linagora.linshare.core.service.UploadPropositionExceptionRuleService;
import org.linagora.linshare.mongo.entities.UploadPropositionExceptionRule;

public class UploadPropositionExceptionRuleServiceImpl extends GenericServiceImpl<Account, UploadPropositionExceptionRule> implements UploadPropositionExceptionRuleService {

	private final UploadPropositionExceptionRuleBusinessService exceptionRuleBusinessService;

	public UploadPropositionExceptionRuleServiceImpl(
			final UploadPropositionExceptionRuleBusinessService exceptionRuleBusinessService,
			final UploadPropositionExceptionRuleResourceAccessControl rac) {
		super(rac);
		this.exceptionRuleBusinessService = exceptionRuleBusinessService;
	}

	@Override
	public UploadPropositionExceptionRule find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		checkReadPermission(authUser, actor, UploadPropositionExceptionRule.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_READ, null);
		return exceptionRuleBusinessService.find(uuid);
	}

	@Override
	public List<UploadPropositionExceptionRule> findByExceptionRule(Account authUser, Account actor,
			UploadPropositionExceptionRuleType exceptionRuleType) {
		preChecks(authUser, actor);
		checkReadPermission(authUser, actor, UploadPropositionExceptionRule.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_LIST, null);
		return exceptionRuleBusinessService.findByExceptionRuleType(actor.getLsUuid(), exceptionRuleType);
	}

	@Override
	public UploadPropositionExceptionRule create(Account authUser, Account actor,
			UploadPropositionExceptionRule exceptionRule) {
		preChecks(authUser, actor);
		checkCreatePermission(authUser, actor, UploadPropositionExceptionRule.class,
				BusinessErrorCode.UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_CREATE, null);
		UploadPropositionExceptionRule persistedExceptionRule = new UploadPropositionExceptionRule(
				UUID.randomUUID().toString(), exceptionRule.getDomainUuid(), exceptionRule.getMail(),
				exceptionRule.getAccountUuid(), exceptionRule.getExceptionRuleType(), new Date(), new Date());
		return exceptionRuleBusinessService.create(persistedExceptionRule);
	}

}
