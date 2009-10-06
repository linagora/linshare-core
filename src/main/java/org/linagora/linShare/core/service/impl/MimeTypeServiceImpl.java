/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.service.impl;

import java.util.List;

import org.linagora.linShare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linShare.core.domain.entities.AllowedMimeType;
import org.linagora.linShare.core.domain.entities.MimeTypeStatus;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AllowedMimeTypeRepository;
import org.linagora.linShare.core.service.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypeServiceImpl implements MimeTypeService {

    Logger logger = LoggerFactory.getLogger(MimeTypeServiceImpl.class);
    
    /**
     * database allowed mimetype
     */
    private final AllowedMimeTypeRepository allowedMimeTypeRepository;

    /**
     * supported mimetype by the provider implementation
     */
    private final MimeTypeMagicNumberDao mimeTypeMagicNumberDao;

    /** Constructor.
     * @param userRepository repository.
     */
    public MimeTypeServiceImpl(AllowedMimeTypeRepository allowedMimeTypeRepository, MimeTypeMagicNumberDao mimeTypeMagicNumberDao) {
        this.allowedMimeTypeRepository = allowedMimeTypeRepository;
        this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
    }

	public List<AllowedMimeType> getAllSupportedMimeType()
			throws BusinessException {
		return mimeTypeMagicNumberDao.getAllSupportedMimeType();
	}

	public List<AllowedMimeType> getAllowedMimeType() throws BusinessException {
		return allowedMimeTypeRepository.findAll();
	}

	public void createAllowedMimeType(List<AllowedMimeType> newlist) throws IllegalArgumentException, BusinessException {
		
		List<AllowedMimeType> oldlist = allowedMimeTypeRepository.findAll();
		
		for (AllowedMimeType one : oldlist) {
			allowedMimeTypeRepository.delete(one);
		}
		
		for (AllowedMimeType one : newlist) {
			allowedMimeTypeRepository.create(one);
		}
	}
	
	public boolean isAllowed(String mimeType) {

		boolean res = true;

		List<AllowedMimeType> list = allowedMimeTypeRepository.findByMimeType(mimeType);
		if (list != null && list.size() != 0) {
			for (AllowedMimeType allowedMimeType : list) {
				if(allowedMimeType.getStatus()!=MimeTypeStatus.AUTHORISED)
				res = false;
			}
		} else {
			res = false; //list is empty for this mime Type
		}
		
		return res;
	}

	public void saveOrUpdateAllowedMimeType(List<AllowedMimeType> list)
			throws BusinessException {
		allowedMimeTypeRepository.saveOrUpdateMimeType(list);
	}

	public MimeTypeStatus giveStatus(String mimeType) {
		
		MimeTypeStatus statusToReturn = MimeTypeStatus.DENIED;
		List<AllowedMimeType> list = allowedMimeTypeRepository.findByMimeType(mimeType);
		
		if (list != null && list.size() != 0) {
			statusToReturn = list.get(0).getStatus();
			//type mime exists in database (same as apperture at this time)
			//so check admin configuration
		} else {
			//type mime does not exist in database
			statusToReturn = MimeTypeStatus.AUTHORISED;
		}
		
		return statusToReturn;
	}

}
