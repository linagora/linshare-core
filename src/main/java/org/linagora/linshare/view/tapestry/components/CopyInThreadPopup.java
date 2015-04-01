/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
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
	 * @throws BusinessException 
	 */
	@SetupRender
	public void init() throws BusinessException {
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