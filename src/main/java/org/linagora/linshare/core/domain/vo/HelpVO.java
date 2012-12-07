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
package org.linagora.linshare.core.domain.vo;

import java.util.UUID;

import org.linagora.linshare.view.tapestry.enums.HelpType;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((helpType == null) ? 0 : helpType.hashCode());
		result = prime * result
				+ ((idSection == null) ? 0 : idSection.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HelpVO other = (HelpVO) obj;
		if (helpType == null) {
			if (other.helpType != null)
				return false;
		} else if (!helpType.equals(other.helpType))
			return false;
		if (idSection == null) {
			if (other.idSection != null)
				return false;
		} else if (!idSection.equals(other.idSection))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	
	


	
	
	
}
