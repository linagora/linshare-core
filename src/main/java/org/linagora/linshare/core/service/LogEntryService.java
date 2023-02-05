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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.entities.EventNotification;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.slf4j.spi.LocationAwareLogger;

public interface LogEntryService {

	final public int INFO = LocationAwareLogger.INFO_INT;

	final public int WARN = LocationAwareLogger.WARN_INT;

	final public int ERROR = LocationAwareLogger.ERROR_INT;

	public List<LogEntry> findByCriteria(User actor, LogCriteriaBean criteria);

	AuditLogEntryUser insert(AuditLogEntryUser entry);

	AuditLogEntryUser insert(AuditLogEntryUser entry, EventNotification event);

	AuditLogEntryUser insert(int level, AuditLogEntryUser entry);

	AuditLogEntryUser insert(int level, AuditLogEntryUser entry, EventNotification event);
	
	AuditLogEntryAdmin insert(AuditLogEntryAdmin entry);

	List<AuditLogEntryUser> insert(List<AuditLogEntryUser> entities);

	List<AuditLogEntryUser> insert(List<AuditLogEntryUser> entities, List<EventNotification> events);

	List<AuditLogEntryUser> insert(int level, List<AuditLogEntryUser> entities);

	List<AuditLogEntryUser> insert(int level, List<AuditLogEntryUser> entities, List<EventNotification> events);

	EventNotification insertEvent(EventNotification event);
	
	AuditLogEntryAdmin insert(int level, AuditLogEntryAdmin entity);

	BasicStatistic generateBasicStatistic(AuditLogEntry entity);
}
