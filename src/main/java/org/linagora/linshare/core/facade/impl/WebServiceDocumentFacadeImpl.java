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
package org.linagora.linshare.core.facade.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceDocumentFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.webservice.dto.Document;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebServiceDocumentFacadeImpl implements WebServiceDocumentFacade {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceDocumentFacade.class);
    
	private final DocumentEntryService documentEntryService;
	private final AccountService accountService;

	public WebServiceDocumentFacadeImpl(final DocumentEntryService documentEntryService, final AccountService accountService) {
		this.documentEntryService = documentEntryService;
		this.accountService = accountService;
	}
	
	
	@Override
	public List<Document> getDocuments() throws BusinessException {
		User actor = getAuthentication();
		 
		List<DocumentEntry> docs;
		try {
			docs = documentEntryService.findAllMyDocumentEntries(actor, actor);
		} catch (BusinessException e) {
			throw e;
		}
 
		if (docs == null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_NOT_FOUND, "No such document");
		}
		
		return convertDocumentEntryList (docs);
	}

	
	@Override
	public Document uploadfile(InputStream fi, String filename, String fileComment) throws BusinessException{
		DocumentEntry res;
		FileInputStream tempfi = null;
		File tempFile = null;
		
		try {
			User actor = getAuthentication();
			
			//here we need tempFile to evaluate length of the stream ...
			tempFile = getTempFile(fi,filename);
			tempfi = new FileInputStream(tempFile);
			res =  documentEntryService.createDocumentEntry(actor, tempfi, tempFile.length(), filename);
			documentEntryService.updateFileProperties(actor, res.getUuid(), res.getName(), fileComment);
		} catch (BusinessException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT, "unable to upload", e);
		} catch (IOException e) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT, "unable to upload", e);
		} finally {
			if (tempfi != null)
				try {
					tempfi.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			if (!tempFile.delete()) {
				tempFile.deleteOnExit();
			}
		}
		
		return new Document(res);
	}
	

	
	@Override
	public User checkAuthentication() throws BusinessException {
		
		User actor = getAuthentication();
		
		if (actor== null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		
		return actor;
	}
	
	

	@Override
	public Document addDocumentXop(DocumentAttachement doca)  throws BusinessException {
    	DocumentEntry res;
    	FileInputStream tempfi = null;
    	File tempFile = null;
    	
    	try {
    		User actor = getAuthentication();
    		DataHandler dh = doca.getDocument();
    		InputStream in = dh.getInputStream();
			 
			//here we need tempFile to evaluate length of the stream ...
			tempFile = getTempFile(in, doca.getFilename());
			tempfi = new FileInputStream(tempFile);
			
			res =  documentEntryService.createDocumentEntry(actor, tempfi, tempFile.length(), doca.getFilename());
			
			//mandatory ?
		 	String comment = (doca.getComment() == null)? "" : doca.getComment();
			
			documentEntryService.updateFileProperties(actor, res.getUuid(), res.getName(), comment);
		} catch (IOException e) {
			throw  new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT, "unable to upload",e);
		} catch (BusinessException e) {
			throw e;
		} finally {
			if (tempfi != null) {
				try {
					tempfi.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
			if (!tempFile.delete()) {
				tempFile.deleteOnExit();
			}
		}
		
		return new Document(res);
	}
	
	@Override
	public Long getUserMaxFileSize() throws BusinessException {
		
		Long res;
		
		try {
			User actor = getAuthentication();
			res = documentEntryService.getUserMaxFileSize(actor);
		} catch (BusinessException e) {
			throw e;
		}
		return res;
	}
	
	@Override
	public Long getAvailableSize() throws BusinessException {
		Long res;
		
		try {
			User actor = getAuthentication();
			res = documentEntryService.getAvailableSize(actor);
		} catch (BusinessException e) {
			throw e;
		}
		return res;
	}
	
	
	
	//#############  utility methods
	
	private File getTempFile(InputStream stream, String fileName) throws IOException {
		
		File tempFile = null;
		BufferedOutputStream bof = null;
			
			try {
				int splitIdx = fileName.lastIndexOf('.');
				String extension = "";
				if(splitIdx>-1){
					extension = fileName.substring(splitIdx, fileName.length());
				}
				
				tempFile = File.createTempFile("linshare", extension); //we need to keep the extension for the thumbnail generator
				bof = new BufferedOutputStream(new FileOutputStream(tempFile));
				
				// Transfer bytes from in to out
				byte[] buf = new byte[20480];
				int len;
				while ((len = stream.read(buf)) > 0) {
					bof.write(buf, 0, len);
				}

				bof.flush();
				
			} catch (IOException e) {
				throw e;
			} finally {
				if(bof!=null) bof.close();
			}
		
		return tempFile;
	}
	
	
	private User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
	     String name =  (auth != null) ? auth.getName() : null; //get logged in username
	     if (name == null) {
	    	 return null;
	     }
	     User user = (User) accountService.findByLsUid(name);
	     return user;
	}

	
	private static List<Document> convertDocumentEntryList(List<DocumentEntry> input) {

		if (input == null)
			return null;

		List<Document> output = new ArrayList<Document>();
		for (DocumentEntry var : input) {
			output.add(new Document(var));
		}
		return output;
	}

}
