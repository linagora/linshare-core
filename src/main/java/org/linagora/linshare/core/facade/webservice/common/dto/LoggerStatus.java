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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@XmlRootElement(name = "LoggerStatus")
public class LoggerStatus {

	private String name;

	private String level;

	public LoggerStatus() {
		super();
	}

	public LoggerStatus(String name, String level) {
		super();
		this.name = name;
		this.level = level;
	}

	public LoggerStatus(Logger logger, Level level) {
		super();
		this.name = logger.getName();
		if (level != null) {
			this.level = level.toString();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
