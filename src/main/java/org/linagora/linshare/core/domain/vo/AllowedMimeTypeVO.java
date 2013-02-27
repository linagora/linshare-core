/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import org.linagora.linshare.core.domain.entities.MimeTypeStatus;

public class AllowedMimeTypeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4186209866758458217L;
	private long id;
	private String extensions;
	private String mimetype;
	private MimeTypeStatus status;
	
	
	public AllowedMimeTypeVO() {
		this (-1,null,null,MimeTypeStatus.AUTHORISED);
	}
	
	public AllowedMimeTypeVO(long id,String mimetype,String extensions, MimeTypeStatus status){
		this.id=id;
		this.extensions = extensions;
		this.mimetype = mimetype;
		this.status = status;
	}

	
	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public MimeTypeStatus getStatus() {
		return status;
	}

	public void setStatus(MimeTypeStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AllowedMimeTypeVO) {
			AllowedMimeTypeVO vo = (AllowedMimeTypeVO) obj;
			return (vo.getId()== this.getId());
		}
		return false;
		
	}
	
	@Override
	public int hashCode() {
		return (int) this.id;
	}
	
	
	public String getLabel() {
		return toString();
	}
	
	@Override
	public String toString() {
		String extensionsStr = (extensions==null)? "":extensions;
		return "["+extensionsStr+"]:"+this.mimetype;
	}
	
}
