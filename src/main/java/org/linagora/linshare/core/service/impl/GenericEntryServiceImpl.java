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

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.EntryResourceAccessControl;

public class GenericEntryServiceImpl<R, E extends Entry> extends
		GenericServiceImpl<R, E> {

	protected final EntryResourceAccessControl<R, E> rac;

	public GenericEntryServiceImpl(EntryResourceAccessControl<R, E> rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.rac = rac;
	}

	protected void checkDownloadPermission(Account actor,
			Account targetedAccount, Class<?> clazz, BusinessErrorCode errCode,
			E entry, Object... opt) throws BusinessException {
		rac.checkDownloadPermission(actor, targetedAccount, clazz, errCode,
				entry, opt);
	}

	protected void checkThumbNailDownloadPermission(Account actor,
			Account targetedAccount, Class<?> clazz, BusinessErrorCode errCode,
			E entry, Object... opt) throws BusinessException {
		rac.checkThumbNailDownloadPermission(actor, targetedAccount, clazz,
				errCode, entry, opt);
	}
}
