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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ServerType;

import java.util.Date;

public abstract class AbstractServerDtoBuilder<T extends AbstractServerDto> {

	protected String uuid;
	protected String name;
	protected String description;
	protected String url;
	protected ServerType serverType;
	protected Date creationDate;
	protected Date modificationDate;

	public AbstractServerDtoBuilder<T> uuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public AbstractServerDtoBuilder<T> name(String name) {
		this.name = name;
		return this;
	}

	public AbstractServerDtoBuilder<T> description(String description) {
		this.description = description;
		return this;
	}

	public AbstractServerDtoBuilder<T> url(String url) {
		this.url = url;
		return this;
	}

	public AbstractServerDtoBuilder<T> serverType(ServerType serverType) {
		this.serverType = serverType;
		return this;
	}

	public AbstractServerDtoBuilder<T> serverType(String serverType) {
		this.serverType = ServerType.fromString(serverType);
		return this;
	}

	public AbstractServerDtoBuilder<T> creationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public AbstractServerDtoBuilder<T> modificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public void validation() {
		Validate.notBlank(name, "name must be set.");
		Validate.notBlank(url, "url must be set.");
		Validate.notNull(serverType, "Server type must be set");
	}

	public abstract T build();
}
