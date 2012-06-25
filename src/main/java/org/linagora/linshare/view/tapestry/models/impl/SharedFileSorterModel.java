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

import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.view.tapestry.enums.Order;
import org.linagora.linshare.view.tapestry.models.SorterModel;

public class SharedFileSorterModel implements SorterModel<ShareDocumentVo>{

	private static final String DATE_SHARING_FIELD="dateSharing";
//	private static final String DATE_CREATION_FIELD="dateCreation";
	private static final String DATE_EXPIRATION_FIELD="dateExpiration";
	private static final String NAME_FIELD="name";
	private static final String SIZE_FIELD="size";
	
	private List<ShareDocumentVo> listToSort;

	public SharedFileSorterModel(List<ShareDocumentVo> listToSort){
		this.listToSort=listToSort;
	}
	/**
	 * all are with their natural sort.
	 * 
	 */
	public Comparator<ShareDocumentVo> getComparator(String fieldId) {
		if(DATE_SHARING_FIELD.equals(fieldId)){
			return new Comparator<ShareDocumentVo>(){
				public int compare(ShareDocumentVo o1, ShareDocumentVo o2) {
					return o1.getSharingDate().compareTo(o2.getSharingDate());
				}
			};
		}
//		if(DATE_CREATION_FIELD.equals(fieldId)){
//			return new Comparator<ShareDocumentVo>(){
//				public int compare(ShareDocumentVo o1, ShareDocumentVo o2) {
//					return o1.getCreationDate().compareTo(o2.getCreationDate());
//				}
//			};
//		}
		if(DATE_EXPIRATION_FIELD.equals(fieldId)){
			return new Comparator<ShareDocumentVo>(){
				public int compare(ShareDocumentVo o1, ShareDocumentVo o2) {
					return o1.getShareExpirationDate().compareTo(o2.getShareExpirationDate());
				}
			};
		}
		if(NAME_FIELD.equals(fieldId)){
			return new Comparator<ShareDocumentVo>(){
				public int compare(ShareDocumentVo o1, ShareDocumentVo o2) {
					//Ignoring accents in comparing.
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getFileName(), o2.getFileName());
				}
			};
		}
		if(SIZE_FIELD.equals(fieldId)){
			return new Comparator<ShareDocumentVo>(){
				public int compare(ShareDocumentVo o1, ShareDocumentVo o2) {
					return o1.getSize().compareTo(o2.getSize());
				}
			};
		}
		return null;
	}

	public List<ShareDocumentVo> getListToSort() {
		return listToSort;
	}

	public Order getOrder(String fieldId) {
		if(DATE_SHARING_FIELD.equals(fieldId)){
			return Order.DESC;
		}
//		if(DATE_CREATION_FIELD.equals(fieldId)){
//			return Order.DESC;
//		}
		if(DATE_EXPIRATION_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(NAME_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(SIZE_FIELD.equals(fieldId)){
			return Order.DESC;
		}
		return Order.ASC;
	}


}
