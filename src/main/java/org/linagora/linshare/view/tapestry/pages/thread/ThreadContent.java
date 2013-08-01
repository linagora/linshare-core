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
package org.linagora.linshare.view.tapestry.pages.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadViewAssoVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.ThreadFileUploadPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadContent {
	private static final Logger logger = LoggerFactory.getLogger(ThreadContent.class);
	
	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	@Persist
	private ThreadVo selectedThread;

	@Persist
	private int depth;
	
	@Property
	private String threadName;

	@InjectComponent
	private ThreadFileUploadPopup threadFileUploadPopup;
	
	@InjectPage
	private AdminThread adminThread;

	@Property
	private ThreadEntryVo entry;

	@Persist
	private String selectedThreadEntryId;
	
	@Property
	private DefaultMutableTreeNode root;
	
	@Property
	private List<DefaultMutableTreeNode> children;
	
	@Property
	private DefaultMutableTreeNode currentChild;
	
	@Property
	private DefaultMutableTreeNode currentLeaf;

	@Inject
	private Block case0, case1, case2, case3;


	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

	@Inject
	private Messages messages;

	@Inject
	private ThreadEntryFacade threadEntryFacade;


	@SuppressWarnings("unchecked")
	@SetupRender
	public void setupRender() throws BusinessException {
		logger.debug("Setup Render begins");
		
		threadName = selectedThread.getName();
		threadFileUploadPopup.setMyCurrentThread(selectedThread);
		depth = selectedThread.getView().getDepth();

		List<ThreadViewAssoVo> listViewTag = selectedThread.getView().getThreadViewAssos();
		DefaultMutableTreeNode root = null;

		// building Tag Tree from listViewTag
		for (ThreadViewAssoVo view : listViewTag) {
			switch (view.getDepth()) {
			case 1:
				root = new DefaultMutableTreeNode(view.getTagVo());
				break;
			case 2:
				root.add(new DefaultMutableTreeNode(view.getTagVo()));
				break;
			case 3:
				for (int i = 0; i < root.getChildCount(); ++i) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
					child.add(new DefaultMutableTreeNode(view.getTagVo()));
				}
				break;
			default:
				throw new BusinessException("Can't generate the thread content tree view. Tree depth should be in [1..3] but is " + view.getDepth());
			}
		}
		switch (depth) {
		case 0:
			root = null;
			break;
		case 1:
			currentLeaf = root;
			break;
		case 2:
			currentChild = root;
			break;
		case 3:
			break;
		default:
			throw new BusinessException("Can't generate the thread content tree view. Tree depth should be in [1..3] but is " + depth);
		}
		children = root != null ? Collections.list(root.children()) : new ArrayList<DefaultMutableTreeNode>();
	}
	
	public Object onActivate() {
		if (selectedThread == null) {
			return Index.class;
		}
		return null;
	}

	@AfterRender
	public void afterRender() {
	}
	
	public Object onActionFromAdmin() {
		if (!this.getAdmin())
			return null;
		adminThread.setSelectedCurrentThread(selectedThread);
		return adminThread;
	}

	public String getCurrentChildSubject() {
		return currentChild.toString();
	}

	@SuppressWarnings("unchecked")
	public List<DefaultMutableTreeNode> getLeaves() {
		return Collections.list(currentChild.children());
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

	/*
	 * Handle page layout with Tapestry Blocks
	 */
	public Object getCase() {
		logger.debug("Depth = " + depth);
		switch (depth) {
		case 0:
			return case0;
		case 1:
			return case1;
		case 2:
			return case2;
		case 3:
			return case3;
		default:
			return null;
		}
	}

	public List<ThreadEntryVo> getCurrentEntriesList() throws BusinessException {
		// handling list displaying without any tag
		if (depth == 0) {
			return threadEntryFacade.getAllThreadEntryVo(userVo, selectedThread);
		}
		// handling list displaying with tags
		Object[] objs = currentLeaf.getUserObjectPath();
		TagVo[] tags = Arrays.copyOf(objs, objs.length, TagVo[].class);
		return threadEntryFacade.getAllThreadEntriesTaggedWith(userVo, selectedThread, tags);
	}

	/*
	 *  Mandatory for page generation
	 */
	public void setMySelectedThread(ThreadVo selectedThread) {
		this.selectedThread = selectedThread;
	}

	/*
	 *  Mandatory for page generation
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getSelectedThreadEntryId() {
		return selectedThreadEntryId;
	}

	public void setSelectedThreadEntryId(String selectedThreadEntry) {
		this.selectedThreadEntryId = selectedThreadEntry;
	}

	@OnEvent(value="eventDeleteThreadEntry")
	public void deleteThreadEntry() {
		ThreadEntryVo selectedVo = null;
		try {
			selectedVo = threadEntryFacade.findById(userVo, selectedThreadEntryId);
			threadEntryFacade.removeDocument(userVo, selectedVo);
			shareSessionObjects.removeDocument(selectedVo);
			shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"), selectedVo.getFileName()));
		} catch (BusinessException e) {
			shareSessionObjects.addError(String.format(messages.get("pages.index.message.failRemovingFile"), selectedVo.getFileName()) );
			logger.debug(e.toString());
		}
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
