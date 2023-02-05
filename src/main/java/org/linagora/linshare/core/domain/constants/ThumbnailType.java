/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.sis.util.NullArgumentException;
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