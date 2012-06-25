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
package org.linagora.linshare.view.tapestry.models.impl;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.linagora.linshare.core.domain.constants.UserType;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.enums.Order;
import org.linagora.linshare.view.tapestry.models.SorterModel;

public class UserSorterModel implements SorterModel<UserVo>{

	private static final String NAME_FIELD="name";
	private static final String MAIL_FIELD="mail";
	private static final String EXPIRATION_FIELD="expiration";
	private List<UserVo> listToSort;

	public UserSorterModel(List<UserVo> listToSort){
		this.listToSort=listToSort;
	}
	/**
	 * all are with their natural sort.
	 * 
	 */
	public Comparator<UserVo> getComparator(String fieldId) {
		if(NAME_FIELD.equals(fieldId)){
			return new Comparator<UserVo>(){
				public int compare(UserVo o1, UserVo o2) {
					
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getLastName(), o2.getLastName());
				}
			};
		}
		if(MAIL_FIELD.equals(fieldId)){
			return new Comparator<UserVo>(){
				public int compare(UserVo o1, UserVo o2) {
					//Ignoring accents in comparing.
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getMail(), o2.getMail());
				}
			};
		}
		if(EXPIRATION_FIELD.equals(fieldId)){
			return new Comparator<UserVo>(){
				public int compare(UserVo o1, UserVo o2) {
					/*
					 * for internal user the expiration date is null.
					 * two guests.
					 */
					if(o1.getExpirationDate()!=null && o2.getExpirationDate()!=null){
						return o1.getExpirationDate().compareTo(o2.getExpirationDate());
					}
					/*
					 * internal compareTo guest.
					 */
					if(o1.getUserType().equals(UserType.INTERNAL) && o2.getUserType().equals(UserType.GUEST)){
						return 1;
					}
					/*
					 * guest compareTo internal.
					 */
					if(o1.getUserType().equals(UserType.GUEST) && o2.getUserType().equals(UserType.INTERNAL)){
						return -1;
					}
						
					return -1;
				}
			};
		}
		return null;
	}

	public List<UserVo> getListToSort() {
		return listToSort;
	}

	public Order getOrder(String fieldId) {

		if(NAME_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(MAIL_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(EXPIRATION_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		
		return Order.ASC;
	}


}
