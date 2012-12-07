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

import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.view.tapestry.enums.Order;
import org.linagora.linshare.view.tapestry.models.SorterModel;

public class FileSorterModel implements SorterModel<DocumentVo>{

	private static final String DATE_FIELD="date";
	private static final String NAME_FIELD="name";
	private static final String TYPE_FIELD="type";
	private static final String SIZE_FIELD="size";
	
	private List<DocumentVo> listToSort;

	public FileSorterModel(List<DocumentVo> listToSort){
		this.listToSort=listToSort;
	}
	/**
	 * all are with their natural sort.
	 * 
	 */
	public Comparator<DocumentVo> getComparator(String fieldId) {
		if(DATE_FIELD.equals(fieldId)){
			return new Comparator<DocumentVo>(){
				public int compare(DocumentVo o1, DocumentVo o2) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
			};
		}
		if(NAME_FIELD.equals(fieldId)){
			return new Comparator<DocumentVo>(){
				public int compare(DocumentVo o1, DocumentVo o2) {
					//Ignoring accents in comparing.
					Collator usCollator = Collator.getInstance(Locale.US);
					usCollator.setStrength(Collator.PRIMARY);
					return usCollator.compare(o1.getFileName(), o2.getFileName());
				}
			};
		}
		if(TYPE_FIELD.equals(fieldId)){
			return new Comparator<DocumentVo>(){
				public int compare(DocumentVo o1, DocumentVo o2) {
					
					return o1.getType().compareToIgnoreCase(o2.getType());
				}
			};
		}
		if(SIZE_FIELD.equals(fieldId)){
			return new Comparator<DocumentVo>(){
				public int compare(DocumentVo o1, DocumentVo o2) {
					return o1.getSize().compareTo(o2.getSize());
				}
			};
		}
		return null;
	}

	public List<DocumentVo> getListToSort() {
		return listToSort;
	}

	public Order getOrder(String fieldId) {
		if(DATE_FIELD.equals(fieldId)){
			return Order.DESC;
		}
		if(NAME_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(TYPE_FIELD.equals(fieldId)){
			return Order.ASC;
		}
		if(SIZE_FIELD.equals(fieldId)){
			return Order.DESC;
		}
		return Order.ASC;
	}


}
