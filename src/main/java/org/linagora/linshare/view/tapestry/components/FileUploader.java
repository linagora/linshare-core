/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;

/**
 * Component used to display the multiple uploader This component my throw two
 * exceptions, fatal, that are to be catched : -
 * FileUploadBase.FileSizeLimitExceededException : if a file is too large -
 * FileUploadBase.SizeLimitExceededException : if the total upload is too large
 * 
 * @author ncharles
 * 
 */
@SupportsInformalParameters
public class FileUploader {

	private static final long DEFAULT_MAX_FILE_SIZE = 52428800;

	/* ***********************************************************
	 * Parameters***********************************************************
	 */
	@Parameter(required = false)
	@Property(write = false)
	private String divId;

	@Parameter(required = false, value = "true")
	@Property(write = false)
	private String showSendButton;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private BusinessMessagesManagementService messagesManagementService;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private Logger logger;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Messages messages;

	/* ***********************************************************
	 * Properties & injected symbol, ASO, etc
	 * ***********************************************************
	 */

	@SessionState
	private UserVo userDetails;

	@Property
	private String customTitle;

	@Property
	private String maxSize;
	
	/* ***********************************************************
	 * Event handlers&processing
	 * ***********************************************************
	 */

	@SetupRender
	void setupRender() throws BusinessException {
		customTitle = messages.get("components.uploadFile.title");
		long max = documentFacade.getUserMaxFileSize(userDetails);
		long free = documentFacade.getUserAvailableQuota(userDetails);
		maxSize = "";
		if (max != LinShareConstants.defaultMaxFileSize) {
			maxSize += messages.format("components.fileuploader.max",
					FileUtils.getFriendlySize(max, messages));
		}
		if (free != LinShareConstants.defaultFreeSpace) {
			maxSize += maxSize.length() > 0 ? " " : "";
			maxSize += messages.format("components.fileuploader.free",
					FileUtils.getFriendlySize(free, messages));
		}
	}

	@AfterRender
	public void afterRender() {
	}

	/**
	 * Prior to validating the form, we need to initialize all the arrays
	 */
	public void onPrepare() {
	}

	@OnEvent(value = "fileUploaded")
	public void processFilesUploaded(Object[] context) {
		boolean toUpdate = false;

		for (int i = 0; i < context.length; i++) {
			UploadedFile uploadedFile = (UploadedFile) context[i];
			if (uploadedFile != null) {
				if (uploadedFile.getSize() > getMaxFileSize()) {
					messagesManagementService.notify(new BusinessUserMessage(
							BusinessUserMessageType.UPLOAD_WITH_FILE_TOO_LARGE,
							MessageSeverity.ERROR, uploadedFile.getFileName(),
							FileUtils.getFriendlySize(uploadedFile.getSize(),
									messages), FileUtils.getFriendlySize(
									getMaxFileSize(), messages)));
					continue;
				}
				if (uploadedFile.getSize() > getUserFreeSpace()) {
					messagesManagementService.notify(new BusinessUserMessage(
							BusinessUserMessageType.UPLOAD_NOT_ENOUGH_SPACE,
							MessageSeverity.ERROR, uploadedFile.getFileName(),
							FileUtils.getFriendlySize(uploadedFile.getSize(),
									messages), FileUtils.getFriendlySize(
									getUserFreeSpace(), messages)));
					continue;
				}

				// String mimeType;
				// try {
				// mimeType =
				// documentFacade.getMimeType(uploadedFile.getStream(),
				// uploadedFile.getFilePath());
				// if (null == mimeType) {
				// mimeType = uploadedFile.getContentType();
				// }
				// } catch (BusinessException e) {
				// mimeType = uploadedFile.getContentType();
				// }

				try {
					DocumentVo document = documentFacade.insertFile(
							uploadedFile.getStream(),
							uploadedFile.getFileName(), userDetails);
					MimeTypeStatus status = documentFacade.getMimeTypeStatus(
							userDetails.getLsUuid(), document.getIdentifier());

					if (status.equals(MimeTypeStatus.WARN)) {
						String[] extras = { uploadedFile.getFileName(),
								uploadedFile.getContentType() };
						messagesManagementService
								.notify(new BusinessUserMessage(
										BusinessUserMessageType.MIME_TYPE_WARNING,
										MessageSeverity.WARNING, extras));
					}
					messagesManagementService.notify(new BusinessUserMessage(
							BusinessUserMessageType.UPLOAD_OK,
							MessageSeverity.INFO, uploadedFile.getFileName()));

					// Notify the add of a file.
					Object[] addedFile = { document };
					componentResources.triggerEvent("fileAdded", addedFile,
							null);
					toUpdate = true;
				} catch (BusinessException e) {
					messagesManagementService.notify(e);
				}
			}
		}
		if (toUpdate)
			componentResources.triggerEvent("resetListFiles", null, null);
	}

	/* ***********************************************************
	 * Helpers***********************************************************
	 */

	public long getUserFreeSpace() {
		long res = 0;
		try {
			res = documentFacade.getUserAvailableQuota(userDetails);
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return res;
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
			while (stream.read() != -1)
				; // NOPMD by matthieu on 24/02/10 10:19
		} catch (IOException ex) {
			logger.error(ex.toString());
		}
	}
}
