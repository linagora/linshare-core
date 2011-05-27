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
package org.linagora.linShare.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.StatisticsService;
import org.linagora.linShare.view.tapestry.beans.LogCriteriaBean;

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

	public long getDownloadVolume(String minDate, String maxDate) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		
		Date dateMin = dateFormat.parse(minDate);
		Calendar calendarMin = new GregorianCalendar();
		calendarMin.setTime(dateMin);

		Date dateMax = dateFormat.parse(maxDate);
		Calendar calendarMax = new GregorianCalendar();
		calendarMax.setTime(dateMax);

		return getDownloadVolume(calendarMin, calendarMax);
	}

	public long getUploadVolume(String minDate, String maxDate) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		
		Date dateMin = dateFormat.parse(minDate);
		Calendar calendarMin = new GregorianCalendar();
		calendarMin.setTime(dateMin);
		
		Date dateMax = dateFormat.parse(maxDate);
		Calendar calendarMax = new GregorianCalendar();
		calendarMax.setTime(dateMax);
		
		return getUploadVolume(calendarMin, calendarMax);
	}
	
	public long getDownloadVolume(Calendar minDate, Calendar maxDate) {
		long volume = 0;
		LogCriteriaBean logCriteria = new LogCriteriaBean();
		logCriteria.setAfterDate(maxDate);
		logCriteria.setBeforeDate(minDate);
		
		List<LogEntry> logs = logEntryRepository.findByCriteria(logCriteria, null);
		
		for (LogEntry logEntry : logs) {
			LogAction action = logEntry.getLogAction();
			if (action.equals(LogAction.ANONYMOUS_SHARE_DOWNLOAD)
					|| action.equals(LogAction.SHARE_DOWNLOAD)) {
				FileLogEntry fileLogEntry = (FileLogEntry)logEntry;
				Long fileSize = fileLogEntry.getFileSize();
				volume+=fileSize.longValue();
			}
		}
		return volume;
	}

	public long getUploadVolume(Calendar minDate, Calendar maxDate) {
		long volume = 0;
		LogCriteriaBean logCriteria = new LogCriteriaBean();
		logCriteria.setAfterDate(maxDate);
		logCriteria.setBeforeDate(minDate);
		
		List<LogEntry> logs = logEntryRepository.findByCriteria(logCriteria, null);
		
		for (LogEntry logEntry : logs) {
			LogAction action = logEntry.getLogAction();
			if (action.equals(LogAction.FILE_UPLOAD)) {
				FileLogEntry fileLogEntry = (FileLogEntry)logEntry;
				Long fileSize = fileLogEntry.getFileSize();
				volume+=fileSize.longValue();
			}
		}
		return volume;
	}

	public int getNbCipheredFiles(long minSize, long maxSize) {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getEncrypted() && document.getSize() >= minSize && document.getSize() <= maxSize) {
				count++;
			}
		}
		return count;
	}

	public int getNbExternalUser() {
		List<User> allUsers = userRepository.findAll();
		int count=0;
		
		for (User user : allUsers) {
			if (user.getUserType().equals(UserType.GUEST)) {
				count++;
			}
		}
		return count;
	}

	public int getNbFiles(long minSize, long maxSize) {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getSize() >= minSize && document.getSize() <= maxSize) {
				count++;
			}
		}
		return count;
	}

	public int getNbInternalUser() {
		List<User> allUsers = userRepository.findAll();
		int count=0;
		
		for (User user : allUsers) {
			if (user.getUserType().equals(UserType.INTERNAL)) {
				count++;
			}
		}
		return count;
	}

	public int getNbNotCipheredFiles(long minSize, long maxSize) {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (!document.getEncrypted() && document.getSize() >= minSize && document.getSize() <= maxSize) {
				count++;
			}
		}
		return count;
	}

	public int getNbNotSharedFiles(long minSize, long maxSize) {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getSize() >= minSize && document.getSize() <= maxSize && !document.getShared()) {
				count++;
			}
		}
		
		return count;
	}

	public int getNbSharedFiles(long minSize, long maxSize) {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getSize() >= minSize && document.getSize() <= maxSize && document.getShared()) {
				count++;
			}
		}
		
		return count;
	}

	public int getNbUser() {
		List<User> allUsers = userRepository.findAll();
		return allUsers.size();
	}

	public int getNbCipheredFiles() {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getEncrypted()) {
				count++;
			}
		}
		return count;
	}

	public int getNbFiles() {
		List<Document> allDocs = documentRepository.findAll();
		return allDocs.size();
	}

	public int getNbNotCipheredFiles() {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (!document.getEncrypted()) {
				count++;
			}
		}
		return count;
	}

	public int getNbNotSharedFiles() {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (!document.getShared()) {
				count++;
			}
		}
		
		return count;
	}

	public int getNbSharedFiles() {
		List<Document> allDocs = documentRepository.findAll();
		int count = 0;
		
		for (Document document : allDocs) {
			if (document.getShared()) {
				count++;
			}
		}
		
		return count;
	}

}
