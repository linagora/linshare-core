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
package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Locale;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.LogEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.StatisticsService;

public class StatisticsServiceImpl implements StatisticsService {
	private final UserRepository<User> userRepository;
	private final DocumentRepository documentRepository;
	private final LogEntryRepository logEntryRepository;
	  Locale locale = Locale.getDefault();


	  // TODO write statistic service.
	public StatisticsServiceImpl(UserRepository<User> userRepository,
			DocumentRepository documentRepository,
			LogEntryRepository logEntryRepository) {
		super();
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.logEntryRepository = logEntryRepository;
	}


	@Override
	public int getNbInternalUser() {
		
		return 0;
	}


	@Override
	public int getNbExternalUser() {
		
		return 0;
	}


	@Override
	public int getNbUser() {
		
		return 0;
	}


	@Override
	public int getNbFiles(long minSize, long maxSize) {
		
		return 0;
	}


	@Override
	public int getNbSharedFiles(long minSize, long maxSize) {
		
		return 0;
	}


	@Override
	public int getNbNotSharedFiles(long minSize, long maxSize) {
		
		return 0;
	}


	@Override
	public int getNbCipheredFiles(long minSize, long maxSize) {
		
		return 0;
	}


	@Override
	public int getNbNotCipheredFiles(long minSize, long maxSize) {
		
		return 0;
	}


	@Override
	public int getNbFiles() {
		
		return 0;
	}


	@Override
	public int getNbSharedFiles() {
		
		return 0;
	}


	@Override
	public int getNbNotSharedFiles() {
		
		return 0;
	}


	@Override
	public int getNbCipheredFiles() {
		
		return 0;
	}


	@Override
	public int getNbNotCipheredFiles() {
		
		return 0;
	}


	@Override
	public long getUploadVolume(Calendar minDate, Calendar maxDate) {
		
		return 0;
	}


	@Override
	public long getDownloadVolume(Calendar minDate, Calendar maxDate) {
		
		return 0;
	}


	@Override
	public long getUploadVolume(String minDate, String maxDate) throws Exception {
		
		return 0;
	}


	@Override
	public long getDownloadVolume(String minDate, String maxDate) throws Exception {
		
		return 0;
	}

	
//	@Override
//	public long getDownloadVolume(String minDate, String maxDate) throws Exception {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
//		
//		Date dateMin = dateFormat.parse(minDate);
//		Calendar calendarMin = new GregorianCalendar();
//		calendarMin.setTime(dateMin);
//
//		Date dateMax = dateFormat.parse(maxDate);
//		Calendar calendarMax = new GregorianCalendar();
//		calendarMax.setTime(dateMax);
//
//		return getDownloadVolume(calendarMin, calendarMax);
//	}
//
//	@Override
//	public long getUploadVolume(String minDate, String maxDate) throws Exception {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
//		
//		Date dateMin = dateFormat.parse(minDate);
//		Calendar calendarMin = new GregorianCalendar();
//		calendarMin.setTime(dateMin);
//		
//		Date dateMax = dateFormat.parse(maxDate);
//		Calendar calendarMax = new GregorianCalendar();
//		calendarMax.setTime(dateMax);
//		
//		return getUploadVolume(calendarMin, calendarMax);
//	}
//	
//	@Override
//	public long getDownloadVolume(Calendar minDate, Calendar maxDate) {
//		long volume = 0;
//		LogCriteriaBean logCriteria = new LogCriteriaBean();
//		logCriteria.setAfterDate(maxDate);
//		logCriteria.setBeforeDate(minDate);
//		
//		List<LogEntry> logs = logEntryRepository.findByCriteria(logCriteria, null);
//		
//		for (LogEntry logEntry : logs) {
//			LogAction action = logEntry.getLogAction();
//			if (action.equals(LogAction.ANONYMOUS_SHARE_DOWNLOAD)
//					|| action.equals(LogAction.SHARE_DOWNLOAD)) {
//				FileLogEntry fileLogEntry = (FileLogEntry)logEntry;
//				Long fileSize = fileLogEntry.getFileSize();
//				volume+=fileSize.longValue();
//			}
//		}
//		return volume;
//	}
//
//	@Override
//	public long getUploadVolume(Calendar minDate, Calendar maxDate) {
//		long volume = 0;
//		LogCriteriaBean logCriteria = new LogCriteriaBean();
//		logCriteria.setAfterDate(maxDate);
//		logCriteria.setBeforeDate(minDate);
//		
//		List<LogEntry> logs = logEntryRepository.findByCriteria(logCriteria, null);
//		
//		for (LogEntry logEntry : logs) {
//			LogAction action = logEntry.getLogAction();
//			if (action.equals(LogAction.FILE_UPLOAD)) {
//				FileLogEntry fileLogEntry = (FileLogEntry)logEntry;
//				Long fileSize = fileLogEntry.getFileSize();
//				volume+=fileSize.longValue();
//			}
//		}
//		return volume;
//	}
//
//	@Override
//	public int getNbCipheredFiles(long minSize, long maxSize) {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getEncrypted() && document.getSize() >= minSize && document.getSize() <= maxSize) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbExternalUser() {
//		List<User> allUsers = userRepository.findAll();
//		int count=0;
//		
//		for (User user : allUsers) {
//			if (user.getAccountType().equals(AccountType.GUEST)) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbFiles(long minSize, long maxSize) {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getSize() >= minSize && document.getSize() <= maxSize) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbInternalUser() {
//		List<User> allUsers = userRepository.findAll();
//		int count=0;
//		
//		for (User user : allUsers) {
//			if (user.getAccountType().equals(AccountType.INTERNAL)) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbNotCipheredFiles(long minSize, long maxSize) {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (!document.getEncrypted() && document.getSize() >= minSize && document.getSize() <= maxSize) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbNotSharedFiles(long minSize, long maxSize) {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getSize() >= minSize && document.getSize() <= maxSize && !document.getShared()) {
//				count++;
//			}
//		}
//		
//		return count;
//	}
//
//	@Override
//	public int getNbSharedFiles(long minSize, long maxSize) {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getSize() >= minSize && document.getSize() <= maxSize && document.getShared()) {
//				count++;
//			}
//		}
//		
//		return count;
//	}
//
//	@Override
//	public int getNbUser() {
//		List<User> allUsers = userRepository.findAll();
//		return allUsers.size();
//	}
//
//	@Override
//	public int getNbCipheredFiles() {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getEncrypted()) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbFiles() {
//		List<Document> allDocs = documentRepository.findAll();
//		return allDocs.size();
//	}
//
//	@Override
//	public int getNbNotCipheredFiles() {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (!document.getEncrypted()) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	@Override
//	public int getNbNotSharedFiles() {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (!document.getShared()) {
//				count++;
//			}
//		}
//		
//		return count;
//	}
//
//	@Override
//	public int getNbSharedFiles() {
//		List<Document> allDocs = documentRepository.findAll();
//		int count = 0;
//		
//		for (Document document : allDocs) {
//			if (document.getShared()) {
//				count++;
//			}
//		}
//		
//		return count;
//	}
//
}
