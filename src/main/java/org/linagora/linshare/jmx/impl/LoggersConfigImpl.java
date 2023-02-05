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
package org.linagora.linshare.jmx.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
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
		org.apache.logging.log4j.Logger currLogger = LogManager.getLogger(loggerName);
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
		org.apache.logging.log4j.Logger currLogger = LogManager.getLogger(loggerName);
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
			Configurator.setLevel(currLogger, level);
			logger.warn("Log level updated at runtime.");
		}
		return currLevel;
	}

	@Override
	public List<String> getLoggers() {
		return loggers;
	}
}
