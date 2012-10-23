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

public class ProjectThreadGraph {
	private static final Logger logger = LoggerFactory.getLogger(ProjectThreadGraph.class);
	
	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@InjectPage
	private Index index;

	@Property
	@Persist
	private ThreadVo selectedProject;

	@Persist
	private int depth;
	
	@Property
	private String threadName;

	@InjectComponent
	private ThreadFileUploadPopup threadFileUploadPopup;

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
	public void setupRender() {
		logger.debug("Setup Render begins");

		threadName = selectedProject.getName();
		threadFileUploadPopup.setMyCurrentThread(selectedProject);
		depth = selectedProject.getView().getDepth();

		List<ThreadViewAssoVo> listViewTag = selectedProject.getView().getThreadViewAssos();
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
				// TODO : can't be there, throw an exception
				break;
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
			// TODO : can't be there, throw an exception
			break;
		}
		children = root != null ? Collections.list(root.children()) : new ArrayList<DefaultMutableTreeNode>();
	}

	@AfterRender
	public void afterRender() {
		;
	}

	public String getCurrentChildSubject() {
		return currentChild.toString();
	}

	@SuppressWarnings("unchecked")
	public List<DefaultMutableTreeNode> getLeaves() {
		return Collections.list(currentChild.children());
	}
	
	public boolean getCanUpload() {
		return threadEntryFacade.userCanUpload(userVo, selectedProject);
	}

	public boolean getAdmin() {
		return threadEntryFacade.userIsAdmin(userVo, selectedProject);
	}

	/*
	 * Handle page layout with Tapestry Blocks
	 */
	public Object getCase() {
		logger.info("Depth = " + depth);
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
			return threadEntryFacade.getAllThreadEntryVo(userVo, selectedProject);
		}
		// handling list displaying with tags
		Object[] objs = currentLeaf.getUserObjectPath();
		TagVo[] tags = Arrays.copyOf(objs, objs.length, TagVo[].class);
		return threadEntryFacade.getAllThreadEntriesTaggedWith(userVo, selectedProject, tags);
	}

	/*
	 *  Mandatory for page generation
	 */
	public void setMySelectedProject(ThreadVo selectedProject) {
		this.selectedProject = selectedProject;
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
			selectedVo = threadEntryFacade.findById(userVo, selectedProject, selectedThreadEntryId);
			threadEntryFacade.removeDocument(userVo, selectedVo);
			shareSessionObjects.removeDocument(selectedVo);
			shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"), selectedVo.getFileName()));
		} catch (BusinessException e) {
			shareSessionObjects.addError(String.format(messages.get("pages.index.message.failRemovingFile"), selectedVo.getFileName()) );
		}
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
