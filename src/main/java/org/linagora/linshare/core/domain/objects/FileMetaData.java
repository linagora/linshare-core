package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;

/**
 * @author fred
 *
 */
public class FileMetaData {

	private String uuid;

	private final FileMetaDataKind kind;

	private final String mimeType;

	private String bucketUuid;

	private final Long size;

	// optional meta data
	private String fileName;

	public FileMetaData(FileMetaDataKind kind, String mimeType, Long size, String fileName) {
		super();
		this.uuid = null;
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = size;
		this.fileName = fileName;
		this.bucketUuid = null;
	}

	public FileMetaData(FileMetaDataKind kind, String mimeType, Long size) {
		super();
		this.uuid = null;
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = size;
		this.bucketUuid = null;
	}

	public FileMetaData(FileMetaDataKind kind, Document document) {
		super();
		this.uuid = document.getUuid();
		if (kind.equals(FileMetaDataKind.THUMBNAIL)) {
			this.uuid = document.getThmbUuid();
		}
		this.kind = kind;
		this.mimeType = document.getType();
		this.size = document.getSize();
		this.bucketUuid = document.getBucketUuid();
	}

	public FileMetaData(FileMetaDataKind kind, Document document, String mimeType) {
		super();
		this.uuid = document.getUuid();
		if (kind.equals(FileMetaDataKind.THUMBNAIL)) {
			this.uuid = document.getThmbUuid();
		}
		this.kind = kind;
		this.mimeType = mimeType;
		this.size = null;
		this.bucketUuid = document.getBucketUuid();
	}

	public FileMetaData(Signature signature) {
		super();
		this.uuid = null;
		this.kind = FileMetaDataKind.SIGNATURE;
		this.bucketUuid = signature.getDocument().getBucketUuid();
		this.size = signature.getSize();
		this.mimeType = signature.getType();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public FileMetaDataKind getKind() {
		return kind;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getBucketUuid() {
		return bucketUuid;
	}

	public void setBucketUuid(String bucketUuid) {
		this.bucketUuid = bucketUuid;
	}

	public Long getSize() {
		return size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "FileMetaData [uuid=" + uuid + ", fileName=" + fileName + ", kind=" + kind + ", mimeType=" + mimeType
				+ ", bucketUuid=" + bucketUuid + ", size=" + size + "]";
	}
}
