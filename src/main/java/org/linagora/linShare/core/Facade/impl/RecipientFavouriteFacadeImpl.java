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
package org.linagora.linShare.core.Facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.Facade.RecipientFavouriteFacade;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;
import org.linagora.linShare.core.service.RecipientFavouriteService;
import org.linagora.linShare.core.service.UserService;

public class RecipientFavouriteFacadeImpl implements RecipientFavouriteFacade{

	private RecipientFavouriteService service;
	private UserService userService;

	public RecipientFavouriteFacadeImpl(UserService userService,RecipientFavouriteService service){
		this.service=service;
		this.userService=userService;
	}

	public void increment(UserVo owner, List<String> recipients) throws LinShareNotSuchElementException, BusinessException {
		service.increment(userService.findOrCreateUserWithDomainPolicies(owner.getMail(), owner.getDomainIdentifier()), recipients);
	}

	public List<String> allRecipientsOrderedByWeightDesc(UserVo owner) throws BusinessException{
		return service.recipientsOrderedByWeightDesc(userService.findOrCreateUserWithDomainPolicies(owner.getMail(), owner.getDomainIdentifier()));
	}

	public List<UserVo> recipientsOrderedByWeightDesc(List<UserVo> recipients,
			UserVo owner) throws BusinessException {
		
		if(recipients.size()==0) return recipients;
		
		User own = userService.findOrCreateUser(owner.getMail(), owner.getDomainIdentifier());

		if(own!=null){
			ArrayList<String> recipientsMail=new ArrayList<String>();

			for(UserVo user: recipients){
				recipientsMail.add(user.getMail());
			}
			
			List<String> reorder=service.reorderRecipientsByWeightDesc(recipientsMail, own);
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

	public List<UserVo> findRecipientFavorite(String matchStartWith,UserVo owner) throws BusinessException {
		
		User own = userService.findOrCreateUser(owner.getMail(), owner.getDomainIdentifier());
		List<String> mails = service.findRecipientFavorite(matchStartWith, own);
		
		ArrayList<UserVo> favoriteList=new ArrayList<UserVo>();
		for (String email : mails) {
			favoriteList.add(new UserVo(email,null,null,email,null));
		}
		return favoriteList;
	}






}
