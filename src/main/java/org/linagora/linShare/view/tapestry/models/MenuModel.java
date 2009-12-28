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
package org.linagora.linShare.view.tapestry.models;

public interface MenuModel<T> {

	
	/**
	 * this method permits to obtain the link for an item in particular.
	 * @param <T>id the identifier which permits to retrieve the link for the current item.
	 * @return linkItem the link from the item
	 */
	public String getLinkItem(T id);
	
	/**
	 * this method permits to obtain the label for an item in particular.
	 * @param <T>id the identifier which permits to retrieve the label for the current item.
	 * @return
	 */
	public String getLabelItem(T id);
	
	/**
	 * this method permits to obtain the label for an item in particular.
	 * @param <T>id the identifier which permits to retrieve the label for the current item.
	 * @return
	 */
	public String getImage(T id);
	
	
	/**
	 * retrieve the id of the current object
	 * @param o
	 * @return <T> id the id of the current object
	 */
	public T getId(Object o);
}
