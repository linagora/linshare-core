/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.adminv5;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

public interface SharedSpaceRestService {

	SharedSpaceNode find(String uuid) throws BusinessException;

	SharedSpaceNode delete(SharedSpaceNode node, String uuid) throws BusinessException;

	SharedSpaceNode update(SharedSpaceNode node, String uuid) throws BusinessException;

	List<SharedSpaceMember> members(String uuid, String accountUuid) throws BusinessException;

	Response findAll(String accountUuid, String sortOrder, String sortField, Set<String> nodeTypes, Integer pageNumber, Integer pageSize);

	SharedSpaceMember findMember(String memberUuid) throws BusinessException;

	SharedSpaceMember addMember(SharedSpaceMember member) throws BusinessException;

	SharedSpaceMember deleteMember(SharedSpaceMember member, String memberUuid) throws BusinessException;

	SharedSpaceMember updateMember(SharedSpaceMember member, String memberUuid, boolean force, Boolean propagate) throws BusinessException;

	SharedSpaceNode update(PatchDto patchNode, String uuid) throws BusinessException;
}
