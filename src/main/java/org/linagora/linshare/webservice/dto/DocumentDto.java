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
package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DocumentEntry;


@XmlRootElement(name = "Document")
public class DocumentDto extends EntryDto {

	private Boolean ciphered;
	
	private String type;
	
	private Long size;

	public DocumentDto(DocumentEntry de) {
		super(de);
		this.ciphered = de.getCiphered();
		this.type = de.getDocument().getType();
		this.size = de.getDocument().getSize();
	}
	
	public DocumentDto() {
		super();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getSize() {
		return size;
	}
	
	public void setSize(Long size) {
		this.size = size;
	}
	
	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	@Override
	public String toString() {
		return "Document [id=" + uuid + ", name=" + name + ", creation=" + creationDate + "]";
	}
	
}
