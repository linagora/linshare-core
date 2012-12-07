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

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.linagora.linshare.core.domain.entities.DocumentEntry;


@XmlRootElement(name = "Document")
public class Document {

	private String id;
	private String name;
	private Calendar creation;
	
	
	public Document(DocumentEntry de) {
		
		if(de==null) return;
		this.id = de.getUuid();
		this.name = de.getName();
		this.creation = de.getCreationDate();
	}
	
	public Document() {
	}
	
	@XmlElement(name = "uuid")
	@JsonProperty("uuid") 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Calendar getCreation() {
		return creation;
	}
	public void setCreation(Calendar creation) {
		this.creation = creation;
	}
	
	@Override
	public String toString() {
		return "Document [id=" + id + ", name=" + name + ", creation=" + creation + "]";
	}
	
}
