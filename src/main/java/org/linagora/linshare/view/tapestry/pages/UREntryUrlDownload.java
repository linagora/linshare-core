package org.linagora.linshare.view.tapestry.pages;

import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.UploadRequestEntryUrlFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UREntryUrlDownload {

	private static final Logger logger = LoggerFactory
			.getLogger(UREntryUrlDownload.class);

	@Persist
	private String email;

	@Persist
	private String password;

	@Property
	private boolean passwordProtected;

	@Property
	private List<DocumentVo> documents;

	@Property
	private DocumentVo document;

	@Property
	@Persist
	private String uuid;

	@InjectComponent
	private PasswordPopup passwordPopup;

	@Inject
	private Messages messages;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport renderSupport;

	@Inject
	private BusinessMessagesManagementService messagesManagementService;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	@Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;

	@Inject
	@Symbol("linshare.display.licenceTerm")
	@Property
	private boolean linshareLicenceTerm;

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private UploadRequestEntryUrlFacade requestEntryUrlFacade;

	public String[] getContext() {
		return new String[] { uuid, document.getFileName() };
	}

	public boolean getContainsDocuments() {
		return document != null;
	}

	public StreamResponse onActivate(String uploadRequestUrlUuid,
			String documentName) {
		logger.debug("documentId : " + documentName);
		setCurrentUploadRequestEntryUrlUuid(uploadRequestUrlUuid);

		try {
			checkUrl(uploadRequestUrlUuid);
			if (!passwordProtected) {
				DocumentVo doc = requestEntryUrlFacade.getDocument(
						uploadRequestUrlUuid, password);
				final InputStream file = requestEntryUrlFacade
						.retrieveFileStream(uploadRequestUrlUuid,
								doc.getIdentifier(), password);
				return new FileStreamResponse(doc, file);
			} else {
				return null;
			}

		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}

	/**
	 * this is the first method called in this page.
	 * 
	 * @param uploadRequestEntryUrlUuid
	 */
	public void onActivate(String uploadRequestEntryUrlUuid) {
		setCurrentUploadRequestEntryUrlUuid(uploadRequestEntryUrlUuid);
		logger.debug("first on activate : " + uuid);
		try {
			checkUrl(uploadRequestEntryUrlUuid);
			document = requestEntryUrlFacade.getDocument(
					uploadRequestEntryUrlUuid, password);
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	private void setCurrentUploadRequestEntryUrlUuid(String uuid) {
		logger.debug("uuid : " + uuid);
		// check current one with the old one
		if (this.uuid != null && !this.uuid.equals(uuid)) {
			this.password = null; // reset cached password
		}
		this.uuid = uuid;
	}

	private void checkUrl(String uuid) {
		try {
			if (!requestEntryUrlFacade.exists(uuid, componentResources
					.getPageName().toLowerCase())) {
				String msg = "secure url does not exists";
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WRONG_URL, msg);
			}
			this.passwordProtected = (requestEntryUrlFacade
					.isPasswordProtected(uuid) && password == null);
			logger.debug("uuid : " + passwordProtected);
			if (!requestEntryUrlFacade.isValid(uuid, password)) {
				String msg = "the secured url is not valid";
				logger.error(msg);
				throw new BusinessException(msg);
			}

		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	public Zone onValidateFormFromPasswordPopup() {
		if (requestEntryUrlFacade.isValid(uuid, passwordPopup.getPassword())) {
			password = passwordPopup.getPassword();
			return passwordPopup.formSuccess();
		} else {
			passwordPopup
					.getFormPassword()
					.recordError(
							messages.get("components.download.passwordPopup.error.message"));
			return passwordPopup.formFail();
		}
	}

	@AfterRender
	void afterRender() {
		if (passwordProtected)
			renderSupport.addScript("window_passwordPopup.showCenter(true)");
	}

	public String getFriendlySize() {
		return FileUtils.getFriendlySize(document.getSize(), messages);
	}

	public boolean getDisplayLogo() {
		if (linshareLicenceTerm || linshareLogoVisible) {
			return true;
		}
		return false;
	}
}
