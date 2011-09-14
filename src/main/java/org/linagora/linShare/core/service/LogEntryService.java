package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.exception.BusinessException;
import org.slf4j.spi.LocationAwareLogger;

public interface LogEntryService {
	
	final public int INFO= LocationAwareLogger.INFO_INT;
	final public int WARN= LocationAwareLogger.WARN_INT;
	final public int ERROR= LocationAwareLogger.ERROR_INT;
	
	final public String USER_ACTIVITY_MARK = "USER_ACTIVITY";
	

	/**
	 * 
	 * @param level : logger level like INFO, WARN
	 * @param entity : to be create in the database
	 * @return : the log statement created
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	public LogEntry create(int level, LogEntry entity) throws IllegalArgumentException, BusinessException;
	
	
	/**
	 * 
	 * @param entity : to be create in the database
	 * @return : the log statement created
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	public LogEntry create(LogEntry entity) throws IllegalArgumentException, BusinessException;
	

}
