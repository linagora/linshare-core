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
import java.io.PrintWriter;
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
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.domain.enums.DocumentType;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.rest.DocumentRestService;
import org.linagora.linShare.view.tapestry.services.MyMultipartDecoder;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.restmarshaller.Marshaller;
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
    private final ParameterFacade parameterFacade;

	private final MyMultipartDecoder myMultipartDecoder;
	
	private final PropertiesSymbolProvider propertiesSymbolProvider;
	
	private final Marshaller xstreamMarshaller;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentRestServiceImpl.class);
	
	public DocumentRestServiceImpl( final ApplicationStateManager applicationStateManager,
			final SearchDocumentFacade searchDocumentFacade,
			final DocumentFacade documentFacade,
            final ParameterFacade parameterFacade,
			final MyMultipartDecoder myMultipartDecoder,
			final PropertiesSymbolProvider propertiesSymbolProvider,
			final Marshaller xstreamMarshaller) {
		super();
		this.applicationStateManager = applicationStateManager;
		this.searchDocumentFacade = searchDocumentFacade;
		this.documentFacade = documentFacade;
        this.parameterFacade = parameterFacade;
		this.xstreamMarshaller = xstreamMarshaller;
		this.myMultipartDecoder = myMultipartDecoder;
		this.propertiesSymbolProvider = propertiesSymbolProvider;
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
		
		PrintWriter writer = response.getPrintWriter("text/xml");
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
			response.sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
			response.setHeader("Allow", "POST");
			return;

		}

		if (request.getParameterNames().size()<1) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Not enough parameters");
			return;
		}

		if (!request.getParameterNames().contains("file")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter file");
			return;

		}
		
		if ((actor.isGuest() && !actor.isUpload())) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		

		UploadedFile theFile = myMultipartDecoder.getFileUpload("file");

		if (theFile==null) {
			logger.error("No file uploaded by user " +actor.getMail() );
			response.sendError(HttpStatus.SC_BAD_REQUEST, "The file is not provided");
			return;

		}
		
		long maxFileSize = -1;
		try {
			maxFileSize = parameterFacade.loadConfig().getFileSizeMax();
		} catch (BusinessException e1) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load parameters");
		}
		long userFreeSpace = documentFacade.getUserAvailableQuota(actor);
		
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
			DocumentVo doc = documentFacade.insertFile(theFile.getStream(), theFile.getSize(), theFile.getFileName(), mimeType, actor );
			
			
			PrintWriter writer = response.getPrintWriter("text/xml");
			
			
			response.setStatus(HttpStatus.SC_CREATED);
			
			String url = propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");
			response.setHeader("Location", url +"documentrestservice/getdocument/" + doc.getIdentifier());
			
			String xml = xstreamMarshaller.toXml(doc);
			writer.append(xml);
			writer.flush();
			writer.close();
		}  catch (BusinessException e) {
			logger.error("Could not insert file for user  " +actor.getMail()  + " : " + e.getCause());
			response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Error " + e);
			
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
		
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
		
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

        Long freeSpace = documentFacade.getUserAvailableQuota(actor);

		String xml = xstreamMarshaller.toXml(freeSpace);
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
    }

	@RestfulWebMethod
    public void getMaxFileSize(Request request, Response response) throws IOException {
        Long maxFileSize = null;
        try {
            maxFileSize = parameterFacade.loadConfig().getFileSizeMax();
        } catch (BusinessException ex) {
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load parameters");
        }

		String xml = xstreamMarshaller.toXml(maxFileSize);
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();

    }

}
