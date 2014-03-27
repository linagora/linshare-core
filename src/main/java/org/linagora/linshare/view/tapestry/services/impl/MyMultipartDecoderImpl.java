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
package org.linagora.linshare.view.tapestry.services.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.upload.internal.services.ParametersServletRequestWrapper;
import org.apache.tapestry5.upload.internal.services.UploadedFileItem;
import org.apache.tapestry5.upload.services.UploadSymbols;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linshare.view.tapestry.services.MyMultipartDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyMultipartDecoderImpl  implements  MyMultipartDecoder
{
	

	final private static Logger logger=LoggerFactory.getLogger(MyMultipartDecoderImpl.class);
	
    private final Map<String, UploadedFileItem> uploads = CollectionFactory.newMap();

    private final FileItemFactory fileItemFactory;

    private final long maxRequestSize;

    private long maxFileSize;

    private final String requestEncoding;

    private FileUploadException uploadException;
    
    public MyMultipartDecoderImpl(
    		
    		FileItemFactory fileItemFactory,

            @Symbol(UploadSymbols.REQUESTSIZE_MAX)
            long maxRequestSize,

            @Symbol(UploadSymbols.FILESIZE_MAX)
            long maxFileSize,

            @Inject @Symbol(SymbolConstants.CHARSET)
            String requestEncoding)
    
    {

    	this.fileItemFactory = fileItemFactory;
        this.maxRequestSize = maxRequestSize;
        this.maxFileSize = maxFileSize;
        this.requestEncoding = requestEncoding;
    }

    public UploadedFile getFileUpload(String parameterName)
    {
        return uploads.get(parameterName);
    }

    public HttpServletRequest decode(HttpServletRequest request)
    {

       
        
        try
        {
            request.setCharacterEncoding(requestEncoding);
        }
        catch (UnsupportedEncodingException ex)
        {
        	logger.error("error while uploading the files", ex);
            throw new RuntimeException(ex);
        }
/*
        if   (request.getContentLength() > maxRequestSize) {
            uploadException = new FileUploadException("Upload to big"); 
            return request;
        	
        }
  */   

        
        List<FileItem> fileItems = parseRequest(request);

        return processFileItems(request, fileItems);
    }

    public void threadDidCleanup()
    {
        for (UploadedFileItem uploaded : uploads.values())
        {
            uploaded.cleanup();
        }
    }

    @SuppressWarnings("unchecked")
    protected List<FileItem> parseRequest(HttpServletRequest request)
    {
        try
        {
            return createFileUpload().parseRequest(request);
        }
        catch (FileUploadException ex)
        {
        	logger.error("error while uploading the files", ex);
            uploadException = ex;

            return Collections.emptyList();
        }
    }

    protected ServletFileUpload createFileUpload()
    {
	   ServletFileUpload upload = new ServletFileUpload(fileItemFactory);

        // set maximum file upload size
        upload.setSizeMax(-1);
        upload.setFileSizeMax(-1);

        ProgressListener progressListener = new ProgressListener(){
        	   private long megaBytes = -1;
        	   public void update(long pBytesRead, long pContentLength, int pItems) {
        	       long mBytes = pBytesRead / 1000000;
        	       if (megaBytes == mBytes) {
        	           return;
        	       }
        	       megaBytes = mBytes;
        	       
        	       if(logger.isDebugEnabled()){
	        	       logger.debug("We are currently reading item " + pItems);
	        	    	   
	        	       if (pContentLength == -1) {
	        	    	   logger.debug("So far, " + pBytesRead + " bytes have been read.");
	        	       } else {
	        	    	   logger.debug("So far, " + pBytesRead + " of " + pContentLength
	        	                              + " bytes have been read.");
	        	       }
        	       }
        	   }
        	};


        	upload.setProgressListener(progressListener);

        
        return upload;
    }

    protected HttpServletRequest processFileItems(HttpServletRequest request, List<FileItem> fileItems)
    {
    	  if (uploadException == null && fileItems.isEmpty())
          {
              return request;
          }

        ParametersServletRequestWrapper wrapper = new ParametersServletRequestWrapper(request);

        for (FileItem item : fileItems)
        {
            if (item.isFormField())
            {
                String fieldValue;

                try
                {

                    fieldValue = item.getString(requestEncoding);
                }
                catch (UnsupportedEncodingException ex)
                {
                	logger.error("error while uploading the file " + item.getName(), ex);
                    throw new RuntimeException(ex);
                }

                wrapper.addParameter(item.getFieldName(), fieldValue);
            }
            else
            {
                wrapper.addParameter(item.getFieldName(), item.getName());
                addUploadedFile(item.getFieldName(), new UploadedFileItem(item));
            }
        }

        return wrapper;
    }

    protected void addUploadedFile(String name, UploadedFileItem file)
    {
        uploads.put(name, file);
    }

    public FileUploadException getUploadException()
    {
        return uploadException;
    }
    
    public void cleanException() {
    	this.uploadException = null;
    }
}
