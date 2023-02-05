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

import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.jmx.AntivirusServerConfig;

public class AntivirusServerConfigImpl implements AntivirusServerConfig {

	private VirusScannerBusinessService virusScannerService;

	public AntivirusServerConfigImpl(VirusScannerBusinessService virusScannerService) {
		super();
		this.virusScannerService = virusScannerService;
	}

	@Override
	public String getHost() {
		return virusScannerService.getHost();
	}

	@Override
	public void setHost(String host) {
		virusScannerService.setHost(host);
	}

	@Override
	public Integer getPort() {
		return virusScannerService.getPort();
	}

	@Override
	public void setPort(Integer port) throws Exception {
		virusScannerService.setPort(port);
	}

}
