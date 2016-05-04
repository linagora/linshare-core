/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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

import java.io.Serializable;
import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.DocumentType;

/**
 * Search document Vo for retrieve documents.
 * @author ngapaillard
 *
 */
public class SearchDocumentCriterion implements Serializable {

	private static final long serialVersionUID = 2640576961921262270L;
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
