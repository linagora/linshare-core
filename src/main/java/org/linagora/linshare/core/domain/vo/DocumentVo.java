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

import java.io.Serializable;
import java.util.Calendar;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;

public class DocumentVo implements Serializable, Comparable {

	private static final long serialVersionUID = 8048750523251506651L;

	/**
	 * The size of the document
	 */
	
	private final Long size;
	
	/**
	 * the identifier of the document.
	 */
	private final String identifier;
	
	/**
	 * the fileName of the document.
	 */
	private final String fileName;
	
	/**
	 * the creation date of the document.
	 */
	private final Calendar creationDate;
	
	/**
	 * the expiration date of the document.
	 */
	private final Calendar expirationDate;
	
	/**
	 * the document owner.
	 */
	private final String ownerLogin;
	
	/**
	 * if the document is encrypted.
	 */
	private final Boolean encrypted;
	
	/**
	 * if the document is shared.
	 */
	private final Boolean shared;
	
	/**
	 * the mime type of document
	 */
	private final String type;
	
	
	private final String fileComment;
	
	public DocumentVo(DocumentEntry documentEntry) {
		super();
		this.identifier = documentEntry.getUuid();
		this.fileName = documentEntry.getName();
		this.creationDate = (Calendar)documentEntry.getCreationDate().clone();
		if(documentEntry.getExpirationDate() != null) {
			this.expirationDate = (Calendar)documentEntry.getExpirationDate().clone();
		} else {
			this.expirationDate = null;
		}
		this.ownerLogin = documentEntry.getEntryOwner().getLsUuid();
		this.encrypted = documentEntry.getCiphered();
		this.shared = documentEntry.isShared();
		this.type = documentEntry.getType();
		this.size = documentEntry.getSize();
		this.fileComment = documentEntry.getComment();
	}
	
	public DocumentVo(String identifier,String name, String fileComment, Calendar creationDate,
			Calendar expirationDate,String type, String ownerLogin, Boolean encrypted,
			Boolean shared,Long size) {
		super();
		this.identifier=identifier;
		this.fileName = name;
		this.creationDate = (Calendar)creationDate.clone();
		if(expirationDate != null) {
			this.expirationDate = (Calendar)expirationDate.clone();
		} else {
			this.expirationDate = null;
		}
		this.ownerLogin = ownerLogin;
		this.encrypted = encrypted;
		this.shared = shared;
		this.type=type;
		this.size=size;
		this.fileComment = fileComment;
	}
	
	
	public DocumentVo(AnonymousShareEntry anonymousShareEntry) {
		super();
		this.identifier=anonymousShareEntry.getUuid();
		this.fileName = anonymousShareEntry.getName();
		this.creationDate = (Calendar)anonymousShareEntry.getCreationDate().clone();
		if(anonymousShareEntry.getExpirationDate() !=null) {
			this.expirationDate = (Calendar)anonymousShareEntry.getExpirationDate().clone();
		} else {
			this.expirationDate = null;
		}
		this.ownerLogin = anonymousShareEntry.getEntryOwner().getLsUuid();
		this.encrypted = anonymousShareEntry.getDocumentEntry().getCiphered();
		this.shared = anonymousShareEntry.getDocumentEntry().isShared();
		this.type=anonymousShareEntry.getDocumentEntry().getType();
		this.size=anonymousShareEntry.getDocumentEntry().getSize();
		this.fileComment = anonymousShareEntry.getDocumentEntry().getComment();
	}
	
	
	
	public Long getSize() {
		return size;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getFileName() {
		return fileName;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	
	public boolean getEncrypted() {
		return encrypted;
	}

	public boolean getShared() {
		return shared;
	}

	public String getOwnerLogin() {
		return ownerLogin;
	}

	public String getType() {
		return type;
	}
	
	public String getFileComment() {
		return fileComment;
	}

	@Override
	public boolean equals(Object o){
		if(null==o || !(o instanceof DocumentVo)){
			return false;
		}
		return ((DocumentVo) o).getIdentifier().equals(this.getIdentifier()); 
		
	}
	
	@Override
	public int hashCode(){
		return this.getIdentifier().hashCode();
	}

	@Override
	public int compareTo(Object arg0) { //DESC order
		return -this.creationDate.compareTo(((DocumentVo)arg0).creationDate);
	}
}
