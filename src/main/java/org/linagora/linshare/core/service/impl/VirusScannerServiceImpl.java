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
package org.linagora.linshare.core.service.impl;

import java.io.File;

import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirusScannerServiceImpl implements VirusScannerService {

	protected Logger logger = LoggerFactory.getLogger(VirusScannerServiceImpl.class);

	protected final FunctionalityReadOnlyService functionalityService;

	protected final VirusScannerBusinessService businessService;

	protected final Long sizeLimit;

	public VirusScannerServiceImpl(FunctionalityReadOnlyService functionalityService,
			VirusScannerBusinessService virusScannerBusinessService, Long sizeLimit) {
		super();
		this.functionalityService = functionalityService;
		this.businessService = virusScannerBusinessService;
		this.sizeLimit = sizeLimit;
	}

	@Override
	public Boolean checkVirus(String fileName, Account owner, File file, Long size) throws BusinessException {
		Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(owner.getDomain());
		if (antivirusFunctionality.getActivationPolicy().getStatus()) {
			boolean enabled = !businessService.isDisabled();
			logger.debug("antivirus activation:" + enabled);
			if (enabled) {
				if (sizeLimit != null && size > sizeLimit) {
					logger.info("Threashold reached, antivirus skipped. owner {}, filename {}", owner, fileName);
					return false;
				} else {
					scanVirus(fileName, owner, file, size);
					return true;
				}
			}
		}
		return false;
	}

	protected void scanVirus(String fileName, Account owner, File file, Long size) throws BusinessException {
		boolean checkStatus = false;
		try {
			checkStatus = businessService.check(file);
		} catch (TechnicalException e) {
			// LogEntry logEntry = new AntivirusLogEntry(owner,
			// LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
			logger.error("File scan failed: antivirus enabled but not available ?");
			// logEntryService.create(LogEntryService.ERROR, logEntry);
			throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED, "File scan failed", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus scan result : " + checkStatus);
		}
		// check if the file contains virus
		if (!checkStatus) {
			// LogEntry logEntry = new AntivirusLogEntry(owner,
			// LogAction.FILE_WITH_VIRUS, fileName);
			// logEntryService.create(LogEntryService.WARN, logEntry);
			logger.warn(owner.getLsUuid() + " tried to upload a file containing virus:" + fileName);
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS, "File contains virus", extras);
		}
	}
}
