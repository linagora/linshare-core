/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
