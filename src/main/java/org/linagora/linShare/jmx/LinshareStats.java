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
	public IntStatistique getNbInternalUser();
	public IntStatistique getNbExternalUser();
	public IntStatistique getNbUser();
	public IntStatistique getNbFiles();
	public IntStatistique getNbFiles(int minSize, int maxSize);
	public IntStatistique getNbSharedFiles();
	public IntStatistique getNbSharedFiles(int minSize, int maxSize);
	public IntStatistique getNbNotSharedFiles();
	public IntStatistique getNbNotSharedFiles(int minSize, int maxSize);
	public IntStatistique getNbCipheredFiles();
	public IntStatistique getNbCipheredFiles(int minSize, int maxSize);
	public IntStatistique getNbNotCipheredFiles();
	public IntStatistique getNbNotCipheredFiles(int minSize, int maxSize);
	public LongStatistique getUploadVolume(String minDate, String maxDate);
	public LongStatistique getDownloadVolume(String minDate, String maxDate);
	public LongStatistique getTodaysUploadVolume();
	public LongStatistique getTodaysDownloadVolume();
}
