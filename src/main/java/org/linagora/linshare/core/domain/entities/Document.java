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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.utils.DocumentUtils;

public class Document implements Serializable {

	private static final long serialVersionUID = 2877902686906612071L;

	private long id;

	/**
	 * the identifier of the document.
	 */
	private String uuid;

	/**
	 * the creation date of the document.
	 */
	private Calendar creationDate;

	/**
	 * the document mime type.
	 */
	private String type;

	/**
	 * technical field, used by detection mime type batch.
	 */
	private Boolean checkMimeType;

	/**
	 * the document file size
	 */
	private Long size;

	private Set<Signature> signatures = new HashSet<Signature>();

	/**
	 * UUID of the thumbnail file
	 */
	private String thmbUuid;

	/**
	 * Use by LinShare v2 as container/bucket
	 */
	private String bucketUuid;

	/**
	 * timsStampresponse encoded (der)
	 */
	private byte[] timeStamp;

	private Set<DocumentEntry> documentEntries;

	private Set<ThreadEntry> threadEntries;

	protected String sha1sum;

	protected String sha256sum;

	protected Boolean toUpgrade;

	/* Constructor for tests */
	public Document(String uuid, String name, String type, Calendar creationDate,
			Calendar expirationDate, User owner, Boolean encrypted,
			Boolean shared,Long size) {
		super();
		this.uuid=uuid;
		this.creationDate = creationDate;
		this.type = type;
		this.size = size;
		this.timeStamp = null;
		this.thmbUuid = null;
		this.checkMimeType = false;
		this.toUpgrade = false;
	}

	/**
	 * modifying from protected to public for using BeanUtils without construct 
	 * a document with null in parameters
	 */
	public Document(){
		super();
	}

	public Document(FileMetaData metadata) {
		super();
		this.uuid = metadata.getUuid();
		this.type = metadata.getMimeType();
		this.creationDate = new GregorianCalendar();
		this.size = metadata.getSize();
		this.timeStamp = null;
		this.thmbUuid = null;
		this.checkMimeType = false;
		this.bucketUuid = metadata.getBucketUuid();
		this.toUpgrade = false;
	}

	@Deprecated
	public Document(String uuid, String type, Long size) {
		super();
		this.uuid=uuid;
		this.type = type;
		this.creationDate = new GregorianCalendar();
		this.size = size;
		this.timeStamp = null;
		this.thmbUuid = null;
		this.checkMimeType = false;
		this.toUpgrade = false;
	}

	@Override
	public boolean equals(Object o1){
		if(o1 instanceof Document){
			return this.uuid.equals(((Document)o1).uuid);
		}else{
			return false;
		}
	}

	public String getHumanReadableSize(boolean si) {
		return DocumentUtils.humanReadableByteCount(this.getSize(), si, null);
	}

	@Override
	public int hashCode(){
		return this.uuid.hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if(null == id) this.id = 0;
		else this.id = id;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public Set<Signature> getSignatures() {
		return signatures;
	}

	public void setSignatures(Set<Signature> signatures) {
		this.signatures = signatures;
	}

	public void setThmbUuid(String thmbUUID) {
		this.thmbUuid = thmbUUID;
	}
	
	public String getThmbUuid() {
		return thmbUuid;
	}
	
	public byte[] getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(byte[] timeStamp) {
		if(timeStamp!=null) {
			this.timeStamp = Arrays.copyOf(timeStamp,timeStamp.length);
		}
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Set<DocumentEntry> getDocumentEntries() {
		return documentEntries;
	}

	public void setDocumentEntries(Set<DocumentEntry> documentEntries) {
		this.documentEntries = documentEntries;
	}

	public Set<ThreadEntry> getThreadEntries() {
		return threadEntries;
	}

	public void setThreadEntries(Set<ThreadEntry> threadEntries) {
		this.threadEntries = threadEntries;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long fileSize) {
		this.size = fileSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getCheckMimeType() {
		return checkMimeType;
	}

	public void setCheckMimeType(Boolean checkMimeType) {
		this.checkMimeType = checkMimeType;
	}

	public String getSha1sum() {
		return sha1sum;
	}

	public void setSha1sum(String sha1sum) {
		this.sha1sum = sha1sum;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public String getBucketUuid() {
		return bucketUuid;
	}

	public void setBucketUuid(String bucketUuid) {
		this.bucketUuid = bucketUuid;
	}

	@Override
	public String toString() {
		return "Document [uuid=" + uuid + ", creationDate=" + creationDate.getTimeInMillis()
				+ ", type=" + type + ", size=" + size + ", sha256sum="
				+ sha256sum + "]";
	}

	public String getRepresentation() {
		return "Document [uuid=" + uuid
				+ ", type=" + type + ", size=" + size + ", sha256sum="
				+ sha256sum + "]";
	}

	public Boolean getToUpgrade() {
		return toUpgrade;
	}

	public void setToUpgrade(Boolean toUpgrade) {
		this.toUpgrade = toUpgrade;
	}
}
