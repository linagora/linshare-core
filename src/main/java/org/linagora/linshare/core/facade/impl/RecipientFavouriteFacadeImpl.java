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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.service.RecipientFavouriteService;
import org.linagora.linshare.core.service.UserService;

public class RecipientFavouriteFacadeImpl implements RecipientFavouriteFacade{

	private RecipientFavouriteService service;
	private UserService userService;

	public RecipientFavouriteFacadeImpl(UserService userService,RecipientFavouriteService service){
		this.service=service;
		this.userService=userService;
	}

	
	@Override
	public void increment(UserVo ownerVo, List<String> recipients) throws LinShareNotSuchElementException, BusinessException {
		User owner = userService.findByLsUid(ownerVo.getLsUid());
		service.increment(owner, recipients);
	}
	

	@Override
	public List<String> allRecipientsOrderedByWeightDesc(UserVo ownerVo) throws BusinessException{
		User owner = userService.findByLsUid(ownerVo.getLsUid());
		return service.recipientsOrderedByWeightDesc(owner);
	}
	

	@Override
	public List<UserVo> recipientsOrderedByWeightDesc(List<UserVo> recipients, UserVo ownerVo) throws BusinessException {
		
		if(recipients.size()==0) return recipients;
		
		User owner = userService.findByLsUid(ownerVo.getLsUid());
		if(owner!=null){
			ArrayList<String> recipientsMail=new ArrayList<String>();

			for(UserVo user: recipients){
				recipientsMail.add(user.getMail());
			}
			
			List<String> reorder=service.reorderRecipientsByWeightDesc(recipientsMail, owner);
			ArrayList<UserVo> reorderUserList=new ArrayList<UserVo>();
			for(String mail:reorder){
				for(UserVo user:recipients){
					if(user.getMail().equals(mail)){
						reorderUserList.add(user);
						break;
					}
				}
			}
			return reorderUserList;
		}else{
			return recipients;
		}
	}

	
	@Override
	public List<UserVo> findRecipientFavorite(String matchStartWith,UserVo ownerVo) throws BusinessException {
		User owner = userService.findByLsUid(ownerVo.getLsUid());
		List<String> mails = service.findRecipientFavorite(matchStartWith, owner);
		ArrayList<UserVo> favoriteList=new ArrayList<UserVo>();
		for (String email : mails) {
			favoriteList.add(new UserVo(email,null,null,email,null));
		}
		return favoriteList;
	}

}
