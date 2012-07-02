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

import java.io.Serializable;
import java.util.Calendar;

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
	
	
	public DocumentVo(String identifier,String name, String fileComment, Calendar creationDate,
			Calendar expirationDate,String type, String ownerLogin, Boolean encrypted,
			Boolean shared,Long size) {
		super();
		this.identifier=identifier;
		this.fileName = name;
		this.creationDate = (Calendar)creationDate.clone();
		if(expirationDate !=null) {
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
