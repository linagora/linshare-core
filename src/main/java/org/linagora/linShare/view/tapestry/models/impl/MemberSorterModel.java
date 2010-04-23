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
package org.linagora.linShare.view.tapestry.models.impl;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.linagora.linShare.core.domain.vo.GroupMemberVo;
import org.linagora.linShare.view.tapestry.enums.Order;
import org.linagora.linShare.view.tapestry.models.SorterModel;

public class MemberSorterModel implements SorterModel<GroupMemberVo>{

	private static final String NAME_FIELD="name";
	private static final String MAIL_FIELD="mail";
	private static final String MEMBERSHIP_FIELD="membershipDate";
	private List<GroupMemberVo> listToSort;

	public MemberSorterModel(List<GroupMemberVo> listToSort){
		this.listToSort=listToSort;
	}

	public Comparator<GroupMemberVo> getComparator(String fieldId) {
		if(NAME_FIELD.equals(fieldId)){
			return new Comparator<GroupMemberVo>(){
				public int compare(GroupMemberVo o1, GroupMemberVo o2) {
					
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getLastName(), o2.getLastName());
				}
			};
		}
		if(MAIL_FIELD.equals(fieldId)){
			return new Comparator<GroupMemberVo>(){
				public int compare(GroupMemberVo o1, GroupMemberVo o2) {
					//Ignoring accents in comparing.
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getMail(), o2.getMail());
				}
			};
		}
		if(MEMBERSHIP_FIELD.equals(fieldId)){
			return new Comparator<GroupMemberVo>(){
				public int compare(GroupMemberVo o1, GroupMemberVo o2) {
					if(o1.getMembershipDate()!=null && o2.getMembershipDate()!=null){
						return o1.getMembershipDate().compareTo(o2.getMembershipDate());
					}
						
					return -1;
				}
			};
		}
		return null;
	}

	public List<GroupMemberVo> getListToSort() {
		return listToSort;
	}

	public Order getOrder(String fieldId) {

		if(NAME_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(MAIL_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(MEMBERSHIP_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		
		return Order.ASC;
	}


}
