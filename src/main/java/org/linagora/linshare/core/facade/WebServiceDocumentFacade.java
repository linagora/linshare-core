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
package org.linagora.linshare.core.facade;

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.DocumentDto;
import org.linagora.linshare.webservice.dto.DocumentAttachement;


public interface WebServiceDocumentFacade {
	
	
	public User checkAuthentication() throws BusinessException;
	public List<DocumentDto> getDocuments() throws BusinessException;
	public DocumentDto addDocumentXop(DocumentAttachement doca) throws BusinessException;
	public Long getUserMaxFileSize() throws BusinessException;
	public Long getAvailableSize() throws BusinessException;
	public DocumentDto uploadfile(InputStream fi, String filename, String description) throws BusinessException;
}
