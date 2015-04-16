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
package org.linagora.linshare.view.tapestry.objects;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.utils.ArchiveZipStream;

public class FileStreamResponse implements StreamResponse{

	private InputStream inputStream;
	private long size;
	private String contentType;
	private String fileName;
	
	private static final String BINARY_MIME_TYPE  = "application/octet-stream";
	
	
	public FileStreamResponse(DocumentVo documentVo,InputStream stream){
		this.inputStream=stream;
		this.size = documentVo.getSize();
		this.fileName=documentVo.getFileName();
		
		//this.contentType = BINARY_MIME_TYPE; //always save the doc before reading it ....
		this.contentType=documentVo.getType();
	}
	
	public FileStreamResponse(SignatureVo signatureVo,InputStream stream){
		this.inputStream=stream;
		this.size = signatureVo.getSize();
		
		//we want unicity of the name of file
		//so we put this name signed_fileName.ext_idDatabase.xml
		this.fileName=signatureVo.getName()+"_"+signatureVo.getPersistenceId()+".xml"; 
		this.contentType=Signature.MIMETYPE;
	}
	
	public FileStreamResponse(ArchiveZipStream stream,String filenameResponse){
		
		this.inputStream=stream;
		this.size=(int) stream.getTempFile().length();
		
		if(filenameResponse==null)
		this.fileName=ArchiveZipStream.ARCHIVE_ZIP_DOWNLOAD_NAME;
		else 
		this.fileName=filenameResponse;
		
		this.contentType=BINARY_MIME_TYPE;
	}
	
	
	
	// *** getters/setters

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// *** methods for the interface StreamResponse implementation !!!
	
	public String getContentType() {
		return contentType;
	}
	
	public InputStream getStream() throws IOException {
		return inputStream;
	}

	public void prepareResponse(Response response) {
        
		//BUG WITH IE WHEN PRAGMA IS NO-CACHE solution is:
        //The proper solution to IE cache issues is to declare the attachment as "Pragma: private"
        //and "Cache-Control: private, must-revalidate" in the HTTP Response.
        //This allows MS-IE to save the content as a temporary file in its local cache,
        //but in not general public cache servers, before handing it off the plugin, e.g. Adobe Acrobat, to handle it.
		
		response.setHeader("Content-Length", String.valueOf(this.size));
        response.setHeader("Content-disposition", getContentDispositionHeader());
        response.setHeader("Content-Transfer-Encoding","none");
        
        //Pragma is a HTTP 1.0 directive that was retained in HTTP 1.1 for backward compatibility.
        //no-cache prevent caching in proxy
        response.setHeader("Pragma","private"); 
        
        //�cache-control: private�. It instructs proxies in the path not to cache the page. But it permits browsers to cache the page.
        //must-revalidate means the browser must revalidate the page against the server before serving it from cache
        
        
        //post-check Defines an interval in seconds after which an entity must be checked for freshness.
        //The check may happen after the user is shown the resource but ensures that on the next roundtrip
        //the cached copy will be up-to-date
        //pre-check Defines an interval in seconds after
        //which an entity must be checked for freshness prior to showing the user the resource.
        
        response.setHeader("Cache-Control","private,must-revalidate, post-check=0, pre-check=0");
        
        //Cache Control: max-age is the same as Expires header
        //Setting max-age to zero ensures that a page is never served from cache, but is always re-validated against the server
        
        //response.setHeader("Cache-Control","private,must-revalidate,max-age=0,post-check=0, pre-check=0");
        //response.setIntHeader("Expires", 0); //HTTP 1.0 directive that was retained for backward compatibility
	}

	private String getContentDispositionHeader() {
		String encodeFileName = null;
		try {
			URI uri = new URI(null, null, this.fileName, null);
			encodeFileName = uri.toASCIIString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("attachment; ");
		
		// Adding filename using the old way for old browser compatibility
		sb.append("filename=\""+this.fileName+"\"; ");
		
		// Adding UTF-8 encoded filename. If the browser do not support this parameter, it will use the old way.
		if(encodeFileName != null) {
			sb.append("filename*= UTF-8''" + encodeFileName);
		}
		return sb.toString();
	}
}
