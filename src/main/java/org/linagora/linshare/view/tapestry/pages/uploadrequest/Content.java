package org.linagora.linshare.view.tapestry.pages.uploadrequest;

import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.UploadRequestEntryVo;
import org.linagora.linshare.core.domain.vo.UploadRequestVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UploadRequestFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

public class Content {

	private static final Logger logger = LoggerFactory.getLogger(Content.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	private UploadRequestEntryVo current;

	@Persist
	@Property
	private List<UploadRequestEntryVo> entries;

	@Property
	@Persist
	private UploadRequestVo selected;

	@InjectPage
	private Detail detail;

	@Inject
	private Messages messages;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private UploadRequestFacade uploadRequestFacade;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	public Object onActivate(String uuid) {
		logger.debug("Upload Request uuid: " + uuid);
		try {
			this.selected = uploadRequestFacade.findRequestByUuid(userVo, uuid);
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.UPLOAD_REQUEST_NOT_FOUND,
					MessageSeverity.ERROR));
			return Index.class;
		}
		return null;
	}

	public Object onActivate() {
		if (!functionalityFacade.isEnableUploadRequest(userVo
				.getDomainIdentifier())) {
			return Index.class;
		}
		if (selected == null) {
			logger.info("No upload request selected, abort");
			return Index.class;
		}
		if (!selected.getOwner().businessEquals(userVo)
				|| !selected.isVisible()) {
			logger.info("Unauthorized");
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.UPLOAD_REQUEST_NOT_FOUND,
					MessageSeverity.ERROR));
			return Index.class;
		}
		return null;
	}

	public Object onPassivate() {
		return selected.getUuid();
	}

	@SetupRender
	public void init() throws BusinessException {
		logger.debug("Setup Render begins");

		entries = uploadRequestFacade.findAllEntries(userVo, selected);
	}

	public Object onActionFromShowDetail() throws BusinessException {
		detail.setMySelected(selected);
		return detail;
	}

	public StreamResponse onActionFromDownload(String uuid)
			throws BusinessException {
		UploadRequestEntryVo entry = Iterables.find(entries,
				UploadRequestEntryVo.equalTo(uuid));

		try {
			InputStream stream = uploadRequestFacade.getFileStream(userVo, entry);
			return new FileStreamResponse(entry.getDocument(), stream);
		} catch (Exception e) {
			logger.error("File don't exist anymore, please remove it");
			businessMessagesManagementService
					.notify(new BusinessException(
							BusinessErrorCode.FILE_UNREACHABLE,
							"File unreachable in file system, please remove the entry"));
			return null;
		}
	}

	public void setMySelected(UploadRequestVo selected) {
		this.selected = selected;
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}
