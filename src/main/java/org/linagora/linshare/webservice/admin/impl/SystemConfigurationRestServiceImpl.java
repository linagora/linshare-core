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
package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.LoggerStatus;
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
		userFacade.isAuthorized(Role.SUPERADMIN, 1);
		logger.warn("Trying to update log level at runtime using logger name : "
				+ loggerName);
		org.apache.log4j.Logger currLogger = org.apache.log4j.LogManager
				.getLogger(loggerName);
		Level level = Level.toLevel(levelStr.toUpperCase());
		logger.warn("Log level value : " + level);
		currLogger.setLevel(level);
		logger.warn("Log level updated at runtime.");
		return new LoggerStatus(currLogger, level);
	}

	@Path("/{loggerName}")
	@GET
	@Override
	public LoggerStatus getLogLevel(
			@PathParam(value = "loggerName") String loggerName) {
		userFacade.isAuthorized(Role.SUPERADMIN, 1);
		org.apache.log4j.Logger currLogger = org.apache.log4j.LogManager
				.getLogger(loggerName);
		return new LoggerStatus(currLogger, currLogger.getLevel());
	}
}
