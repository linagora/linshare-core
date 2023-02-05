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
package org.linagora.linshare.webservice.admin;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;

public interface PublicKeyRestService {

	PublicKeyLs create(PublicKeyLs publicKeyLs) throws BusinessException;

	PublicKeyLs find(String uuid) throws BusinessException;

	List<PublicKeyLs> findAll(String domainUuid) throws BusinessException;

	PublicKeyLs delete(PublicKeyLs publicKeyLs, String uuid) throws BusinessException;

	Set<AuditLogEntryAdmin> findAll(String domainUuid, List<LogAction> actions);

	PublicKeyLs create(InputStream publicKeyInputS, String domainUuid, String issuer, PublicKeyFormat format)
			throws BusinessException;
}
