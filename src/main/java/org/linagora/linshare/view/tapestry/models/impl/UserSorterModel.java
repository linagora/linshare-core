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
package org.linagora.linshare.view.tapestry.models.impl;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.linagora.linshare.core.domain.constants.AccountType;
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
					if(o1.getUserType().equals(AccountType.INTERNAL) && o2.getUserType().equals(AccountType.GUEST)){
						return 1;
					}
					/*
					 * guest compareTo internal.
					 */
					if(o1.getUserType().equals(AccountType.GUEST) && o2.getUserType().equals(AccountType.INTERNAL)){
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
