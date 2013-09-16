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

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {

	private static final Logger logger = LoggerFactory.getLogger(Index.class);
	

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    @Property
    private UserVo userVo;
    
    @InjectPage
    private ThreadContent threadContent;

    @Property
    @Persist
    private List<ThreadVo> threads;
    
    @Property
    private ThreadVo currentThread;
    
    @Property
    private boolean fromReset;
    
    @Property
    @Persist
    private boolean inSearch;
    
    @Persist
    @Property
    private String recipientsSearchThread;
    
    @Property
    private int autocompleteMin = 3;
    
    @Property
    private boolean showThreadTab;
    
    @Property
    private boolean showCreateButton;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

    @Inject
    private Messages messages;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade;
    
    @Inject
    private FunctionalityFacade functionalityFacade; 

    @SetupRender
    public void init() throws BusinessException{
    	if(!inSearch){
        	threads = threadEntryFacade.getListOfLastModifiedThreads(userVo);
    	}
    	showThreadTab = functionalityFacade.isEnableThreadTab(userVo.getDomainIdentifier());
    	showCreateButton = functionalityFacade.isEnableCreateThread(userVo.getDomainIdentifier());
    	recipientsSearchThread = "*";
    	
    }

    public Object onActionFromShowThreadContent(String lsUuid) {
    	logger.debug("Debut onActionFromShowThreadContent");
    	for (ThreadVo thread : threads) {
			if (thread.getLsUuid().equals(lsUuid)) {
		    	threadContent.setMySelectedThread(thread);
		    	logger.debug("Projet " + thread.getName() + "recupere");
		    	return threadContent;
			}
		}
    	return null;
    }
    
    public Object onActionFromAddThread() {
    	return null;
    }
    
    @AfterRender
    public void afterRender() {
    }
    
    public void onSuccessFromResetSearch() {
        inSearch = false;
        fromReset=true;
     }

    
    /**
	 * Format the creation date for good displaying using DateFormatUtils of
	 * apache commons lib.
	 * 
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(currentThread.getCreationDate().getTime());
	}
	
    /**
	 * Format the modification date for good displaying using DateFormatUtils of
	 * apache commons lib.
	 * 
	 * @return creation date the date in localized format.
	 */
	public String getModificationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(currentThread.getModificationDate().getTime());
	}
	
    /**
	 * Retrieve the number of documents in the thread.
	 * 
	 * @return the number of documents
	 */
	public int getCountDocuments() {
		try {
			return threadEntryFacade.getAllThreadEntryVo(userVo, currentThread).size();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
    
	public Object onSuccessFromFormSearch() throws BusinessException{	
		if(fromReset){
			threads = threadEntryFacade.getAllMyThread(userVo);
		} else {
			if(threads != null){
				threads.clear();
			}
			if(recipientsSearchThread.equals("*")){
				threads = threadEntryFacade.getAllMyThread(userVo);
			} else {
				List<ThreadVo> lists = threadEntryFacade.getAllMyThread(userVo);
					
				for(ThreadVo current : lists){
					if(current.getName().contains(recipientsSearchThread)){
						threads.add(current);
					}
				}
			}
			inSearch = true;
		}
		return null;
	}
	
    public List<String> onProvideCompletionsFromSearchThread(String input) throws BusinessException {
    	return threadEntryFacade.completionOnThreads(userVo, input);
    }
    
	
    public Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }
    
}
