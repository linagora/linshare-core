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
package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.LoggerStatus;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.admin.SystemConfigurationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/loggers")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SystemConfigurationRestServiceImpl implements
		SystemConfigurationRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(SystemConfigurationRestServiceImpl.class);

	private final UserFacade userFacade;

	public SystemConfigurationRestServiceImpl(UserFacade userFacade) {
		super();
		this.userFacade = userFacade;
	}

	@Path("/{loggerName}/{level}")
	@GET
	@Override
	public LoggerStatus changeLogLevel(
			@PathParam(value = "loggerName") String loggerName,
			@PathParam(value = "level") String levelStr) {
		userFacade.isAuthorized(Role.SUPERADMIN, Version.V1);
		logger.warn("Trying to update log level at runtime using logger name : "
				+ loggerName);
		org.apache.logging.log4j.Logger currLogger = LogManager.getLogger(loggerName);
		Level level = Level.toLevel(levelStr.toUpperCase());
		logger.warn("Log level value : " + level);
		Configurator.setLevel(currLogger, level);
		logger.warn("Log level updated at runtime.");
		return new LoggerStatus(currLogger, level);
	}

	@Path("/{loggerName}")
	@GET
	@Override
	public LoggerStatus getLogLevel(
			@PathParam(value = "loggerName") String loggerName) {
		userFacade.isAuthorized(Role.SUPERADMIN, Version.V1);
		org.apache.logging.log4j.Logger currLogger = LogManager.getLogger(loggerName);
		return new LoggerStatus(currLogger, currLogger.getLevel());
	}
}
