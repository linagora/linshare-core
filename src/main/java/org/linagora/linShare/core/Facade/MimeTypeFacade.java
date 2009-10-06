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
package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linShare.core.exception.BusinessException;


/** Facade entry for mime type management.
 */
public interface MimeTypeFacade {

    public List<AllowedMimeTypeVO>  getAllSupportedMimeType() throws BusinessException;
    public List<AllowedMimeTypeVO>  getAllowedMimeType() throws BusinessException;
    public boolean isAllowed(String mimeType);
    public void createAllowedMimeType(List<AllowedMimeTypeVO> newlist) throws BusinessException;
    public void saveOrUpdateAllowedMimeType(List<AllowedMimeTypeVO> list) throws BusinessException;
}