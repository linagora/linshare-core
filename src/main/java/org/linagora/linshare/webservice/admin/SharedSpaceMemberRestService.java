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

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;

public interface SharedSpaceMemberRestService {

	SharedSpaceMember find(String uuid) throws BusinessException;

	SharedSpaceMember addMember(SharedSpaceMember member) throws BusinessException;

	SharedSpaceMember update(SharedSpaceMember ssmember, String uuid, boolean force, Boolean propagate) throws BusinessException;

	SharedSpaceMember delete(SharedSpaceMember ssmember, String uuid) throws BusinessException;
}
