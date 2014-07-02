/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
		User owner = userService.findByLsUuid(ownerVo.getLsUuid());
		service.increment(owner, recipients);
	}
	

	@Override
	public List<String> allRecipientsOrderedByWeightDesc(UserVo ownerVo) throws BusinessException{
		User owner = userService.findByLsUuid(ownerVo.getLsUuid());
		return service.recipientsOrderedByWeightDesc(owner);
	}
	

	@Override
	public List<UserVo> recipientsOrderedByWeightDesc(List<UserVo> recipients, UserVo ownerVo) throws BusinessException {
		
		if(recipients.size()==0) return recipients;
		
		User owner = userService.findByLsUuid(ownerVo.getLsUuid());
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
		User owner = userService.findByLsUuid(ownerVo.getLsUuid());
		List<String> mails = service.findRecipientFavorite(matchStartWith, owner);
		ArrayList<UserVo> favoriteList=new ArrayList<UserVo>();
		for (String email : mails) {
			favoriteList.add(new UserVo(email,null,null,email,null));
		}
		return favoriteList;
	}

}
