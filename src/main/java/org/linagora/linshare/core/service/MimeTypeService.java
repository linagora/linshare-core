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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.exception.BusinessException;

public interface MimeTypeService {

        /**
         * find all supported mime type supplied by the implementation (apperture)
         * @return
         * @throws BusinessException
         */
		public List<AllowedMimeType>  getAllSupportedMimeType() throws BusinessException;
        
		/**
		 * return allowed mime type from database
		 * @return
		 * @throws BusinessException
		 */
		public List<AllowedMimeType>  getAllowedMimeType() throws BusinessException;
        
		
		/**
		 * return false if mime type does not exist or if it is not set as allowed
		 * @param mimeType to check
		 * @return
		 */
		public boolean isAllowed(String mimeType);
		
		/**
		 * delete and replace all entries with the given one
		 * @param list of mime type to replace the old ones
		 * @throws BusinessException
		 */
		public void createAllowedMimeType(List<AllowedMimeType> list) throws BusinessException;
		
		/**
		 * update the list of mime type which is given as parameter
		 * @param list
		 * @throws BusinessException
		 */
		public void saveOrUpdateAllowedMimeType(List<AllowedMimeType> list) throws BusinessException;


		/**
		 * give the status associated with the mime type in database
		 * if many occurences return the status of the fist entry
		 * if the mime type does not exist return MimeTypeStatus.AUTHORISED
		 * @param mimeType
		 * @return MimeTypeStatus
		 */
		public MimeTypeStatus giveStatus(String mimeType);
		
		
		public void checkFileMimeType(String fileName, String mimeType, Account owner) throws BusinessException;

}
