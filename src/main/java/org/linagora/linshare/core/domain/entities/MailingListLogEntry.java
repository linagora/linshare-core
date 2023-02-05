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
package org.linagora.linshare.core.domain.entities;

public class MailingListLogEntry extends LogEntry {

	private static final long serialVersionUID = -8388034589530890111L;

	private String identifier;

	private String uuid;

	/*
	 * Default constructor for Hibernate
	 */
	protected MailingListLogEntry() {
		super();
		this.identifier = null;
		this.uuid = null;
	}

	public String getListName() {
		return identifier;
	}

	public void setListName(String listName) {
		this.identifier = listName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
