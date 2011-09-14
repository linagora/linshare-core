package org.linagora.linShare.core.service.impl;

import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.service.LogEntryService;
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
		 
		 builder.append(USER_ACTIVITY_MARK+":");
		 
		 builder.append(entity.getLogAction());
		 
		 if (entity.getActorDomain() != null) {
			 builder.append(":"+entity.getActorDomain());
		 } else {
			 builder.append(":null domain");
		 }
		 builder.append(":"+entity.getActorMail());
		 builder.append(":"+entity.getDescription());

		 	 
		 // Add specific test for specific data
		 if(entity instanceof FileLogEntry) {
			 builder.append(":");
			 builder.append("file="+((FileLogEntry)entity).getFileName());
			 builder.append(",");
			 builder.append("size="+((FileLogEntry)entity).getFileSize());
		 }
		 return builder.toString();
	}
	
	@Override
	public LogEntry create(int level, LogEntry entity) throws IllegalArgumentException, BusinessException  {

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
	public LogEntry create(LogEntry entity) throws IllegalArgumentException,
			BusinessException {
		return create(INFO,entity);
	}
	

}
