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

import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.DocumentType;

/**
 * Search document Vo for retrieve documents.
 * @author ngapaillard
 *
 */
public class SearchDocumentCriterion {

	
	private final UserVo user;
	private final String name;
	private final Long sizeMin;
	private final Long sizeMax;
	private final String type;
	
	// TODO: Fix or remove. Currently not used
	private final Boolean shared;
	
	private final Calendar dateBegin;
	private final Calendar dateEnd;
	private final DocumentType documentType;
	private final String sharedFrom;
	private final String extension;
	
	/**
	 * Build the searchVo which is a filter for retrieve information from database.
	 * @param userVo the user who has the document
	 * @param name of the document
	 * @param size of the document
	 * @param type of the document
	 * @param shared if the document is shared 
	 */
	public SearchDocumentCriterion(UserVo user,String name, Long sizeMin,Long sizeMax, String type, 
			Boolean shared,Calendar dateBegin,Calendar dateEnd, String extension,String sharedFrom,DocumentType documentType) {
		super();
		this.name = name;
		this.sizeMin = sizeMin;
		this.sizeMax = sizeMax;
		this.type = type;
		this.shared = shared;
		this.user = user;
		this.dateBegin = dateBegin;
		this.dateEnd = dateEnd;
		this.documentType = documentType;
		this.sharedFrom = sharedFrom;
		this.extension= extension;
	}
	
	public String getName() {
		return name;
	}
	public Long getSizeMin() {
		return sizeMin;
	}
	public String getType() {
		return type;
	}
	public Boolean isShared() {
		return shared;
	}
	public UserVo getUser() {
		return user;
	}

	public Long getSizeMax() {
		return sizeMax;
	}

	public Calendar getDateBegin() {
		return dateBegin;
	}

	public Calendar getDateEnd() {
		return dateEnd;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public Boolean getShared() {
		return shared;
	}

	public String getSharedFrom() {
		return sharedFrom;
	}

	public String getExtension() {
		return extension;
	}
}
