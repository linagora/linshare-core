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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEntryServiceImpl implements LogEntryService {

	final static Logger logger = LoggerFactory.getLogger(LogEntryService.class);
	
	private final LogEntryRepository logEntryRepository;

	public LogEntryServiceImpl(LogEntryRepository logEntryRepository) {
		super();
		this.logEntryRepository = logEntryRepository;
	}
	
	private String getLogMessage(LogEntry entity) {
		 StringBuilder builder = new StringBuilder();
		 
		 builder.append(USER_ACTIVITY_MARK + ':');
		 builder.append(entity.getLogAction());
		 if (entity.getActorDomain() != null) {
			 builder.append(':' + entity.getActorDomain());
		 } else {
			 builder.append(":null domain");
		 }
		 builder.append(':' + entity.getActorMail());
		 builder.append(':' + entity.getDescription());

		 // Add specific test for specific data
		 if(entity instanceof FileLogEntry) {
			 builder.append(':');
			 builder.append("file=" + ((FileLogEntry)entity).getFileName());
			 builder.append(',');
			 builder.append("size=" + ((FileLogEntry)entity).getFileSize());
		 }
		 
		 return builder.toString();
	}
	
	@Override
	public LogEntry create(int level, LogEntry entity) throws IllegalArgumentException, BusinessException {
		 if (entity == null) {
	            throw new IllegalArgumentException("Entity must not be null");
	     }
		 // Logger trace
		 if(level == INFO) {
			 logger.info(getLogMessage(entity));
		 } else if(level == WARN) {
			 logger.warn(getLogMessage(entity));
		 } else if(level == ERROR) {
			 logger.error(getLogMessage(entity));
		 }
		 // Database trace
		 return logEntryRepository.create(entity);
	}

	@Override
	public LogEntry create(LogEntry entity) throws IllegalArgumentException, BusinessException {
		return create(INFO,entity);
	}
}
