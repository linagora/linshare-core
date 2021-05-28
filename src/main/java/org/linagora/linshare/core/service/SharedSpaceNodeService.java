/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface SharedSpaceNodeService {

	SharedSpaceNode find(Account authUser, Account actor, String uuid) throws BusinessException;
	
	SharedSpaceNode find(Account authUser, Account actor, String uuid, boolean withRole, boolean lastUpdater) throws BusinessException;

	SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;
	
	SharedSpaceNode updatePartial(Account authUser, Account actor, PatchDto node) throws BusinessException;

	SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	List<SharedSpaceNode> findAll(Account authUser, Account actor);

	/** Search some SharedSpaceNode by their name
	 **/
	List<SharedSpaceNode> searchByName(Account authUser, Account actor, String name) throws BusinessException;

	List<SharedSpaceMember> findAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid, String accountUuid);

	List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor);

	@Deprecated
	/**
	 * Only use to compability with threadFacade
	 *
	 */
	WorkGroupDto createWorkGroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	@Deprecated
	/**
	 * Only use to compability with threadFacade
	 *
	 */
	WorkGroupDto deleteWorkgroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	PageContainer<SharedSpaceNodeNested> findAll(Account authUser, Account actor, Account account, PageContainer<SharedSpaceNodeNested> container);

	List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor, boolean withRole, String parent);
}
