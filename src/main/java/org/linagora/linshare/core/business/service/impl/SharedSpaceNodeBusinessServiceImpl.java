/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;

public class SharedSpaceNodeBusinessServiceImpl implements SharedSpaceNodeBusinessService {

	protected SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;

	public SharedSpaceNodeBusinessServiceImpl(SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository) {
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
	}

	@Override
	public SharedSpaceNode find(String uuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByUuid(uuid);
	}

	@Override
	public SharedSpaceNode create(SharedSpaceNode node) throws BusinessException {
		return sharedSpaceNodeMongoRepository.insert(node);
	}

	@Override
	public List<SharedSpaceNode> findByNameAndParentUuid(String name, String parentUuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByNameAndParentUuid(name, parentUuid);
	}

	@Override
	public List<SharedSpaceNode> findByParentUuidAndType(String parentUuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByParentUuidAndNodeType(parentUuid, NodeType.WORK_GROUP);
	}

	@Override
	public void delete(SharedSpaceNode node) throws BusinessException {
		sharedSpaceNodeMongoRepository.delete(node);
	}

	@Override
	public SharedSpaceNode update(SharedSpaceNode foundNodeToUpdate, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		foundNodeToUpdate.setName(nodeToUpdate.getName());
		if (nodeToUpdate.getVersioningParameters() != null) {
			foundNodeToUpdate.setVersioningParameters(nodeToUpdate.getVersioningParameters());
		}
		foundNodeToUpdate.setModificationDate(new Date());
		return sharedSpaceNodeMongoRepository.save(foundNodeToUpdate);
	}

	public List<SharedSpaceNode> findAll() throws BusinessException {
		return sharedSpaceNodeMongoRepository.findAll();
	}

	@Override
	public List <SharedSpaceNode> searchByName(String name) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByName(name);
	}

}
