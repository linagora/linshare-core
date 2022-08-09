/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public class StatisticServiceUtils {

	public StatisticServiceUtils() {
	}

	public static String getHumanMimeType(String mimeType) {
		if (mimeType.matches("^audio.*")
				|| mimeType.equals("application/ogg")
				) {
			return "audio";
		}
		if (mimeType.matches("^video.*")
				|| mimeType.matches(".*mp4.*")
				|| mimeType.matches(".*mpeg.*")
				|| mimeType.equals("application/x-matroska")
				|| mimeType.equals("application/quicktime")
				) {
			return "video";
		}
		if (mimeType.matches("^text.*")) {
			return "text";
		}
		if (mimeType.matches("^image.*")) {
			return "image";
		}
		if (mimeType.equals("application/pdf")
				|| mimeType.equals("application/vnd.cups-pdf")
				|| mimeType.equals("application/vnd.sealedmedia.softseal.pdf")
				) {
			return "pdf";
		}
		if (mimeType.equals("application/zip")
				|| mimeType.equals("application/gzip")
				|| mimeType.equals("application/bzip")
				|| mimeType.equals("application/bzip2")
				|| mimeType.equals("application/x-7z-compressed")
				|| mimeType.equals("application/x-gtar")
				|| mimeType.equals("application/x-lz4")
				|| mimeType.equals("application/x-lzip")
				|| mimeType.equals("application/x-lzma")
				|| mimeType.equals("application/x-archive")
				|| mimeType.equals("application/x-tar")
				|| mimeType.equals("application/x-xz")
				|| mimeType.matches("^application/.*zip.*")
				|| mimeType.matches("^application/x-rar-compressed.*")
				) {
			return "archive";
		}
		if (mimeType.equals("application/encrypted")
				|| mimeType.equals("application/pgp-encrypted")
				|| mimeType.equals("application/x-axcrypt")
				) {
			return "encrypted";
		}
		if (mimeType.matches("^application/msword.*")
				|| mimeType.matches("application/vnd.ms-.*")
				|| mimeType.matches("application/vnd.openxmlformats-officedocument.*")
				|| mimeType.matches("application/vnd.oasis.opendocument.*")
				) {
			return "document";
		}
		return "others";
	}

	protected Pair<Date, Date> checkDatesInitialization(String beginDate, String endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			if (endDate == null) {
				Calendar endCalendar = new GregorianCalendar();
				endCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				endCalendar.set(Calendar.SECOND, 59);
				endCalendar.add(Calendar.SECOND, 1);
				eDate = endCalendar.getTime();
			} else {
				eDate = formatter.parse(endDate);
			}
			if (beginDate == null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(eDate);
				cal.add(Calendar.MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				bDate = cal.getTime();
			} else {
				bDate = formatter.parse(beginDate);
			}
		} catch (ParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, "Can not parse the dates.");
		}
		return new ImmutablePair<>(bDate, eDate);
	}
}
