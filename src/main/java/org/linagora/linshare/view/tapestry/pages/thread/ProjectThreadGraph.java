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

	

//	<t:loop source="children" value="currentChild">
//    <div t:type="ck/SlidingPanel" t:id="currentChild" t:closed="true" t:subject="currentChildSubject" t:options="{duration:0.3}">
//        <t:loop source="childLeafs" value="currentLeaf">
//            <t:ListThreadDocument t:listThreadEntries="currentEntriesList" t:threadVo="selectedProject" t:user="userVo" t:title="currentTitle" />
//            <br />
//        </t:loop>
//    </div>
//    </t:loop>




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

//		try {
			/*
			 * TODO : print getOutInstructions() content
			 */
			DefaultMutableTreeNode root = null;

			List<DummyViewTagVo> listViewTag = new ArrayList<DummyViewTagVo>();
			/*
			 * TODO : a ThreadFacade.getSortedViewTagsFromThread() method that return the list sorted by ascendant order
			 */
			listViewTag.add(new DummyViewTagVo(new TagVo(selectedProject.getName()), 1));
			listViewTag.add(new DummyViewTagVo(new TagVo("Demande"), 2));
			listViewTag.add(new DummyViewTagVo(new TagVo("Réponse"), 2));
			listViewTag.add(new DummyViewTagVo(new TagVo("Phases", "Instruction"), 3));
			listViewTag.add(new DummyViewTagVo(new TagVo("Phases", "Contradiction"), 3));
			listViewTag.add(new DummyViewTagVo(new TagVo("Phases", "Recommandation"), 3));
			/*
			 * XXX : This algorithm is valid only if datas are correctly ordered by TagViewVo.depth
			 */
			/*
			 * TODO : maybe a method returning the correct Tree structure
			 */
			if (listViewTag == null || listViewTag.isEmpty()) {
				root = new DefaultMutableTreeNode(new TagVo(selectedProject.getName()));
			}
			else {
				for (DummyViewTagVo dummy : listViewTag) {
					switch (dummy.getDepth()) {
					case 1:
						root = new DefaultMutableTreeNode(dummy.getTagVo());
						break;
					case 2:
						root.add(new DefaultMutableTreeNode(dummy.getTagVo()));
						break;
					case 3:
						for (int i = 0; i < root.getChildCount(); ++i) {
							DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
							child.add(new DefaultMutableTreeNode(dummy.getTagVo()));
						}
						break;
					default:
						// TODO : can't be there, throw an exception?
						break;
					}
				}
			}
			children = root != null ? Collections.list(root.children()) : new ArrayList<DefaultMutableTreeNode>();
//			List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
//			for (DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) root.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
//				children.add(currentChild);
//			}
//			for (DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) root.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
//				for (DefaultMutableTreeNode currentLeaf = currentChild.getFirstLeaf(); currentLeaf != null; currentLeaf = currentLeaf.getNextSibling()) {
//					logger.info("looping");
//					Object[] objs = currentLeaf.getUserObjectPath();
//					TagVo[] tags = Arrays.copyOf(objs, objs.length, TagVo[].class);
//					List<ThreadEntryVo> current = threadEntryFacade.getAllThreadEntriesTaggedWith(userVo, selectedProject, tags);
//
//					logger.info("> Tag : " + ((TagVo) ((DefaultMutableTreeNode) currentLeaf.getParent()).getUserObject()).getName() + " - " + ((TagVo) currentLeaf.getUserObject()).getFullName());
//					for (ThreadEntryVo tevo : current) {
//						logger.info(" ---> File : " + tevo.getFileName());
//					}
//				}
//			}
//		} catch (BusinessException e) {
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
	}

	@AfterRender
	public void afterRender() {
		;
	}
	
	public String getCurrentChildSubject() {
		return currentChild.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DefaultMutableTreeNode> getChildLeaves() {
		return Collections.list(currentChild.children());
	}
	
	public List<ThreadEntryVo> getCurrentEntriesList() throws BusinessException {
		Object[] objs = currentLeaf.getUserObjectPath();
		TagVo[] tags = Arrays.copyOf(objs, objs.length, TagVo[].class);
		return threadEntryFacade.getAllThreadEntriesTaggedWith(userVo, selectedProject, tags);
	}

	public void setMySelectedProject(ThreadVo selectedProject) {
		this.selectedProject = selectedProject;
	}

	public String getSelectedThreadEntryId() {
		return selectedThreadEntryId;
	}

	public void setSelectedThreadEntryId(String selectedThreadEntry) {
		this.selectedThreadEntryId = selectedThreadEntry;
	}

	@OnEvent(value="eventDeleteThreadEntry")
	public void deleteThreadEntry() {
		System.out.println(new Exception().getStackTrace()[0].getMethodName() + " : selectedThreadEntry = " + selectedThreadEntryId);
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
