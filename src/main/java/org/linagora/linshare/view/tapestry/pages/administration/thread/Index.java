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
package org.linagora.linshare.view.tapestry.pages.administration.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
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
    
    @Inject
    private Messages messages;
    
    @Inject
    private RecipientFavouriteFacade recipientFavouriteFacade;
    
    @Inject
    private UserFacade userFacade;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade;
    
    @Persist
    @Property
    private String recipientsSearchThread;
	
    @Persist
    @Property
    private String recipientsSearchUser;
    
    @Persist
    @Property
    private String criteriaOnSearch;
    
    @Property
    private int autocompleteMin = 3;
    
	@Persist
	@Property(write = true)
	private boolean inSearch;
	
    @Property
    @Persist
    private List<ThreadVo> threads;
    
    @InjectPage
    private org.linagora.linshare.view.tapestry.pages.administration.thread.AdminThread adminThread;
    
    @Persist
    @Property
    private ThreadVo currentThread;
    
    @Property
    @Persist(value="flash")
    private ThreadVo threadToDelete;
    
    
    @SetupRender
    public void init() throws BusinessException{
    	if(!inSearch){
    			recipientsSearchThread = "*";
    	    	threads = threadEntryFacade.getAllThread();
    	    	criteriaOnSearch ="all";
    			inSearch=true;
		}
    }
    
	public int getCountDocuments() {
		try {
			return threadEntryFacade.getAllThreadEntryVo(userFacade.findUserByLsUuid(userVo,currentThread.getOwnerLsUuid()), currentThread).size();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return 0;
	}
    
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(currentThread.getCreationDate().getTime());
	}

	public String getModificationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(currentThread.getModificationDate().getTime());
	}
	
    public Object onActionFromShowThreadContent(String lsUuid) {
    	for(ThreadVo current : threads){
    		if(current.getLsUuid().equals(lsUuid)){
            	adminThread.setSelectedCurrentThread(current);
    		}
    	}
    	return adminThread;
    }
    
    public List<String> onProvideCompletionsFromSearchThread(String input) throws BusinessException {
    	List<ThreadVo> lists = threadEntryFacade.getAllThread();
		List<String> elements = new ArrayList<String>();
		
		for(ThreadVo current : lists){
			if(current.getName().startsWith(input)){
				elements.add("\""+current.getName()+"\" <"+userFacade.findUserByLsUuid(userVo,currentThread.getOwnerLsUuid()).getFullName()+">");
			}
		}
		return elements;
    }
    
	public List<String> onProvideCompletionsFromSearchUser(String input) throws BusinessException {
		List<UserVo> searchResults = performSearch(input);
		List<String> elements = new ArrayList<String>();
		
		for (UserVo user : searchResults) {
			String completeName = MailCompletionService.formatLabel(user);
			elements.add(completeName);
			}

		return elements;
	}
    
	private List<UserVo> performSearch(String input) {

		Set<UserVo> userSet = new HashSet<UserVo>();

		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		try {
			if (input != null) {
				userSet.addAll(userFacade.searchUser(input.trim(), null, null,userVo));
			}
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_,userVo));

			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,userVo));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(),userVo));
			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet),userVo);
		} catch (BusinessException e) {
			logger.error("Error while searching user", e);
		}
		return new ArrayList<UserVo>();
	}
    
    public void onSelectedFromStop() {
        inSearch = false;
    }
   
    public void onSelectedFromReset() {
    	criteriaOnSearch = "all";
    	recipientsSearchUser = "";
        inSearch = false;
    }
    
    public UserVo getOwner() throws BusinessException{
    	return userFacade.findUserByLsUuid(userVo,currentThread.getOwnerLsUuid());
    }
    
    public Object onSuccessFromFormSearchByUser() throws BusinessException {
    	List<UserVo> users = new ArrayList<UserVo>();
    	threads.clear();
    	if(recipientsSearchUser.startsWith("\"") && recipientsSearchUser.endsWith(">")){
    		
    		users.add(MailCompletionService.getUserFromDisplay(recipientsSearchUser));
        	for(UserVo current : users ){
        		if(criteriaOnSearch.equals("admin")){
        			
        			List<UserVo> user = userFacade.searchUser(current.getMail(), current.getFirstName(), current.getLastName(), userVo);
        			UserVo currentUser = userFacade.findUser(user.get(0).getDomainIdentifier(), user.get(0).getMail());
        			threads = threadEntryFacade.getAllMyThreadWhereAdmin(currentUser);
        		} else if(criteriaOnSearch.equals("simple")){
        			
        			List<UserVo> user = userFacade.searchUser(current.getMail(), current.getFirstName(), current.getLastName(), userVo);
        			UserVo currentUser = userFacade.findUser(user.get(0).getDomainIdentifier(), user.get(0).getMail());
        			List<ThreadVo> threadSimple  = threadEntryFacade.getAllMyThreadWhereCanUpload(currentUser);
        			for(ThreadVo currentThread : threadSimple){
        				if(!(threadEntryFacade.userIsAdmin(currentUser, currentThread))){
        					threads.add(currentThread);
        				}
        			}
        			
        		} else if(criteriaOnSearch.equals("restricted")){
        			
        			List<UserVo> user = userFacade.searchUser(current.getMail(), current.getFirstName(), current.getLastName(), userVo);
        			UserVo currentUser = userFacade.findUser(user.get(0).getDomainIdentifier(), user.get(0).getMail());
        			
        			List<ThreadVo> threadRestricted = threadEntryFacade.getAllMyThread(currentUser);
        			for(ThreadVo currentThread : threadRestricted){
        				if(!(threadEntryFacade.userIsAdmin(currentUser, currentThread)) && !(threadEntryFacade.userCanUpload(currentUser, currentThread))){
        					threads.add(currentThread);
        				}
        			}
        			
        		} else {
        				
        			List<UserVo> user = userFacade.searchUser(current.getMail(), current.getFirstName(), current.getLastName(), userVo);
        			UserVo currentUser = userFacade.findUser(user.get(0).getDomainIdentifier(), user.get(0).getMail());
        			threads = threadEntryFacade.getAllMyThread(currentUser);
        		}
        	}
    	}
    	return null;
    }
    
	public Object onSuccessFromFormSearch() throws BusinessException{	
		if(inSearch){
			threads.clear();
			if(recipientsSearchThread.equals("*")){
				threads = threadEntryFacade.getAllThread();
			} else if(recipientsSearchThread.startsWith("\"") && recipientsSearchThread.endsWith(">")){
				String chain =  threadEntryFacade.getThreadFromSearch(recipientsSearchThread);
				int index = chain.indexOf("+");
				String threadName = chain.substring(0,index);
				String owner = chain.substring(index+1,chain.length());
				List<ThreadVo> lists = threadEntryFacade.getAllThread();
				
				for(ThreadVo current : lists){
					if((current.getName().equals(threadName)) && (userFacade.findUserByLsUuid(userVo,currentThread.getOwnerLsUuid()).getFullName().equals(owner))){
						threads.add(current);
					}
				}
				
			} else {
				List<ThreadVo> lists = threadEntryFacade.getAllThread();
				
				for(ThreadVo current : lists){
					if(current.getName().startsWith(recipientsSearchThread)){
						threads.add(current);
					}
				}
			}
		}
		return null;
	}
	
    public void onActionFromDeleteThread(String lsuuid,String ownerlsUuid) throws BusinessException {
    	this.threadToDelete= threadEntryFacade.getThread(userFacade.findUserByLsUuid(userVo,ownerlsUuid), lsuuid);
		logger.debug("thread to delete :"+threadToDelete.getName());
    }
	
	@OnEvent(value="deleteThreadEvent")
	public void deleteThread() {
		try {
			threadEntryFacade.deleteThread(userFacade.findUserByLsUuid(userVo,threadToDelete.getOwnerLsUuid()),threadToDelete);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
		List<ThreadVo> copy = new ArrayList<ThreadVo>(threads);
		threads.clear();
		for(ThreadVo current : copy){
			if(!(current.getLsUuid().equals(threadToDelete.getLsUuid()))){
				threads.add(current);
			}
		}
	}
	
	public String getAdmin() { 
		return "admin"; 
	}
	
	public String getSimple() { 
		return "simple"; 
	}
	
	public String getrestricted() { 
		return "restricted"; 
	}
	
	public String getAll() { 
		return "all"; 
	}
	
}
