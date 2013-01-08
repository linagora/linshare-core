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
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = {"SizeOfPopup.js"})
public class CopyInThreadPopup {
	private static final Logger logger = LoggerFactory.getLogger(CopyInThreadPopup.class);
	
	@SessionState
	private UserVo userVo;

	@SessionState
	private ShareSessionObjects shareSessionObjects;

	@Persist
	@Property
	private List<ThreadVo> threadsVo;
	
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<DocumentVo> documentsVo;

	
	@Component(parameters = {"style=bluelighting", "show=false","width=700", "height=300"})
	private WindowWithEffects groupShareWindow;

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String messageLabel;

	@Inject
	private Messages messages;
	
	@Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
	
	
    // The block that contains the action to be thrown on success 
	@Inject
	private Block onSuccess;
    
    // The block that contains the action to be thrown on failure
	@Inject
	private Block onFailure;
    
    @SuppressWarnings("unused")
	@Property
    private Boolean valueCheck;
	
	@Property
	private ThreadVo threadSelected;

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

    @Environmental
    private JavaScriptSupport renderSupport;

	@Inject
	private ComponentResources componentResources;
	
	@Inject
	private SelectModelFactory selectModelFactory;
	
	@Inject
	private ThreadEntryFacade threadEntryFacade;

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */
	@Property
	@Persist
	private List<ThreadVo> allMyThreadWhereCanUpload;
	private List<ThreadVo> selectedThreads;
	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		allMyThreadWhereCanUpload = threadEntryFacade.getAllMyThreadWhereCanUpload(userVo);
//    	List<String> threadList = new ArrayList<String>();
//    	for (ThreadVo threadVo : allMyThreadWhereCanUpload) {
//    		threadList.add(threadVo.getName());
//    	}
//        threadSelectModel = selectModelFactory.create(allMyThreadWhereCanUpload, "name");
	}

	@AfterRender
    public void afterRender() {
    	//resize the share popup
        renderSupport.addScript(String.format("groupShareWindow.setSize(650, getHeightForPopup())"));

    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
        if (selectedThreads == null) {
        	selectedThreads = new ArrayList<ThreadVo>();
        }
        if (selected) {
        	selectedThreads.add(threadSelected);
        }
        else {
        	selectedThreads.remove(threadSelected);
        }
    }

    public Block onFailure() {
    	return onFailure;
    }
	
	
    public Block onSuccess() throws BusinessException {
    	
        if (selectedThreads == null) {
   			shareSessionObjects.addError(messages.get("components.copyInThread.fail"));
        	return null;
        }
    	
    	for (ThreadVo selectedThreadVo : selectedThreads) {
			for (DocumentVo documentVo : documentsVo) {
				try {
					threadEntryFacade.copyDocinThread(userVo, selectedThreadVo, documentVo);
				} catch (BusinessException e) {
					shareSessionObjects.addError(String.format(messages.get("pages.index.message.failCopyToThreadFile"),
							documentVo.getFileName()) );
					logger.debug(e.toString());
				}
			}
			shareSessionObjects.addMessage(messages.get("components.copyInThread.success"));
    	}
   		return onSuccess;
	}
    

	public String getJSONId() {
		return groupShareWindow.getJSONId();
	}


}