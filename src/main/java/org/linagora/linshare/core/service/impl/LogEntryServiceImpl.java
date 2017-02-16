/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.mongo.entities.EventNotification;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.EventNotificationMongoRepository;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class LogEntryServiceImpl implements LogEntryService {

	final static Logger logger = LoggerFactory.getLogger(LogEntryService.class);

	private final LogEntryRepository logEntryRepository;

	private final DomainBusinessService domainBusinessService;

	private final AuditUserMongoRepository auditUserMongoRepository;

	private final EventNotificationMongoRepository eventNotificationMongoRepository;

	public LogEntryServiceImpl(final LogEntryRepository logEntryRepository,
			final AuditUserMongoRepository auditUserMongoRepository,
			final EventNotificationMongoRepository eventNotificationMongoRepository,
			final DomainBusinessService domainBusinessService) {
		super();
		this.logEntryRepository = logEntryRepository;
		this.domainBusinessService = domainBusinessService;
		this.auditUserMongoRepository = auditUserMongoRepository;
		this.eventNotificationMongoRepository = eventNotificationMongoRepository;
	}

	@Override
	public List<LogEntry> findByCriteria(User actor, LogCriteriaBean criteria) {
		List<LogEntry> list = Lists.newArrayList();
		List<String> allMyDomainIdentifiers = domainBusinessService.getAllMyDomainIdentifiers(actor.getDomain());
		for (String domain : allMyDomainIdentifiers) {
			list.addAll(logEntryRepository.findByCriteria(criteria, domain));
		}
		return list;
	}

	@Override
	public AuditLogEntryUser insert(AuditLogEntryUser entity) {
		return insert(INFO, entity);
	}

	@Override
	public List<AuditLogEntryUser> insert(List<AuditLogEntryUser> entities) {
		return insert(INFO, entities);
	}

	@Override
	public AuditLogEntryUser insert(int level, AuditLogEntryUser entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity must not be null");
		}
		// Logger trace
		if (level == INFO) {
			logger.info(entity.toString());
		} else if (level == WARN) {
			logger.warn(entity.toString());
		} else if (level == ERROR) {
			logger.error(entity.toString());
		} else {
			throw new IllegalArgumentException("Unknown log level, is neither INFO, WARN nor ERROR");
		}
		return auditUserMongoRepository.insert(entity);
	}

	@Override
	public List<AuditLogEntryUser> insert(int level, List<AuditLogEntryUser> entities) {
		if (entities == null || entities.isEmpty()) {
			throw new IllegalArgumentException("Entity must not be null or empty");
		}
		// Logger trace
		if (level == INFO) {
			logger.info(entities.toString());
		} else if (level == WARN) {
			logger.warn(entities.toString());
		} else if (level == ERROR) {
			logger.error(entities.toString());
		} else {
			throw new IllegalArgumentException("Unknown log level, is neither INFO, WARN nor ERROR");
		}
		return auditUserMongoRepository.insert(entities);
	}

	@Override
	public AuditLogEntryUser insert(AuditLogEntryUser entry, EventNotification event) {
		AuditLogEntryUser log = insert(entry);
		eventNotificationMongoRepository.insert(event);
		return log;
	}

	@Override
	public AuditLogEntryUser insert(int level, AuditLogEntryUser entry, EventNotification event) {
		AuditLogEntryUser log = insert(level, entry);
		eventNotificationMongoRepository.insert(event);
		return log;
	}

	@Override
	public List<AuditLogEntryUser> insert(List<AuditLogEntryUser> entities, List<EventNotification> events) {
		List<AuditLogEntryUser> log = insert(entities);
		eventNotificationMongoRepository.insert(events);
		return log;
	}

	@Override
	public List<AuditLogEntryUser> insert(int level, List<AuditLogEntryUser> entities,
			List<EventNotification> events) {
		List<AuditLogEntryUser> log = insert(level, entities);
		eventNotificationMongoRepository.insert(events);
		return log;
	}

	@Override
	public EventNotification insertEvent(EventNotification event) {
		return eventNotificationMongoRepository.insert(event);
	}
}
