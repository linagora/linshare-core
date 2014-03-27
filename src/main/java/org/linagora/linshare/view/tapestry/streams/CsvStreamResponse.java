/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.view.tapestry.streams;


import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import com.sun.xml.messaging.saaj.util.ByteInputStream;

public class CsvStreamResponse implements StreamResponse{

	
	private String chain;
	private int size=0;
	private String fileName;
	
	public CsvStreamResponse(String chain,String fileName){
		this.chain=chain;
		this.fileName=fileName;
		if(this.chain!=null){
			this.size=this.chain.getBytes().length;
		}
	}
	
	public String getContentType() {
		return "application/octet-stream";
	}

	public InputStream getStream() throws IOException {
		return new ByteInputStream(chain.getBytes(), size);
	}

	public void prepareResponse(Response response) {
		response.setContentLength(size);
        response.setHeader("Content-disposition", "attachment; filename="+fileName);
        response.setHeader("Content-Transfer-Encoding","none");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0, public");
        response.setIntHeader("Expires", 0);
		
	}
}
