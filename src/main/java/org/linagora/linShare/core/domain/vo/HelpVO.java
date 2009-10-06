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
package org.linagora.linShare.core.domain.vo;

import java.util.UUID;

import org.linagora.linShare.view.tapestry.enums.HelpType;

public class HelpVO {

	private final String idSection;
	
	private final String role;
	
	private final String descItems;
	
	private final String extension;

	private final HelpType helpType;

	private final String uuid;
	
	/**
	 * Default Constructor for text.
	 * @param idSection the id of the section.
	 * @param role the role for the help.
	 * @param descItems the number of items by subsection.
	 * @param imgExtension the extension of the image.
	 */

	public HelpVO(String idSection, String role, String descItems,
			String extension,HelpType helpType) {
		super();
		this.uuid=UUID.randomUUID().toString();
		this.idSection = idSection;
		this.role = role;
		this.descItems = descItems;
		this.extension = extension;
		this.helpType = helpType;
	}

	/**
	 * Constructor for video.
	 * @param idSection the id of the section.
	 * @param role the role for the help.
	 * @param videoExtension the extension of video.
	 */
	public HelpVO(String idSection, String role,String extension){
		this(idSection,role,null,extension,HelpType.VIDEO);
	}
	

	
	/**
	 * Give the id of the section.
	 * @return idSection the id of the section.
	 */
	public String getIdSection() {
		return idSection;
	}

	/**
	 * Give the role for the help.
	 * @return role the role for the help.
	 */
	public String getRole() {
		return role;
	}
	
	/**
	 * Give the role for the help.
	 * @return role the role for the help.
	 */
	public String getDescItems() {
		return descItems;
	}

	/**
	 * Give the extension associated with the media to display.
	 * @return extension the extension associated with the media to display.
	 */
	public String getExtension() {
		return extension;
	}
	
	
	public HelpType getHelpType() {
		return helpType;
	}

	public String getUuid() {
		return uuid;
	}
	
	@Override
	public boolean equals(Object o){
		if(o==this || 
				(null!=o 
				&& o instanceof HelpVO 
				&& (((HelpVO)o).getIdSection().equals(idSection)) 
				&& ((HelpVO)o).getHelpType().equals(helpType) 
				&& ((HelpVO)o).getUuid().equals(uuid) )){ 
				
			return true;
		}
		return false;
	}


	
	
	
}
