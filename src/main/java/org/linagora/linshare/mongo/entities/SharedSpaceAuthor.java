/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.mongo.entities;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Author")
public class SharedSpaceAuthor {
	
	@Schema(description = "uuid")
	protected String uuid;
	
	@Schema(description = "name")
	protected String name;
	
	@Schema(description = "mail")
	protected String mail;

	public SharedSpaceAuthor() {
		super();
	}

	public SharedSpaceAuthor(String name, String mail) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.mail = mail;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "SharedSpaceAuthor [uuid=" + uuid + ", name=" + name + ", mail=" + mail + "]";
	}

}
