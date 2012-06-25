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
package org.linagora.linshare.core.service;

import java.util.List;

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

}
