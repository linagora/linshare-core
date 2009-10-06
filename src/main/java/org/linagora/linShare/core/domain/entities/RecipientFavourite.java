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
package org.linagora.linShare.core.domain.entities;


/**
 * The recipient favourite.
 * @author ngapaillard
 *
 */
public class RecipientFavourite extends AbstractFavourite<User>{

	/** the recipient concerned by the favorite */
	private String recipient;
	

	private Long persistenceId;
	
	/** constructor for hibernate **/
	protected RecipientFavourite(){};
	
	/**
	 * The constructor with definition of a recipient.
	 * @param recipient.
	 */
	public RecipientFavourite(User owner,String recipient){
		super();
		super.setOwner(owner);
		super.setWeight(new Long(1));
		this.recipient=recipient;
		
	}

	/**
	 * Give the recipient.
	 * @return recipient the recipient.
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Set the recipient.
	 * @param recipient the recipient to set.
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}
	

	
	@Override
	public boolean equals(Object o){
		if(null!=o && o instanceof RecipientFavourite) {
			if(o==this 
					|| (((RecipientFavourite)o).getOwner().equals(this.getOwner())
					&& ((RecipientFavourite)o).getRecipient().equals(this.getRecipient())
					
				)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.getOwner().hashCode()+this.getRecipient().hashCode();
	}
	
	
}
