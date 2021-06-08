/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
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

	List<SharedSpaceMemberAuditLogEntry> insertSharedSpaceMemberAuditLogs(Integer level,
			List<SharedSpaceMemberAuditLogEntry> logs);
}
