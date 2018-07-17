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
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;

public class SharedSpaceNodeBusinessServiceImpl implements SharedSpaceNodeBusinessService {

	private SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;

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
	public void delete(SharedSpaceNode node) throws BusinessException {
		sharedSpaceNodeMongoRepository.delete(node);
	}

	@Override

	public SharedSpaceNode update(SharedSpaceNode foundNodeToUpdate, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		foundNodeToUpdate.setName(nodeToUpdate.getName());
		foundNodeToUpdate.setModificationDate(new Date());
		return sharedSpaceNodeMongoRepository.save(foundNodeToUpdate);
	}

	public List<SharedSpaceNode> findAll() throws BusinessException {
		return sharedSpaceNodeMongoRepository.findAll();
	}

}
