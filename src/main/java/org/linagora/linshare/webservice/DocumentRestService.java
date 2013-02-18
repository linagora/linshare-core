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
 *   License along with Linshare.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.webservice;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Path;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.webservice.dto.DocumentDto;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.linagora.linshare.webservice.dto.SimpleLongValue;


/**
 * REST jaxRS interface
 */


@Path("/rest/document")
public interface DocumentRestService {

	public List<DocumentDto> getDocuments();
	public SimpleLongValue getUserMaxFileSize();
	public SimpleLongValue getAvailableSize();
	public DocumentDto uploadfile(InputStream theFile, String description, String givenFileName, MultipartBody body);
	public DocumentDto addDocumentXop(DocumentAttachement doca);
}