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
package org.linagora.linshare.jmx;

import java.util.Calendar;

import org.linagora.linshare.core.service.StatisticsService;

public class LinshareStatsImpl implements LinshareStats {
	private final StatisticsService statsService;
	
	public LinshareStatsImpl(StatisticsService statsService) {
		super();
		this.statsService = statsService;
	}

	public LongStatistique getDownloadVolume(String minDate, String maxDate) {
		long stat=0;
		try {
			stat = statsService.getDownloadVolume(minDate, maxDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new LongStatistique("DOWNLOAD_VOLUME", stat);
	}

	public LongStatistique getUploadVolume(String minDate, String maxDate) {
		long stat=0;
		try {
			stat = statsService.getUploadVolume(minDate, maxDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new LongStatistique("UPLOAD_VOLUME", stat);
	}

	public LongStatistique getTodaysDownloadVolume() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		long stat = statsService.getDownloadVolume(yesterday, tomorrow);
		return new LongStatistique("DOWNLOAD_VOLUME", stat);
	}

	public LongStatistique getTodaysUploadVolume() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		long stat = statsService.getUploadVolume(yesterday, tomorrow);
		return new LongStatistique("UPLOAD_VOLUME", stat);
	}

	public IntStatistique getNbCipheredFiles() {
		int stat = statsService.getNbCipheredFiles();
		return new IntStatistique("NB_CIPHERED_FILES", stat);
	}

	public IntStatistique getNbCipheredFiles(int minSize, int maxSize) {
		int stat = statsService.getNbCipheredFiles(minSize, maxSize);
		return new IntStatistique("NB_CIPHERED_FILES", stat);
	}

	public IntStatistique getNbExternalUser() {
		int stat = statsService.getNbExternalUser();
		return new IntStatistique("NB_EXTERNAL_USER", stat);
	}

	public IntStatistique getNbFiles() {
		int stat = statsService.getNbFiles();
		return new IntStatistique("NB_FILES", stat);
	}

	public IntStatistique getNbFiles(int minSize, int maxSize) {
		int stat = statsService.getNbFiles(minSize, maxSize);
		return new IntStatistique("NB_FILES", stat);
	}

	public IntStatistique getNbInternalUser() {
		int stat = statsService.getNbInternalUser();
		return new IntStatistique("NB_INTERNAL_USER", stat);
	}

	public IntStatistique getNbNotCipheredFiles() {
		int stat = statsService.getNbNotCipheredFiles();
		return new IntStatistique("NB_NOT_CIPHERED_FILES", stat);
	}

	public IntStatistique getNbNotCipheredFiles(int minSize, int maxSize) {
		int stat = statsService.getNbNotCipheredFiles(minSize, maxSize);
		return new IntStatistique("NB_NOT_CIPHERED_FILES", stat);
	}

	public IntStatistique getNbNotSharedFiles() {
		int stat = statsService.getNbNotSharedFiles();
		return new IntStatistique("NB_NOT_SHARED_FILES", stat);
	}

	public IntStatistique getNbNotSharedFiles(int minSize, int maxSize) {
		int stat = statsService.getNbNotSharedFiles(minSize, maxSize);
		return new IntStatistique("NB_NOT_SHARED_FILES", stat);
	}

	public IntStatistique getNbSharedFiles() {
		int stat = statsService.getNbSharedFiles();
		return new IntStatistique("NB_SHARED_FILES", stat);
	}

	public IntStatistique getNbSharedFiles(int minSize, int maxSize) {
		int stat = statsService.getNbSharedFiles(minSize, maxSize);
		return new IntStatistique("NB_SHARED_FILES", stat);
	}

	public IntStatistique getNbUser() {
		int stat = statsService.getNbUser();
		return new IntStatistique("NB_USER", stat);
	}

}
