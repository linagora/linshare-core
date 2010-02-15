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
package org.linagora.linShare.jmx;

import javax.management.MXBean;

@MXBean
public interface LinshareStats {
	/**
	 * Get the number of internal users
	 * @return
	 */
	public IntStatistique getNbInternalUser();
	/**
	 * Get the number of guest users
	 * @return
	 */
	public IntStatistique getNbExternalUser();
	/**
	 * Get the total number of users
	 * @return
	 */
	public IntStatistique getNbUser();
	/**
	 * Get the total number of files
	 * @return
	 */
	public IntStatistique getNbFiles();
	/**
	 * Get the number of files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return
	 */
	public IntStatistique getNbFiles(int minSize, int maxSize);
	/**
	 * Get the number of shared files
	 * @return
	 */
	public IntStatistique getNbSharedFiles();
	/**
	 * Get the number of shared files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return
	 */
	public IntStatistique getNbSharedFiles(int minSize, int maxSize);
	/**
	 * Get the number of not shared files
	 * @return
	 */
	public IntStatistique getNbNotSharedFiles();
	/**
	 * Get the number of not shared files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return
	 */
	public IntStatistique getNbNotSharedFiles(int minSize, int maxSize);
	/**
	 * Get the number of ciphered files
	 * @return
	 */
	public IntStatistique getNbCipheredFiles();
	/**
	 * Get the number of ciphered files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return
	 */
	public IntStatistique getNbCipheredFiles(int minSize, int maxSize);
	/**
	 * Get the number of not ciphered files
	 * @return
	 */
	public IntStatistique getNbNotCipheredFiles();
	/**
	 * Get the number of not ciphered files of size between minSize and maxSize.
	 * @param minSize minimum file size in bytes
	 * @param maxSize maximum file size in bytes
	 * @return
	 */
	public IntStatistique getNbNotCipheredFiles(int minSize, int maxSize);
	/**
	 * Get the upload volume in bytes of the days between minDate and maxDate
	 * @param minDate in format dd/mm/yyyy
	 * @param maxDate in format dd/mm/yyyy
	 * @return
	 */
	public LongStatistique getUploadVolume(String minDate, String maxDate);
	/**
	 * Get the download volume in bytes of the days between minDate and maxDate
	 * @param minDate in format dd/mm/yyyy
	 * @param maxDate in format dd/mm/yyyy
	 * @return
	 */
	public LongStatistique getDownloadVolume(String minDate, String maxDate);
	/**
	 * Get today's upload volume in bytes
	 * @return
	 */
	public LongStatistique getTodaysUploadVolume();
	/**
	 * Get today's download volume in bytes
	 * @return
	 */
	public LongStatistique getTodaysDownloadVolume();
}
