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
