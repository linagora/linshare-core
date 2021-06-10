/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.domain.entities.fields;

import java.util.Arrays;
import java.util.List;

/**
 * Provide a list of document kinds and related mimeTypes (supported by LinShare). Used as filters in LinShare API's dealing
 * with documents
 *
 */
public enum DocumentKind {

	PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.slideshow",
			"application/vnd.openxmlformats-officedocument.presentationml.template",
			"application/vnd.oasis.opendocument.presentation-template",
			"application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/vnd.openxmlformats-officedocument.presentationml.slide",
			"application/vnd.oasis.opendocument.flat.presentation", "application/x-corelpresentations",
			"application/vnd.oasis.opendocument.presentation",
			"application/vnd.ms-powerpoint.presentation.macroenabled.12"),

	PDF("application/pdf", "application/vnd.cups-pdf", "application/vnd.sealedmedia.softseal.pdf"),

	IMAGE("image/png", "image/vnd.dwg", "image/x-ms-bmp", "image/x-icon", "image/vnd.wap.wbmp", "image/x-xcf",
			"image/vnd.adobe.photoshop", "image/svg+xml", "image/tiff", "image/webp", "image/gif",
			"image/vnd.microsoft.icon", "image/jpeg", "image/bmp"),

	VIDEO("video/3gpp2", "video/mp4", "video/quicktime", "video/x-m4v", "video/3gpp"),

	AUDIO("audio/mpeg", "audio/mp4", "audio/x-wav", "audio/x-aiff", "audio/basic", "application/x-midi", "audio/midi",
			"audio/x-oggpcm", "audio/ogg", "audio/opus", "audio/speex", "audio/vorbis"),

	DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.oasis.opendocument.text", "application/x-vnd.oasis.opendocument.text", "text/csv",
			"text/plain", "application/msword"),

	ARCHIVE("application/x-tar", "application/x-tika-unix-dump", "application/java-archive",
			"application/x-7z-compressed", "application/x-archive", "application/x-cpio", "application/zip"),

	SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet",
			"application/vnd.oasis.opendocument.flat.spreadsheet",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/vnd.ms-spreadsheetml",
			"application/vnd.oasis.opendocument.spreadsheet-template",
			"application/x-tika-msworks-spreadsheet",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.template"),
	OTHER;

	private final List<String> values;

	DocumentKind(String... values) {
		this.values = Arrays.asList(values);
	}

	public List<String> getValues() {
		return values;
	}

}
