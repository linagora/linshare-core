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
package org.linagora.linshare.view.tapestry.beans;

public class MenuEntry {

	private final String label;
	private final String link;
	private final String image;
	private final String target;
	private final String highlight;
	private Integer id;
	
	public MenuEntry( String link,String label, String image,String target, String highlight, Integer id) {
		super();
		this.label = label;
		this.link = link;
		this.image = image;
		this.target = target;
		this.highlight = highlight;
		this.id=id;
	}
	
	public MenuEntry( String link,String label, String image,String target, String highlight) {
		super();
		this.label = label;
		this.link = link;
		this.image = image;
		this.target = target;
		this.highlight = highlight;
	}
	
	public String getLabel() {
		return label;
	}
	public String getLink() {
		return link;
	}
	public String getImage() {
		return image;
	}
	public Integer getId() {
		return id;
	}
	public String getTarget() {
		return target;
	}
	public String getHighlight() {
		return highlight;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
