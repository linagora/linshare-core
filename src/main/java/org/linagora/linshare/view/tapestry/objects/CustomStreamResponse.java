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
package org.linagora.linshare.view.tapestry.objects;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.objects.FileInfo;

public class CustomStreamResponse implements StreamResponse{

	private FileInfo info;
	private InputStream stream;
	
	public CustomStreamResponse(FileInfo info,InputStream stream){
		this.info=info;
		this.stream=stream;
	}
	
	public String getContentType() {
		return info.getMimeType();
		
	}

	public InputStream getStream() throws IOException {
		return stream;
	}

	public void prepareResponse(Response response) {
        response.setHeader("Content-disposition", "attachment; filename="+info.getName());
        response.setHeader("Content-Transfer-Encoding","none");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0, public");
        response.setIntHeader("Expires", 0);
		
	}

}
