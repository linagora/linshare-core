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
package org.linagora.linshare.jmx.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.linagora.linshare.jmx.LoggersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggersConfigImpl implements LoggersConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(LoggersConfigImpl.class);

	private final List<String> loggers;

	public LoggersConfigImpl(String availableLoggers) {
		super();
		if (availableLoggers != null) {
			List<String> asList = Arrays.asList(availableLoggers.split(","));
			this.loggers = asList;
		} else {
			this.loggers = new ArrayList<String>();
		}
	}

	@Override
	public String level(String loggerName) {
		Validate.notEmpty(loggerName);
		org.apache.log4j.Logger currLogger = org.apache.log4j.LogManager
				.getLogger(loggerName);
		Level level = currLogger.getLevel();
		String currLevel = null;
		if (level != null) {
			currLevel = currLogger.getLevel().toString();
		}
		return currLevel;
	}

	@Override
	public String level(String loggerName, String levelStr) {
		Validate.notEmpty(loggerName);
		org.apache.log4j.Logger currLogger = org.apache.log4j.LogManager
				.getLogger(loggerName);
		Level level = currLogger.getLevel();
		String currLevel = null;
		if (level != null) {
			currLevel = currLogger.getLevel().toString();
		}
		if (levelStr != null) {
			logger.warn("Trying to update log level at runtime using logger name : "
					+ loggerName);
			level = Level.toLevel(levelStr.toUpperCase());
			logger.warn("Log level value : " + level);
			currLogger.setLevel(level);
			logger.warn("Log level updated at runtime.");
		}
		return currLevel;
	}

	@Override
	public List<String> getLoggers() {
		return loggers;
	}
}
