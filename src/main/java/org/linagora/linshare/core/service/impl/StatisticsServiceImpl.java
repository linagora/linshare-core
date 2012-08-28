/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
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
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbExternalUser() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbUser() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbFiles(long minSize, long maxSize) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbSharedFiles(long minSize, long maxSize) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbNotSharedFiles(long minSize, long maxSize) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbCipheredFiles(long minSize, long maxSize) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbNotCipheredFiles(long minSize, long maxSize) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbFiles() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbSharedFiles() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbNotSharedFiles() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbCipheredFiles() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNbNotCipheredFiles() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getUploadVolume(Calendar minDate, Calendar maxDate) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getDownloadVolume(Calendar minDate, Calendar maxDate) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getUploadVolume(String minDate, String maxDate) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getDownloadVolume(String minDate, String maxDate) throws Exception {
		// TODO Auto-generated method stub
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
