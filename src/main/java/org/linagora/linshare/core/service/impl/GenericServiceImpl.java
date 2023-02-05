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
package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class GenericServiceImpl<R, E> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final AbstractResourceAccessControl<Account, R, E> rac;

	protected final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	public GenericServiceImpl(
			AbstractResourceAccessControl<Account, R, E> rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super();
		this.rac = rac;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
	}

	protected void preChecks(Account actor, Account owner) {
		preChecks(actor, owner, false);
	}

	protected void preChecks(Account actor, Account owner,
			boolean dontCheckOwner) {
		Validate.notNull(actor, "Missing actor account");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		if (!dontCheckOwner) {
			Validate.notNull(owner, "Missing owner account");
			Validate.notEmpty(owner.getLsUuid(), "Missing owner uuid");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Current actor " + actor.getAccountRepresentation());
			if (!dontCheckOwner) {
				logger.trace("Current owner " + owner.getAccountRepresentation());
			}
		}
	}

	protected void checkReadPermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		rac.checkReadPermission(actor, targetedAccount, clazz, errCode, entry,
				opt);
	}

	protected void checkListPermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		rac.checkListPermission(actor, targetedAccount, clazz, errCode, entry,
				opt);
	}

	protected void checkCreatePermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		rac.checkCreatePermission(actor, targetedAccount, clazz, errCode,
				entry, opt);
	}

	protected void checkUpdatePermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		rac.checkUpdatePermission(actor, targetedAccount, clazz, errCode,
				entry, opt);
	}

	protected void checkDeletePermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, E entry, Object... opt)
			throws BusinessException {
		rac.checkDeletePermission(actor, targetedAccount, clazz, errCode,
				entry, opt);
	}

	protected String sanitize(String input) {
		if (!Strings.isNullOrEmpty(input)) {
			return sanitizerInputHtmlBusinessService.strictClean(input);
		}
		return input;
	}

	protected String sanitizeFileName(String input) {
		if (!Strings.isNullOrEmpty(input)) {
			return sanitizerInputHtmlBusinessService.sanitizeFileName(input);
		}
		return input;
	}

	protected String sanitizeDuplicatedContactListName(String input) {
		if (!Strings.isNullOrEmpty(input)) {
			return sanitizerInputHtmlBusinessService.sanitizeDuplicatedContactListName(input);
		}
		return input;
	}
}
