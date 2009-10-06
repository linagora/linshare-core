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
package org.linagora.linShare.core.domain.vo;

/**
 * Value object representation of a favourite.
 * @author ngapaillard
 *
 */
public class RecipientFavouriteVO {

	/** the weight of the element. **/
	private final Long weight;
	/**
	 * the owner of the favourite.
	 */
	private final UserVo owner;
	
	/** the recipient concerned by the favorite */
	private final String recipient;
	



	/**
	 * The constructor with definition of a recipient.
	 * @param recipient.
	 */
	public RecipientFavouriteVO(UserVo owner,String recipient,Long weight){

		this.recipient=recipient;
		this.owner=owner;
		this.weight=weight;
		
	}

	/**
	 * Retrieve the current element weight.
	 * @return the weight of the current element.
	 */
	public Long getWeight(){
		return this.weight;
	}
	
	/**
	 * give the owner of the favorite.
	 * @return owner the owner of the favourite.
	 */
	public UserVo getOwner() {
		return owner;
	}
	
	/**
	 * Give the recipient.
	 * @return recipient the recipient.
	 */
	public String getRecipient() {
		return recipient;
	}

}
