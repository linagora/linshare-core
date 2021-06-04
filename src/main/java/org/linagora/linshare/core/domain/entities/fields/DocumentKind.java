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
			"application/vnd.openxmlformats-officedocument.spreadsheetml.template");

	private final List<String> values;

	DocumentKind(String... values) {
		this.values = Arrays.asList(values);
	}

	public List<String> getValues() {
		return values;
	}

}
