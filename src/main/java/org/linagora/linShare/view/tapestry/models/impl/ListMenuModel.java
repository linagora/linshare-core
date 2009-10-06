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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.view.tapestry.models.MenuModel;


public class ListMenuModel implements MenuModel<Integer>{

	private final static String SEPARATOR=",";
	private List<String> elements=new ArrayList<String>();
	public ListMenuModel(List<String> element){
		this.elements=element;
	}
	public Integer getId(Object o) {
		
		return elements.indexOf((String)o);
	}

	public String getLabelItem(Integer id) {
		
		return elements.get(id).split(SEPARATOR)[1];
	}

	public String getLinkItem(Integer id) {
		return elements.get(id).split(SEPARATOR)[0];
	}
	
	public String getImage(Integer id) {
		
		return elements.get(id).split(SEPARATOR)[2];
	}

}
