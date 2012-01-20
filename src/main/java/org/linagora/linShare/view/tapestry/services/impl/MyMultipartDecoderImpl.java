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
package org.linagora.linShare.view.tapestry.services.impl;

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
import org.linagora.linShare.view.tapestry.services.MyMultipartDecoder;
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
