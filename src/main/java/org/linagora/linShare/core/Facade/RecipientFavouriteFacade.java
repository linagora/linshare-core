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
package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;

public interface RecipientFavouriteFacade {
	/**
	 * Add one point to the recipients.
	 * if the recipient doesn't exist, the method will create one and assign him one weight. 
	 * @param owner the owner of the favourites. 
	 * @param recipient the recipients that will be increased of one weight.
	 */
	public void increment(UserVo owner,List<String> recipients)  throws LinShareNotSuchElementException, BusinessException ;
	
	/**
	 * Give all recipients associated to the owner ordered by point in descendant way.
	 * @param owner the owner having the favourites.
	 * @return recipients all recipients associated to the owner ordered by point in descendant way.
	 */
	public List<String> allRecipientsOrderedByWeightDesc(UserVo owner);
	
	/**
	 * Give all recipients associated to the owner ordered by point in descendant way.
	 * @param recipients to reordered (UserVo form).
	 * @return recipients all recipients associated to the owner ordered by point in descendant way (UserVo form).
	 */
	public List<UserVo> recipientsOrderedByWeightDesc(List<UserVo> recipients,UserVo owner);

	/**
	 * find all user's recipients of his sharing process. 
	 *  
	 * @param matchStartWith recipients which begin with match pattern ordered by point in descendant way
	 * @param owner owner of the recipient
	 * @return list of favorite user
	 */
	public List<UserVo> findRecipientFavorite(String matchStartWith,UserVo owner);
}
