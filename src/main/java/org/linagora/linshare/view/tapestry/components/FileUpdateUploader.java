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
package org.linagora.linshare.view.tapestry.components;


import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;



/**
 * Component used to display the multiple uploader
 * This component my throw two exceptions, fatal, that are to be catched :
 * - FileUploadBase.FileSizeLimitExceededException : if a file is too large
 * - FileUploadBase.SizeLimitExceededException : if the total upload is too large
 * @author ncharles
 *
 */
@SupportsInformalParameters
public class FileUpdateUploader {

    private static final long DEFAULT_MAX_FILE_SIZE = 52428800;

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private ShareFacade shareFacade;

    @Inject
    private BusinessMessagesManagementService messagesManagementService;

	@Inject
	private AbstractDomainFacade domainFacade;

    @Inject
    private Logger logger;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private Messages messages;

	@Component(parameters = {"style=bluelighting", "show=false","width=600", "height=250"})
	private WindowWithEffects windowUpdateDocUpload;



	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	@SessionState
	private UserVo userDetails;

	@Persist
	private String uuidDocToUpdate;


    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */

	@SetupRender
	void setupRender() {
	}

    @AfterRender
    public void afterRender() {
    }



	/**
	 * Prior to validating the form, we need to initialise all the arrays
	 */
	public void onPrepare() {
	}

    @OnEvent(value = "fileUploaded")
    public void processFilesUploaded(Object[] context) {
    	boolean toUpdate = false;

        for (int i = 0; i < context.length; ++i) {
        	UploadedFile uploadedFile = (UploadedFile) context[i];

            if (uploadedFile != null && i < 1) { //limit to one file (for update)
                try {
                	DocumentVo initialdocument = documentFacade.getDocument(userDetails, uuidDocToUpdate);
                	String filesizeTxt = FileUtils.getFriendlySize(initialdocument.getSize(), messages);
                    DocumentVo document  =  documentFacade.updateDocumentContent(uuidDocToUpdate, uploadedFile.getStream(), uploadedFile.getSize(),
                            uploadedFile.getFileName(), userDetails, filesizeTxt);

                    messagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.UPLOAD_UPDATE_FILE_CONTENT_OK,
                        MessageSeverity.INFO, initialdocument.getFileName(),uploadedFile.getFileName()));
                    // Notify the add of a file.
                    Object[] addedFile = {document};
                    componentResources.triggerEvent("fileAdded", addedFile, null);
                    toUpdate = true;
                } catch (BusinessException e) {
                    messagesManagementService.notify(e);
                    readFileStream(uploadedFile);
                }
            } else {
            	if (uploadedFile != null)
            		readFileStream(uploadedFile);
            }
        }
        if (toUpdate) 
        	componentResources.triggerEvent("resetListFiles", null, null);
    }

    /* ***********************************************************
     *                   Helpers
     ************************************************************ */

    public long getUserFreeSpace() {
    	long res = 0;
        try {
			res = documentFacade.getUserAvailableQuota(userDetails);
		} catch (BusinessException e) {
            messagesManagementService.notify(e);
		}
		return res;
    }

	public void setUuidDocToUpdate(String uuidDocToUpdate) {
		this.uuidDocToUpdate = uuidDocToUpdate;
	}

	public long getMaxFileSize() {
        long maxFileSize = DEFAULT_MAX_FILE_SIZE;
        try {
            maxFileSize = documentFacade.getUserMaxFileSize(userDetails);
        } catch (BusinessException e) {
            // value has not been defined. We use the default value.
        	e.printStackTrace();
        }
        return maxFileSize;
    }

    private void readFileStream(UploadedFile file) {
        try {
            // read the complete stream.
            InputStream stream = file.getStream();
            while (stream.read() != -1); // NOPMD by matthieu on 24/02/10 10:19
        } catch (IOException ex) {
            logger.error(ex.toString());
        }
    }
}
