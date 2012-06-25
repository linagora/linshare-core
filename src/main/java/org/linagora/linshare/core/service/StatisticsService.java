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
package org.linagora.linshare.core.service;

import java.util.Calendar;


public interface StatisticsService {
	public int getNbInternalUser();
	public int getNbExternalUser();
	public int getNbUser();
	public int getNbFiles(long minSize, long maxSize);
	public int getNbSharedFiles(long minSize, long maxSize);
	public int getNbNotSharedFiles(long minSize, long maxSize);
	public int getNbCipheredFiles(long minSize, long maxSize);
	public int getNbNotCipheredFiles(long minSize, long maxSize);
	public int getNbFiles();
	public int getNbSharedFiles();
	public int getNbNotSharedFiles();
	public int getNbCipheredFiles();
	public int getNbNotCipheredFiles();
	public long getUploadVolume(Calendar minDate, Calendar maxDate);
	public long getDownloadVolume(Calendar minDate, Calendar maxDate);
	public long getUploadVolume(String minDate, String maxDate) throws Exception;
	public long getDownloadVolume(String minDate, String maxDate) throws Exception;
}
