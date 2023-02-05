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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class GenericAdminServiceImpl {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	public GenericAdminServiceImpl(SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super();
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
	}

	protected String sanitize(String input) {
		if (!Strings.isNullOrEmpty(input)) {
			return sanitizerInputHtmlBusinessService.strictClean(input);
		}
		return input;
	}

	protected void preChecks(Account actor) {
		Validate.notNull(actor, "Missing actor account");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		if (logger.isTraceEnabled()) {
			logger.trace("Current actor " + actor.getAccountRepresentation());
		}
	}
}
