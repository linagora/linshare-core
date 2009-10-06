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
package org.linagora.linShare.view.tapestry.beans;


/**
 * Just a pojo for use in Tracker component.
 * @author ngapaillard
 *
 */
public class TrackerObject{
	private String page;
	private String message;
	private boolean last=false;
	
	public TrackerObject(String page,String message,boolean last){
		this.page=page;
		this.message=message;
		this.last=last;
	}
	
	public String getPage(){
		return this.page;
	}
	public String getMessage(){
		return this.message;
	}
	public boolean isLast(){
		return last;
	}
}
