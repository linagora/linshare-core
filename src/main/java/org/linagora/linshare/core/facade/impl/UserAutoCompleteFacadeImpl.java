/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import java.util.Map;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class UserAutoCompleteFacadeImpl extends GenericTapestryFacade implements
		UserAutoCompleteFacade {

	final private static Logger logger = LoggerFactory
			.getLogger(UserAutoCompleteFacadeImpl.class);

	// TODO : Use database configuration for auto complete limit
	final private static int AUTO_COMPLETE_LIMIT = 20;

	final private static int FAVOURTITE_RECIPIENT_LIMIT = 100;

	private UserService userService;

	private final FavouriteRepository<String, User, RecipientFavourite> favourite;

	public UserAutoCompleteFacadeImpl(
			final UserService userService,
			final AccountService accountService,
			final FavouriteRepository<String, User, RecipientFavourite> favourite) {
		super(accountService);
		this.userService = userService;
		this.favourite = favourite;
	}

	@Override
	public List<UserVo> autoCompleteUser(UserVo currentUserVo, String pattern)
			throws BusinessException {
		User actor = getActor(currentUserVo);
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : Use database configuration for auto complete limit
		return getUserVoList(users, AUTO_COMPLETE_LIMIT, true);
	}

	@Override
	public List<UserVo> autoCompleteUserSortedByFavorites(UserVo currentUserVo,
			String pattern) throws BusinessException {
		User actor = getActor(currentUserVo);
		List<CompleteUser> results = autoCompleteUser(actor, pattern);
		int range = (results.size() < AUTO_COMPLETE_LIMIT ? results.size()
				: AUTO_COMPLETE_LIMIT);
		return Lists.transform(results.subList(0, range), CompleteUser.toVo());
	}

	private List<CompleteUser> autoCompleteUser(User actor, String pattern) {
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		Map<String, CompleteUser> userMap = Maps.newHashMap();
		for (User user : users) {
			userMap.put(user.getMail(), new CompleteUser(user));
		}
		List<RecipientFavourite> findMatchElementsOrderByWeight = favourite
				.findMatchElementsOrderByWeight(pattern, actor,
						FAVOURTITE_RECIPIENT_LIMIT);
		for (RecipientFavourite recipientFavourite : findMatchElementsOrderByWeight) {
			String recipient = recipientFavourite.getRecipient();
			if (userMap.containsKey(recipient)) {
				CompleteUser user = userMap.get(recipient);
				user.setWeight(recipientFavourite.getWeight());
			} else {
				userMap.put(recipient, new CompleteUser(recipientFavourite));
			}
		}
		List<CompleteUser> results = Ordering.natural()
				.immutableSortedCopy(userMap.values()).reverse();
		return results;
	}

	@Override
	public List<String> autoCompleteMail(UserVo currentUserVo, String pattern)
			throws BusinessException {
		User actor = getActor(currentUserVo);
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getMailList(users, AUTO_COMPLETE_LIMIT);
	}

	/***********************/
	/** Utility functions **/
	/***********************/
	/**
	 * @author fred
	 * 
	 */
	static class CompleteUser implements Comparable<CompleteUser> {

		protected String firstName;

		protected String lastName;

		protected String mail;

		protected Long weight;

		public CompleteUser(User user) {
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			this.mail = user.getMail();
			this.weight = new Long(0);
		}

		public CompleteUser(RecipientFavourite recipientFavourite) {
			this.mail = recipientFavourite.getRecipient();
			this.weight = recipientFavourite.getWeight();
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getMail() {
			return mail;
		}

		public long getWeight() {
			return weight;
		}

		public void setWeight(long weight) {
			this.weight = weight;
		}

		@Override
		public String toString() {
			return "CompleteUser [weight=" + weight + ", mail=" + mail + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mail == null) ? 0 : mail.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompleteUser other = (CompleteUser) obj;
			if (mail == null) {
				if (other.mail != null)
					return false;
			} else if (!mail.equals(other.mail))
				return false;
			return true;
		}

		@Override
		public int compareTo(CompleteUser o) {
			return weight.compareTo(o.getWeight());
		}

		/*
		 * Transformers
		 */
		public static Function<CompleteUser, UserVo> toVo() {
			return new Function<CompleteUser, UserVo>() {
				@Override
				public UserVo apply(CompleteUser u) {
					return new UserVo(u.getMail(), u.getFirstName(),
							u.getLastName());
				}
			};
		}
	}

	class EmptyUserVo extends UserVo {
		private static final long serialVersionUID = 1188489032838386296L;

		public EmptyUserVo() {
			super(null, null, null, "...", AccountType.INTERNAL);
		}
	}

	/**
	 * Convert a list of Users to a list of UserVo.
	 * 
	 * @param users
	 *            a list of users.
	 * @param limit
	 *            set the max element to be return
	 * @param addEmptyUser
	 *            TODO
	 * @return a list of UserVo.
	 */
	private List<UserVo> getUserVoList(List<User> users, int limit,
			boolean addEmptyUser) {
		List<UserVo> userVos = new ArrayList<UserVo>();
		int count = 0;
		for (User user : users) {
			userVos.add(new UserVo(user));
			count++;
			if (count == limit) {
				if (addEmptyUser)
					userVos.add(new EmptyUserVo());
				break;
			}
		}
		return userVos;
	}

	private List<String> getMailList(List<User> users, int limit) {
		int count = 0;
		List<String> res = new ArrayList<String>();
		for (User user : users) {
			res.add(user.getMail());
			count++;
			if (count == limit) {
				res.add("...");
				break;
			}
		}
		return res;
	}
}
