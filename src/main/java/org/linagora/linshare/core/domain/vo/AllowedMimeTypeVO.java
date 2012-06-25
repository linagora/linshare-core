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
			if(vo.getId()== this.getId()) return true;
			else return false;
		} else return false;
		
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
