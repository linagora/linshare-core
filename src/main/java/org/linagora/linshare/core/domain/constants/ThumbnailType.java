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

package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.apache.sis.util.NullArgumentException;
import org.linagora.LinThumbnail.utils.ThumbnailKind;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum ThumbnailType {

	SMALL, MEDIUM, LARGE, PDF;

	public static FileMetaDataKind toFileMetaDataKind(ThumbnailType thumbnailEKind) {
		try {
			return FileMetaDataKind.valueOf("THUMBNAIL_" + thumbnailEKind.name());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing Thumbnailkind");
		}
	}

	public static String getThmbUuid(FileMetaDataKind dataKind, Document doc) {
		try {
			if (getThumbnailType(dataKind) != null) {
				Thumbnail thumbnail = doc.getThumbnails().get(getThumbnailType(dataKind));
				if (thumbnail != null) {
					return thumbnail.getThumbnailUuid();
				}
			}
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing Thumbnailkind");
		}
		throw new NullArgumentException("there is not thumbnailUuid match");
	}

	public static ThumbnailType toThumbnailType(ThumbnailKind key) {
		try {
			return ThumbnailType.valueOf(key.toString());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing ThumbnailType");
		}
	}

	public static ThumbnailType fromString(String s) {
		try {
			return ThumbnailType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.DATABASE_INCOHERENCE, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}

	public static ThumbnailType getThumbnailType(FileMetaDataKind kind) {
		if (kind.toString().contains("THUMBNAIL_")) {
			return ThumbnailType.valueOf(kind.toString().split("THUMBNAIL_")[1]);
		}
		return null;
	}

	public static ThumbnailType getThumbnailType(String fileName) {
		try {
			if (fileName.contains(".png")) {
				return ThumbnailType.valueOf(fileName.split(".png")[0]);
			} else if(fileName.contains(".pdf")) {
				return ThumbnailType.valueOf(fileName.split(".pdf")[0]);
			}
			return null;
		} catch (RuntimeException re){
			throw new IllegalArgumentException("Doesn't match an existing ThumbnailType");
		}
	}

	public static String getFileType(ThumbnailType kind) {
		if (ThumbnailType.PDF.equals(kind)) {
			return ".pdf";
		}
		return ".png";
	}

	public static String getFileMimeType(ThumbnailType kind) {
		if (ThumbnailType.PDF.equals(kind)) {
			return "application/pdf";
		}
		return "image/png";
	}

}