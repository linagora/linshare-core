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
package org.linagora.linShare.core.service;

import java.util.List;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;

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
}
