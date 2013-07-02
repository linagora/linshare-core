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

package org.linagora.linshare.view.tapestry.pages.lists;

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
import org.linagora.linshare.view.tapestry.pages.lists.Index;
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
	@Persist(value = "flash")
	private long listToDelete;

	@Inject
	private MailingListFacade mailingListFacade;

	@Property
	private int autocompleteMin=3;

	@Property
	private String targetLists;

	@Inject
	private FunctionalityFacade functionalityFacade;

	private boolean emptyList;
	
	@Persist
	@Property
	private String criteriaOnSearch;
	
	@Persist
	private boolean inSearch;
	
	@Persist
	private boolean fromCreate;

	@SetupRender
	public void init() throws BusinessException {
		
		if(inSearch == false || fromCreate == true){
			lists = mailingListFacade.findAllMailingListByUser(loginUser);
			}
			setEmptyList(lists.isEmpty());
			criteriaOnSearch = "all";
	}


	public boolean getListIsDeletable() throws BusinessException {
		list = mailingListFacade.retrieveMailingList(list.getPersistenceId());
		if (loginUser.getMail().equals(list.getOwner().getMail())) {
			return true;
		}
		return false;
	}
	
	public boolean getUserIsOwner() throws BusinessException {
		return loginUser.equals(list.getOwner());
	}

	public void onActionFromDeleteList(long persistenceId) {
		this.listToDelete = persistenceId;
	}

	@OnEvent(value = "listDeleteEvent")
	public void deleteList() throws BusinessException {
		mailingListFacade.deleteMailingList(listToDelete);
		lists = mailingListFacade.findAllMailingListByUser(loginUser);
		list=null;
	}

	/**
	 * AutoCompletion for search field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 * @throws BusinessException 
	 */
	public List<String> onProvideCompletionsFromSearch(String input) throws BusinessException {
		List<MailingListVo> searchResults = performSearch(input);
		List<String> elements = new ArrayList<String>();
		for (MailingListVo current: searchResults) {
			String completeName = current.getIdentifier();
				elements.add(completeName);
		}
		return elements;
	}
	
	/**
	 * Perform a list search.
	 * 
	 * @param input
	 *            list search pattern.
	 * @return list of lists.
	 * @throws BusinessException 
	 */
	private List<MailingListVo> performSearch(String input) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		list = mailingListFacade.findAllMailingListByUser(loginUser);
		for(MailingListVo current : list){
			if(current.getIdentifier().indexOf(input) != -1){
				finalList.add(current);
			}
		}
		return finalList;
	}
	

	public Object onSuccessFromForm() throws BusinessException {
    	inSearch = true;
    	if(targetLists!=null){
    		lists.clear();
    		lists = performSearch(targetLists);
    		if(criteriaOnSearch.equals("public")){
    			List<MailingListVo> finalList = mailingListFacade.copyList(lists);
    			lists.clear();
    			for(MailingListVo current : finalList){
    				if(current.isPublic() == true){
    					lists.add(current);
    				}
    			}
    		}
    		else if(criteriaOnSearch.equals("private")){
    			List<MailingListVo> finalList = mailingListFacade.copyList(lists);
    			lists.clear();
    			for(MailingListVo current : finalList){
    				if(current.isPublic() == false){
    					lists.add(current);
    				}
    			}
    		}
    	}
    	else {
    		lists=new ArrayList<MailingListVo>();
    	}
    	fromCreate = false;
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
	
	public String getPublic() { return "public"; }
	public String getPrivate() { return "private"; }
	public String getAll() { return "all"; }


	public boolean isFromCreate() {
		return fromCreate;
	}


	public void setFromCreate(boolean fromCreate) {
		this.fromCreate = fromCreate;
	}
}