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

package org.linagora.linshare.view.tapestry.pages.administration.lists;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.pages.administration.lists.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {
	
    private static Logger logger = LoggerFactory.getLogger(Index.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
    
    @SessionState
    @Property
    private UserVo loginUser;
    
    @Inject
    private Messages messages;
    
    @SessionState
    @Property
    private List<MailingListVo> lists;
	
    @Property
    private MailingListVo list;
    
    @Property
    @Persist(value="flash")
    private long listToDelete;
    
    @Inject
    private MailingListFacade mailingListFacade; 
    
    
	@Persist
	@Property(write=false)
	private boolean displayGrid;
	
	@Property
	private int autocompleteMin;
	
	@Property
	private String targetLists;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	private boolean displayAllLists;


	private boolean emptyList;
	
	
    @SetupRender
    public void init() throws BusinessException {
		if(displayGrid == false){
    	autocompleteMin = functionalityFacade.completionThreshold(loginUser.getDomainIdentifier());
    	lists= mailingListFacade.findAllMailingList();
		setEmptyList(lists.isEmpty());
		} else {
			lists = mailingListFacade.findAllMailingList();
			setEmptyList(lists.isEmpty());
			if(isEmptyList()){
				displayGrid =false;
			}
		}
    }
    @CleanupRender
    public void end() throws BusinessException {
    	displayGrid = false;
    }
    
    public boolean getListIsDeletable() throws BusinessException {
    	list = mailingListFacade.retrieveMailingList(list.getPersistenceId());
    	if(loginUser.getMail().equals(list.getOwner().getMail())){
    		return true;
    	}
    	return false;
    }
    
    public void onActionFromDeleteList(long persistenceId) {
    	this.listToDelete = persistenceId;
    }
    
    @OnEvent(value="listDeleteEvent")
    public void deleteList() throws BusinessException {
    	mailingListFacade.deleteMailingList(listToDelete);
        lists = mailingListFacade.findAllMailingList();
        if(!lists.isEmpty()){ 
        	displayGrid = true;
        }
    }
	/**
	 * AutoCompletion for search field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 * @throws BusinessException 
	 */
	public List<String> onProvideCompletionsFromSearch(String value){
		List<String> res = new ArrayList<String>();
		
			List<MailingListVo> founds = mailingListFacade.findAllMailingList();
			if (founds != null && founds.size() > 0) {
				for (MailingListVo listVo : founds) {
					res.add(listVo.getIdentifier());
				}
			}
		return res;
	}
    
	Object onActionFromDisplayAllLists() throws BusinessException { displayAllLists = true; return onSuccessFromForm();}
    
    public Object onSuccessFromForm() throws BusinessException {	
    		if(displayAllLists) {
        	lists= mailingListFacade.findAllMailingList();
    		} else {
    			lists = mailingListFacade.findAllMailingListByIdentifier(targetLists);
    		}
    	if(!lists.isEmpty()) {
    	displayGrid = true;
    	}
    	return null;
    }
    
    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }
    
	public boolean isEmptyList() {
		return emptyList;
	}

	public void setEmptyList(boolean emptyList) {
		this.emptyList = emptyList;
	}

	public boolean isDisplayGrid() {
		return displayGrid;
	}
	public void setDisplayGrid(boolean displayGrid) {
		this.displayGrid = displayGrid;
	}

}