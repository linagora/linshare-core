/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AllowedMimeTypeRepository;
import org.linagora.linshare.core.service.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class MimeTypeServiceImpl implements MimeTypeService {

	private static final Logger logger = LoggerFactory.getLogger(MimeTypeServiceImpl.class);

	/**
	 * database allowed mimetype
	 */
	private final AllowedMimeTypeRepository allowedMimeTypeRepository;

	/**
	 * supported mimetype by the provider implementation
	 */
	private final MimeTypeMagicNumberDao mimeTypeMagicNumberDao;

	/**
	 * Constructor.
	 * 
	 * @param userRepository
	 *            repository.
	 */
	public MimeTypeServiceImpl(AllowedMimeTypeRepository allowedMimeTypeRepository, MimeTypeMagicNumberDao mimeTypeMagicNumberDao) {
		this.allowedMimeTypeRepository = allowedMimeTypeRepository;
		this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
	}

	@Override
	public List<AllowedMimeType> getAllowedMimeType() throws BusinessException {

		//reading from database
		List<AllowedMimeType> list = allowedMimeTypeRepository.findAll();
		if(list.size() == 0) {
			// reading from provider if table is empty
			list = mimeTypeMagicNumberDao.getAllSupportedMimeType();
		}
		Collections.sort(list);
		return list;
	}

	@Override
	public void createAllowedMimeType(List<AllowedMimeType> newlist) throws IllegalArgumentException, BusinessException {

		List<AllowedMimeType> oldlist = allowedMimeTypeRepository.findAll();

		for (AllowedMimeType one : oldlist) {
			allowedMimeTypeRepository.delete(one);
		}

		for (AllowedMimeType one : newlist) {
			allowedMimeTypeRepository.create(one);
		}
	}

	@Override
	public boolean isAllowed(String mimeType) {

		boolean res = true;

		List<AllowedMimeType> list = allowedMimeTypeRepository.findByMimeType(mimeType);
		if (list != null && list.size() != 0) {
			for (AllowedMimeType allowedMimeType : list) {
				if (allowedMimeType.getStatus() != MimeTypeStatus.AUTHORISED)
					res = false;
			}
		} else {
			res = false; // list is empty for this mime Type
		}

		return res;
	}

	@Override
	public void saveOrUpdateAllowedMimeType(List<AllowedMimeType> list) throws BusinessException {
		allowedMimeTypeRepository.saveOrUpdateMimeType(list);
	}

	@Override
	public MimeTypeStatus giveStatus(String mimeType) {

		MimeTypeStatus statusToReturn = MimeTypeStatus.DENIED;
		List<AllowedMimeType> list = allowedMimeTypeRepository.findByMimeType(mimeType);

		if (list != null && list.size() != 0) {
			statusToReturn = list.get(0).getStatus();
			// type mime exists in database (same as apperture at this time)
			// so check admin configuration
		} else {
			// type mime does not exist in database
			statusToReturn = MimeTypeStatus.AUTHORISED;
		}

		return statusToReturn;
	}

	@Override
	public void checkFileMimeType(String fileName, String mimeType, Account owner) throws BusinessException {
		// use mimetype filtering
		if (logger.isDebugEnabled()) {
			logger.debug("2)check the mimetype:" + mimeType);
		}

		// if we refuse some typemime
		if (mimeType != null) {
			MimeTypeStatus status = giveStatus(mimeType);

			if (status == MimeTypeStatus.DENIED) {
				if (logger.isDebugEnabled())
					logger.debug("mimetype not allowed: " + mimeType);
				String[] extras = { fileName };
				throw new BusinessException(BusinessErrorCode.FILE_MIME_NOT_ALLOWED, "This kind of file is not allowed: " + mimeType, extras);
			} else if (status == MimeTypeStatus.WARN) {
				if (logger.isInfoEnabled())
					logger.info("mimetype warning: " + mimeType + "for user: " + owner.getLsUuid());
			}
		} else {
			// mimetype is null ?
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_MIME_NOT_ALLOWED, "mimetype is empty for this file" + mimeType, extras);
		}
	}

}
