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

import javax.management.MXBean;

@MXBean
public interface LinshareStats {
	/**
	 * Get the number of internal users
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbInternalUser();
	/**
	 * Get the number of guest users
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbExternalUser();
	/**
	 * Get the total number of users
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbUser();
	/**
	 * Get the total number of files
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbFiles();
	/**
	 * Get the number of files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbFiles(int minSize, int maxSize);
	/**
	 * Get the number of shared files
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbSharedFiles();
	/**
	 * Get the number of shared files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbSharedFiles(int minSize, int maxSize);
	/**
	 * Get the number of not shared files
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbNotSharedFiles();
	/**
	 * Get the number of not shared files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbNotSharedFiles(int minSize, int maxSize);
	/**
	 * Get the number of ciphered files
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbCipheredFiles();
	/**
	 * Get the number of ciphered files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbCipheredFiles(int minSize, int maxSize);
	/**
	 * Get the number of not ciphered files
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbNotCipheredFiles();
	/**
	 * Get the number of not ciphered files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return an integer value in a IntStatistique object
	 */
	public IntStatistique getNbNotCipheredFiles(int minSize, int maxSize);
	/**
	 * Get the upload volume in bytes of the days between minDate and maxDate
	 * @param minDate in format dd/mm/yyyy
	 * @param maxDate in format dd/mm/yyyy
	 * @return a long value in a LongStatistique object
	 */
	public LongStatistique getUploadVolume(String minDate, String maxDate);
	/**
	 * Get the download volume in bytes of the days between minDate and maxDate
	 * @param minDate in format dd/mm/yyyy
	 * @param maxDate in format dd/mm/yyyy
	 * @return a long value in a LongStatistique object
	 */
	public LongStatistique getDownloadVolume(String minDate, String maxDate);
	/**
	 * Get today's upload volume in bytes
	 * @return a long value in a LongStatistique object
	 */
	public LongStatistique getTodaysUploadVolume();
	/**
	 * Get today's download volume in bytes
	 * @return a long value in a LongStatistique object
	 */
	public LongStatistique getTodaysDownloadVolume();
}
