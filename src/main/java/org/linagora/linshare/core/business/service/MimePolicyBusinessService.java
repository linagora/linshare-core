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
package org.linagora.linshare.core.business.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.exception.BusinessException;

public interface MimePolicyBusinessService {

	MimePolicy create(MimePolicy mimePolicy) throws BusinessException;

	void delete(MimePolicy mimePolicy) throws BusinessException;

	MimePolicy find(String uuid) throws BusinessException;

	Set<MimePolicy> findAll() throws BusinessException;

	MimePolicy load(MimePolicy mimePolicy) throws BusinessException;

	MimePolicy update(MimePolicy mimePolicy) throws BusinessException;

	MimePolicy enableAll(MimePolicy mimePolicy) throws BusinessException;

	MimePolicy disableAll(MimePolicy mimePolicy) throws BusinessException;

	boolean isTypeUnknown(String mimeType);
}
