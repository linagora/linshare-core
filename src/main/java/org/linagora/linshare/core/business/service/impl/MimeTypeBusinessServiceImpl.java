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
package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.MimeTypeBusinessService;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MimeTypeRepository;

public class MimeTypeBusinessServiceImpl implements MimeTypeBusinessService {

	private MimeTypeRepository mimeTypeRepository;

	public MimeTypeBusinessServiceImpl(MimeTypeRepository mimeTypeRepository) {
		this.mimeTypeRepository = mimeTypeRepository;
	}

	@Override
	public MimeType find(String uuid) throws BusinessException {
		MimeType mimeType = mimeTypeRepository.findByUuid(uuid);
		if (mimeType == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Can not find mimeType " + uuid);
		}
		return mimeType;
	}

	@Override
	public MimeType findByMimeType(MimePolicy mimePolicy, String mimeType) {
		return mimeTypeRepository.findByMimeType(mimePolicy, mimeType);
	}

	@Override
	public MimeType update(MimeType mimeType) throws BusinessException {
		MimeType entity = mimeTypeRepository.findByUuid(mimeType.getUuid());
		entity.setEnable(mimeType.getEnable());
		entity.setExtensions(mimeType.getExtensions());
		return mimeTypeRepository.update(entity);
	}

	@Override
	public void delete(MimeType mimeType) throws BusinessException {
		mimeTypeRepository.delete(mimeType);
	}
}
