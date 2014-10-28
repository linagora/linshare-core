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
package org.linagora.linshare.view.tapestry.pages.thread;

import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.RequestGlobals;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = { "../../components/jquery/jquery-1.7.2.js",
					"../../components/fineuploader/fineuploader-4.1.0.js",
					"../../components/bootstrap/js/bootstrap.js" },
		stylesheet = { "../../components/fineuploader/fineuploader-4.1.0.css" })
public class ThreadContent {

	private static final Logger logger = LoggerFactory
			.getLogger(ThreadContent.class);

	// unlimited file size
	private static final long DEFAULT_MAX_FILE_SIZE = 0;

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	@Persist
	private ThreadVo selectedThread;

	@Property
	private String threadName;

	@Property
	private String threadUuid;

	@InjectPage
	private Admin admin;

	@Property
	private ThreadEntryVo entry;

	@Persist
	private String selectedThreadEntryId;

	@Property
	private String contextPath;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Messages messages;

	@Inject
	private ThreadEntryFacade threadEntryFacade;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	public Object onActivate(String uuid) {
		try {
			this.selectedThread = threadEntryFacade.getThread(userVo, uuid);
		} catch (Exception e) {
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.THREAD_NOT_FOUND,
					MessageSeverity.ERROR));
			logger.error(e.getMessage());
			e.printStackTrace();
			return Index.class;
		}
		return null;
	}

	public Object onActivate() {
		if (selectedThread == null) {
			logger.info("No thread selected, abort");
			return Index.class;
		}
		try {
			if (!threadEntryFacade.userIsMember(userVo, selectedThread)) {
				logger.info("Unauthorized");
				businessMessagesManagementService.notify(new BusinessUserMessage(
								BusinessUserMessageType.THREAD_NOT_FOUND,
								MessageSeverity.ERROR));
				return Index.class;
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			return Index.class;
		}
		contextPath = requestGlobals.getHTTPServletRequest().getContextPath();
		return null;
	}

	public Object onPassivate() {
		return selectedThread.getLsUuid();
	}

	@SetupRender
	public void setupRender() throws BusinessException {
		logger.debug("Setup Render begins");

		threadName = selectedThread.getName();
		threadUuid = selectedThread.getLsUuid();
	}

	@Import(stylesheet = { "../../components/bootstrap/css/bootstrap.css" })
	@CleanupRender
	void cleanupRender() {
	}

	@AfterRender
	public void afterRender() {
	}

	public Object onActionFromAdmin() {
		if (!this.getAdmin())
			return null;
		admin.setSelectedThread(selectedThread);
		return admin;
	}

	public boolean getCanUpload() {
		try {
			return threadEntryFacade.userCanUpload(userVo, selectedThread);
		} catch (BusinessException e) {
			logger.error("cannot retrieve user infos" + e.getMessage());
			logger.debug(e.toString());
			return false;
		}
	}

	public boolean getAdmin() {
		try {
			return threadEntryFacade.userIsAdmin(userVo, selectedThread);
		} catch (BusinessException e) {
			logger.error("cannot retrieve user infos" + e.getMessage());
			logger.debug(e.toString());
			return false;
		}
	}

	public List<ThreadEntryVo> getCurrentEntriesList() throws BusinessException {
		return threadEntryFacade.getAllThreadEntryVo(userVo, selectedThread);
	}

	/*
	 * Mandatory for page generation
	 */
	public void setMySelectedThread(ThreadVo selectedThread) {
		this.selectedThread = selectedThread;
	}

	public String getSelectedThreadEntryId() {
		return selectedThreadEntryId;
	}

	public void setSelectedThreadEntryId(String selectedThreadEntry) {
		this.selectedThreadEntryId = selectedThreadEntry;
	}

	@OnEvent(value = "eventDeleteThreadEntry")
	public void deleteThreadEntry() {
		ThreadEntryVo selectedVo = null;
		try {
			selectedVo = threadEntryFacade.findById(userVo,
					selectedThreadEntryId);
			threadEntryFacade.removeDocument(userVo, selectedVo);
			shareSessionObjects.removeDocument(selectedVo);
			shareSessionObjects.addMessage(String.format(
					messages.get("pages.index.message.onefileRemoved"),
					selectedVo.getFileName()));
		} catch (BusinessException e) {
			shareSessionObjects.addError(String.format(
					messages.get("pages.index.message.failRemovingFile"),
					selectedVo.getFileName()));
			logger.debug(e.toString());
		}
	}

	public long getMaxFileSize() {
		return DEFAULT_MAX_FILE_SIZE;
	}

	public JSONObject getErrorCatalog() {
		JSONObject catalog = new JSONObject();
		for (BusinessErrorCode k : BusinessErrorCode.values()) {
			String prop = "error.code." + k.name();
			catalog.put(k.name(), messages.get(prop));
		}
		return catalog;
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}
