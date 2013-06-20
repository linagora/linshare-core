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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAutoCompleteFacadeImpl implements UserAutoCompleteFacade {

	final private static Logger logger = LoggerFactory.getLogger(UserAutoCompleteFacadeImpl.class);
	
	private UserService userService;
	
	public UserAutoCompleteFacadeImpl(UserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public List<UserVo> autoCompleteUser(UserVo currentUserVo, String pattern) throws BusinessException {
		List<User> users = userService.autoCompleteUser(currentUserVo.getLogin(), pattern);
		return getUserVoList(users);
	}

	@Override
	public List<UserVo> autoCompleteUserSortedByFavorites(UserVo currentUserVo, String pattern) throws BusinessException {
		List<User> users = userService.autoCompleteUser(currentUserVo.getLogin(), pattern);
			
			// TODO : FIXME : FMA : add favorite sort.
//		userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input, userVo));
//		return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userVo);
			
		return getUserVoList(users);
	}

	/***********************/
	/** Utility functions **/
	/***********************/

	/**
	 * Convert a list of Users to a list of UserVo.
	 * 
	 * @param users
	 *            a list of users.
	 * @return a list of UserVo.
	 */
	private List<UserVo> getUserVoList(List<User> users) {
		List<UserVo> userVOs = new ArrayList<UserVo>();
		for (User user : users) {
			userVOs.add(new UserVo(user));
		}
		return userVOs;
	}
}
