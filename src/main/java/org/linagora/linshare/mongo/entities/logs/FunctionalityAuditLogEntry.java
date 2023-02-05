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
package org.linagora.linshare.mongo.entities.logs;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.mongo.entities.mto.FunctionalityMto;

@XmlRootElement
public class FunctionalityAuditLogEntry extends AuditLogEntryAdmin {

	protected FunctionalityMto resource;

	private FunctionalityMto resourceUpdated;

	public FunctionalityAuditLogEntry() {
	}

	public FunctionalityAuditLogEntry(Account authUser, LogAction action, AuditLogEntryType type, Functionality func) {
		super(authUser, func.getDomain().getUuid(), action, type, func.getIdentifier());
		this.setResource(new FunctionalityMto(func));
	}

	public FunctionalityMto getResource() {
		return resource;
	}

	public void setResource(FunctionalityMto func) {
		this.resource = func;
	}

	public FunctionalityMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(FunctionalityMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
