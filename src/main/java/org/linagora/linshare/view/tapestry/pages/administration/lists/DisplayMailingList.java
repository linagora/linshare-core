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

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;

public class DisplayMailingList {
	
    @Inject
    private MailingListFacade mailingListFacade;
	
	@SessionState(create=false)
    @Property
    private MailingListVo mailingList;
	
	private List<MailingListContactVo> lists;
	
	@Persist
	@Property
	private List<UserVo> contacts;

	@Property
	private UserVo contact;
	
	private MailingListContactVo list;
	
    @SessionState
    private UserVo loginUser;
    
    private boolean isEmpty;
    
    
	
	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.administration.lists.Index index;
	
	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
		} else {
			mailingList = null;
		}
		
		isEmpty = mailingList.getMails().isEmpty();
		if(!isEmpty) {
			lists = new ArrayList<MailingListContactVo>();
			lists = mailingList.getMails();
			contacts = new ArrayList<UserVo>();
			for(MailingListContactVo current :lists){
				contacts.add(mailingListFacade.getUserFromDisplay(current.getDisplay()));
			}
		}
	}
	
    public Object onActionFromBack() {
        mailingList=null;
        return index;
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
