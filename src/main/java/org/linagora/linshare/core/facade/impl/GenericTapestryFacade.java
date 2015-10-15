/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.facade.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericTapestryFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(GenericTapestryFacade.class);


	protected final AccountService accountService;

	public GenericTapestryFacade(final AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	/*
	 * Helpers
	 */
	protected User getActor(UserVo userVo) throws BusinessException {
		Validate.notEmpty(userVo.getLsUuid(), "Missing actor uuid");
		User actor = (User) accountService.findByLsUuid(userVo.getLsUuid());
		if (actor == null) {
			logger.error("Can't find actor : " + userVo.getLsUuid());
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"You are not authorized to use this facade");
		}
		return actor;
	}

	protected File getTempFile(InputStream file, String discriminator, String fileName) {
		if (discriminator == null)  {
			discriminator = "";
		}
		// Legacy code, we need to extract extension for the dirty unstable LinThumbnail Module.
		// I hope some day we get rid of it !
		String extension = null;
		if (fileName != null) {
			int splitIdx = fileName.lastIndexOf('.');
			if (splitIdx > -1) {
				extension = fileName.substring(splitIdx, fileName.length());
			}
		}
		File tempFile = null;
		try {
			tempFile = File.createTempFile("linshare-" + discriminator + "-", extension);
			tempFile.deleteOnExit();
			IOUtils.transferTo(file, tempFile);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Can not generate temp file from input stream.");
		}
		return tempFile;
	}

	protected void deleteTempFile(File tempFile) {
		if (tempFile != null) {
			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception e) {
				logger.warn("Can not delete temp file : "
						+ tempFile.getAbsolutePath());
				logger.debug(e.getMessage(), e);
			}
		}
	}
}
