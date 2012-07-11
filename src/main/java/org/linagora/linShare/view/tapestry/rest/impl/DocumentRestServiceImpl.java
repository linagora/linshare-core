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
package org.linagora.linShare.view.tapestry.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.domain.constants.DocumentType;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.rest.DocumentRestService;
import org.linagora.linShare.view.tapestry.services.MyMultipartDecoder;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.linShare.view.tapestry.utils.XSSFilter;
import org.linagora.restmarshaller.Marshaller;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.antera.t5restfulws.RestfulWebMethod;



/**
 * Offers Document related service
 * @author ncharles
 *
 */
public class DocumentRestServiceImpl implements DocumentRestService {
	
	private final ApplicationStateManager applicationStateManager; 
	private final SearchDocumentFacade searchDocumentFacade;
	private final DocumentFacade documentFacade;

	private final MyMultipartDecoder myMultipartDecoder;
	
	private final PropertiesSymbolProvider propertiesSymbolProvider;
	
	private final Marshaller xstreamMarshaller;

    private final MailContainerBuilder mailContainerBuilder;
    private final Policy antiSamyPolicy;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentRestServiceImpl.class);

    private static final int VIRUS_DETECTED_HTTP_STATUS = 451;

	public DocumentRestServiceImpl( final ApplicationStateManager applicationStateManager,
			final SearchDocumentFacade searchDocumentFacade,
			final DocumentFacade documentFacade,
			final MyMultipartDecoder myMultipartDecoder,
			final PropertiesSymbolProvider propertiesSymbolProvider,
			final Marshaller xstreamMarshaller,
            final MailContainerBuilder mailContainerBuilder,
            final Policy antiSamyPolicy) {
		super();
		this.applicationStateManager = applicationStateManager;
		this.searchDocumentFacade = searchDocumentFacade;
		this.documentFacade = documentFacade;
		this.xstreamMarshaller = xstreamMarshaller;
		this.myMultipartDecoder = myMultipartDecoder;
		this.propertiesSymbolProvider = propertiesSymbolProvider;
        this.mailContainerBuilder = mailContainerBuilder;
        this.antiSamyPolicy = antiSamyPolicy;
	}
	

	/* (non-Javadoc)
	 * @see org.linagora.linShare.view.tapestry.rest.impl.DocumentRestService#getdocumentlist(org.apache.tapestry5.services.Request, org.apache.tapestry5.services.Response)
	 */
	@RestfulWebMethod
	public void getdocumentlist(Request request, Response response) throws IOException  {
		// fetch the logged user
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		logger.debug("Showing " + actor.getMail() + " document list");
		List<DocumentVo> list = searchDocumentFacade.retrieveDocument(actor);
		
		if (list == null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "No such document");
			return;
		}
	
		
		String xml = xstreamMarshaller.toXml(list);
		
                OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
	}
	
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.view.tapestry.rest.impl.DocumentRestService#getdocument(org.apache.tapestry5.services.Request, org.apache.tapestry5.services.Response, java.lang.String)
	 */
	@RestfulWebMethod
	public void getdocument(Request request, Response response, String uuid) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		DocumentVo docVo = documentFacade.getDocument(actor.getLogin(), uuid);

		if (docVo!= null ) {
			

			InputStream myStream;
			try {
				myStream = documentFacade.retrieveFileStream(docVo, actor);
				response.setStatus(HttpStatus.SC_OK);
				OutputStream outer = response.getOutputStream("application/octet-stream");
				response.setContentLength(docVo.getSize().intValue());
				response.setHeader("Content-disposition", "attachment; filename="+docVo.getFileName());
				response.setHeader("Content-Transfer-Encoding","none");
				response.setHeader("Pragma","no-cache");
				response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0, public");
				response.setIntHeader("Expires", 0);
				
				writeToOutputStream(myStream, outer);
				outer.flush();
				outer.close();
			} catch (BusinessException e) {
				logger.error("Could not retrieve document " + uuid +" for user " + actor.getMail() + " : " + e.getMessage() );
				response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
				response.sendError(HttpStatus.SC_NOT_FOUND, "document not found");
			}
			

		} else {
			logger.info("Did not found  document " + uuid +" for user " + actor.getMail() );
			response.sendError(HttpStatus.SC_NOT_FOUND, "document not found");
		}
	}

        @RestfulWebMethod
	public void removedocument(Request request, Response response, String uuid) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}

		DocumentVo docVo = documentFacade.getDocument(actor.getLogin(), uuid);

		if (docVo!= null ) {

			try {
                                MailContainer mC = mailContainerBuilder.buildMailContainer(actor, null);
				documentFacade.removeDocument(actor,docVo,mC);
				response.setStatus(HttpStatus.SC_OK);
			} catch (BusinessException e) {
				logger.error("Could not remove document " + uuid +" for user " + actor.getMail() + " : " + e.getMessage() );
				response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
				response.sendError(HttpStatus.SC_EXPECTATION_FAILED, "could not remove the document");
			}


		} else {
			logger.info("Did not found  document " + uuid +" for user " + actor.getMail() );
			response.sendError(HttpStatus.SC_NOT_FOUND, "document not found");
		}
	}
        	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.view.tapestry.rest.impl.DocumentRestService#uploadfile(org.apache.tapestry5.services.Request, org.apache.tapestry5.services.Response)
	 */
	@RestfulWebMethod
	public void uploadfile(Request request, Response response) throws IOException
	{
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if (!"POST".equals(request.getMethod())) {
			String msg= "Method not allowed";
			logger.error(msg);
			response.sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, msg );
			response.setHeader("Allow", "POST");
			return;

		}

		logger.debug("request.getParameterNames() : " + request.getParameterNames());
		if (request.getParameterNames().size()<1) {
			String msg= "Not enough parameters";
			logger.error(msg);
			response.sendError(HttpStatus.SC_BAD_REQUEST, msg);
			return;
		}

		if (!request.getParameterNames().contains("file")) {
			String msg= "Missing parameter file";
			logger.error(msg);
			response.sendError(HttpStatus.SC_BAD_REQUEST, msg);
			return;

		}
		
		if ((actor.isGuest() && !actor.isUpload())) {
			String msg= "You are not authorized to use this service";
			logger.error(msg);
			response.sendError(HttpStatus.SC_FORBIDDEN, msg);
			return;
		}
		

		UploadedFile theFile = myMultipartDecoder.getFileUpload("file");

		if (theFile==null) {
			logger.error("No file uploaded by user " +actor.getMail() );
			response.sendError(HttpStatus.SC_BAD_REQUEST, "The file is not provided");
			return;

		}
		
		String fileComment = null;
		if (request.getParameterNames().contains("comment")) {
			fileComment = request.getParameter("comment");
			logger.debug("comment : " + fileComment);
		}
		
		long maxFileSize = -1;
		try {
			maxFileSize = documentFacade.getUserMaxFileSize(actor);
		} catch (BusinessException e1) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load parameters");
		}
		long userFreeSpace = 0;
		try {
			userFreeSpace = documentFacade.getUserAvailableQuota(actor);
		} catch (BusinessException e1) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load user available quota");
		}
		
		if (maxFileSize > 0 && theFile.getSize() > maxFileSize) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "The file is larger than the maximum allowed");
			return;
		}
		if (theFile.getSize() > userFreeSpace) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "The file is larger than the remaining user space");
			return;
		}

		String mimeType;
		try {
			mimeType = documentFacade.getMimeType(theFile.getStream(), theFile.getFilePath());
			if(null==mimeType){
				mimeType = theFile.getContentType();
			}
		} catch (BusinessException e) {
			mimeType = theFile.getContentType();
		}
                
		try {
			XSSFilter filter = new XSSFilter(antiSamyPolicy, null);
			String fileName = null ;
			if (request.getParameterNames().contains("filename")) {
				fileName = request.getParameter("filename");
			} else {
				fileName = theFile.getFileName();
			}
			fileName = filter.clean(fileName);
			logger.debug("fileName : " + fileName);
			DocumentVo doc = documentFacade.insertFile(theFile.getStream(), theFile.getSize(), fileName, mimeType, actor );
			
			if(fileComment != null) {
				fileComment = filter.clean(fileComment);
				documentFacade.updateFileProperties(doc.getIdentifier(), fileName, fileComment);
			}
			
			
			OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
			
			
			response.setStatus(HttpStatus.SC_CREATED);
			
			String url = propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");
			response.setHeader("Location", url +"documentrestservice/getdocument/" + doc.getIdentifier());
			
			String xml = xstreamMarshaller.toXml(doc);
			writer.append(xml);
			writer.flush();
			writer.close();
		}  catch (BusinessException e) {
			logger.error("Could not insert file for user  " +actor.getMail()  + " : " + e.getCause());
                       if (e.getErrorCode().getCode() == BusinessErrorCode.FILE_CONTAINS_VIRUS.getCode()) {
                        response.setHeader("BusinessError", e.getErrorCode().getCode() + "");
                        response.sendError(VIRUS_DETECTED_HTTP_STATUS, "Error " + e);
                    } else {
                        response.setHeader("BusinessError", e.getErrorCode().getCode() + "");
                        response.sendError(HttpStatus.SC_METHOD_FAILURE, "Error " + e);
                    }
                    return;

		}
		
	}

	@RestfulWebMethod
	public void finddocument(Request request, Response response) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if (!"POST".equals(request.getMethod())) {
			response.sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
			response.setHeader("Allow", "POST");
			return;

		}
                
		if (request.getParameterNames().size()<1) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Not enough parameters");
			return;
		}
		
		
		String name=request.getParameter("name");
		String type  = request.getParameter("type"); // this is the MIME Type
		Boolean shared = "true".equalsIgnoreCase(request.getParameter("shared"));
		
		String sizeMinTmp = request.getParameter("sizeMin");
		Long sizeMin= null;
		if (sizeMinTmp!=null) {
			try {
				sizeMin = Long.parseLong(sizeMinTmp);
			} catch (Exception e) {
				// does nothing
			}
		}
		
		String sizeMaxTmp = request.getParameter("sizeMax");
		Long sizeMax= null;
		if (sizeMaxTmp!=null) {
			try {
				sizeMax = Long.parseLong(sizeMaxTmp);
			} catch (Exception e) {
				// does nothing
			}
		}
		
		DocumentType documentType = DocumentType.BOTH;
		String docType = request.getParameter("documentType");
		if (docType!=null) {
			// test the value
			if ("SHARED".equalsIgnoreCase(docType)) {
				documentType = DocumentType.SHARED;				
			}
			if ("OWNED".equalsIgnoreCase(docType)) {
				documentType = DocumentType.OWNED;		
			}
			
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		
		// begin date
		String beginDate  = request.getParameter("dateBegin");
		Calendar dateBegin = null; 
		if (beginDate != null ){
			try {
				Date date = sdf.parse(beginDate);
				dateBegin = new GregorianCalendar();
				dateBegin.setTime(date);
			} catch (Exception e) {
				// does nothing
			}
		}
		
		// end date
		String endDate  = request.getParameter("dateEnd");
		Calendar dateEnd= null; 
		if (endDate != null ){
			try {
				Date date = sdf.parse(endDate);
				dateEnd = new GregorianCalendar();
				dateEnd.setTime(date);
			} catch (Exception e) {
				// does nothing
			}
		}
		
		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(actor, name, sizeMin, sizeMax, type, shared, dateBegin, dateEnd, null, null,documentType);
		
		List<DocumentVo> list = searchDocumentFacade.retrieveDocumentContainsCriterion(searchDocumentCriterion);
		
		if (list == null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "No such document");
			return;
		}
	
		String xml = xstreamMarshaller.toXml(list);
		
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
		
	}

	@RestfulWebMethod
	public void getdocumentproperties(Request request, Response response,String uid) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}



		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(actor, null, null, null, null, null, null, null, null, null,null);

		List<DocumentVo> list = searchDocumentFacade.retrieveDocumentContainsCriterion(searchDocumentCriterion);


		if (list == null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "No such document");
			return;
		}

            for (DocumentVo dV : list) {
                if (dV.getIdentifier().equals(uid)) {

                    String xml = xstreamMarshaller.toXml(dV);
                    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
                    response.setStatus(HttpStatus.SC_OK);
                    writer.append(xml);
                    writer.flush();
                    writer.close();
                    break;
                }
            }
		

	}
	
	/**
	 * Write all the content on the inputStream to the outputStream, using 4kB blocks
	 * @param in the input stream
	 * @param out the destination streammimeType
	 * @throws IOException
	 */
	protected void writeToOutputStream(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[65536];
		int readBytes;
		while ((readBytes=in.read(b))>0) {
			out.write(b, 0, readBytes);
		}

	}
	
	@RestfulWebMethod
    public void getFreeSpace(Request request, Response response) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}

		if ( (actor.isGuest() && (!actor.isUpload()))) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}

        Long freeSpace = 0L;
		try {
			freeSpace = documentFacade.getUserAvailableQuota(actor);
		} catch (BusinessException e) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load free space");
		}

		String xml = xstreamMarshaller.toXml(freeSpace);
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
    }

	@RestfulWebMethod
    public void getMaxFileSize(Request request, Response response) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);

		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
        
        Long maxFileSize = null;
        try {
            maxFileSize = documentFacade.getUserMaxFileSize(actor);
        } catch (BusinessException ex) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load parameters");
        }

		String xml = xstreamMarshaller.toXml(maxFileSize);
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream("text/xml"),"UTF-8");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();

    }

}
