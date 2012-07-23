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
package org.linagora.linshare.core.Facade;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;

public interface SecuredUrlFacade {
	
	boolean exists(String uuid, String urlPath);
	
	boolean isValid(String alea, String password);

	List<DocumentVo> getDocuments(String uuid, String password) throws BusinessException;

	boolean isPasswordProtected(String uuid) throws LinShareNotSuchElementException;
	
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid , String password, MailContainer mailContainer) throws BusinessException;
	
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password, MailContainer mailContainer) throws BusinessException;
}
