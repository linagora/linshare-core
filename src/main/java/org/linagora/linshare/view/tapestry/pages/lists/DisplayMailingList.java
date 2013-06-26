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

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayMailingList {
	
	
	private static Logger logger = LoggerFactory.getLogger(DisplayMailingList.class);
	
    @Inject
    private MailingListFacade mailingListFacade;
	
	@SessionState(create=false)
    @Property
    private MailingListVo mailingList;
	
	private List<MailingListContactVo> lists;
	
	private MailingListContactVo list;
	
	@Property
	private String firstName;
	
	@Property
	private String lastName;
	
	@Validate("required")
	@Property
	private String email;
	
    @SessionState
    private UserVo loginUser;
    
	@Property
	@Persist(value = "flash")
	private long contactToDelete;
    
    private boolean isEmpty;
    
	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;
	
	@SetupRender
	public void init() throws BusinessException {
		isEmpty = mailingList.getMails().isEmpty();
		if(!isEmpty) {
			lists = new ArrayList<MailingListContactVo>();
			for(MailingListContact contact : mailingList.getMails()) {
				MailingListContactVo current = new MailingListContactVo(contact);
				lists.add(current);
			}
		}
	}
	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
		} else {
			mailingList = null;
		}
	}
	
    public Object onActionFromBack() {
        mailingList=null;
        index.setDisplayGrid(true);
        return index;
     }

	public Object onSuccessFromForm() throws BusinessException {
		
		UserVo user = new UserVo(email,firstName,lastName);
		String display = MailCompletionService.formatLabel(user);
		display = display.substring(0, display.length()-1);
		
		MailingListContactVo newContact = new MailingListContactVo(email,display);
		mailingList.getMails().add(new MailingListContact(newContact));
		mailingListFacade.updateMailingList(mailingList);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
		
		return null;
	}
	
	public boolean getIsInList() throws BusinessException{
		boolean inList = false;
		if(!lists.isEmpty()){
			list = mailingListFacade.retrieveMailingListContact(list.getPersistenceId());
			for(MailingListContactVo current : lists) {
				if(current.getPersistenceId() == list.getPersistenceId()) {
					inList = true;
				}
			}
		}
		return inList;
	}
	
	public void onActionFromDeleteContact(long persistenceId) {
		this.contactToDelete = persistenceId;
	}

	@OnEvent(value = "contactDeleteEvent")
	public void deleteList() throws BusinessException {
		mailingListFacade.deleteMailingListContact(contactToDelete);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
	}
	
	
    public boolean getIsEmpty(){
    	return isEmpty;
    }
    
	public List<MailingListContactVo> getLists() {
		return lists;
	}

	public void setLists(List<MailingListContactVo> lists) {
		this.lists = lists;
	}

	public MailingListContactVo getList() {
		return list;
	}
	
	public void setList(MailingListContactVo list) {
		this.list = list;
	}
}

