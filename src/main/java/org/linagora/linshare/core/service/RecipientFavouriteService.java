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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;

/**
 * Service for manage favourites recipients with users.
 * @author ngapaillard
 *
 */
public interface RecipientFavouriteService {

	/**
	 * Add one point to the recipient.
	 * if the recipient doesn't exist, the method will create one and assign him one weight. 
	 * @param owner the owner of the favourites. 
	 * @param recipients the recipients that will be increased of one weight.
	 */
	public void increment(User owner,List<String> recipients)  throws LinShareNotSuchElementException, BusinessException ;
	
	/**
	 * Give all recipients associated to the owner ordered by point in descendant way.
	 * @param owner the owner having the favourites.
	 * @return recipients all recipients associated to the owner ordered by point in descendant way.
	 */
	public List<String> recipientsOrderedByWeightDesc(User owner);
	
	/**
	 * reorder elements by weight in desc order.
	 * @param elements
	 * @param owner
	 * @throws LinShareNotSuchElementException
	 */
	public List<String> reorderRecipientsByWeightDesc(List<String> recipients,User owner);
	
	/**
	 * find all favorite which start with the given pattern, for the user of the application
	 * @param matchEmail beginning of an email adress
	 * @param owner
	 * @return list of emails
	 */
	public List<String> findRecipientFavorite(String matchEmail,User owner);
	
	/**
	 * Delete all the favorites of a user
	 * @param owner
	 */
	public void deleteFavoritesOfUser(User owner) throws IllegalArgumentException, BusinessException;
}
