package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class UploadRequestDto {

	private String uuid;

	@ApiModelProperty(value = "Owner")
	private ContactDto owner;

	@ApiModelProperty(value = "Recipient")
	private ContactDto recipient;

	// could be null
	private Integer maxFileCount;

	// could be null
	private Long maxDepositSize;

	// could be null
	private Long maxFileSize;

	private Date activationDate;

	// could be null
	private Date expiryDate;

	private boolean canDeleteDocument;

	private boolean canClose;

	private String subject;

	// could be null
	private String body;

	private boolean isClosed;

	private Map<String, String> documents = Maps.newHashMap();

	private boolean protectedByPassword;

	private long usedSpace = 0;

	Set<String> mimeTypes = Sets.newHashSet();

	public UploadRequestDto() {
		super();
	}

	/**
	 * for tests only
	 */
	public UploadRequestDto(String uuid, Integer maxFileCount,
			Long maxDepositSize, Long maxFileSize, Date activationDate,
			Date expiryDate, boolean canDeleteDocument, boolean canClose,
			String subject, String body, boolean isClosed) {
		super();
		this.uuid = uuid;
		this.maxFileCount = maxFileCount;
		this.maxDepositSize = maxDepositSize;
		this.maxFileSize = maxFileSize;
		this.activationDate = activationDate;
		this.expiryDate = expiryDate;
		this.canDeleteDocument = canDeleteDocument;
		this.canClose = canClose;
		this.subject = subject;
		this.body = body;
		this.isClosed = isClosed;
		this.protectedByPassword = false;
	}

	public UploadRequestDto(UploadRequest entity) {
		super();
		this.uuid = entity.getUuid();
		this.owner = new ContactDto(entity.getOwner());
		this.recipient = null;
		this.maxFileCount = entity.getMaxFileCount();
		this.maxDepositSize = entity.getMaxDepositSize();
		this.maxFileSize = entity.getMaxFileSize();
		this.activationDate = entity.getActivationDate();
		this.expiryDate = entity.getExpiryDate();
		this.canDeleteDocument = entity.isCanDelete();
		this.canClose = entity.isCanClose();
		this.subject = entity.getUploadRequestGroup().getSubject();
		this.body = entity.getUploadRequestGroup().getBody();
		this.isClosed = false;
		if (entity.getStatus().equals(UploadRequestStatus.STATUS_CLOSED))
			this.isClosed = true;
		for (UploadRequestEntry entry : entity.getUploadRequestEntries()) {
			DocumentEntry documentEntry = entry.getDocumentEntry();
			if (documentEntry != null) {
				this.documents.put(documentEntry.getUuid(),
						documentEntry.getName());
				this.usedSpace += documentEntry.getSize();
			}
		}
		this.protectedByPassword = false;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactDto getOwner() {
		return owner;
	}

	public void setOwner(ContactDto owner) {
		this.owner = owner;
	}

	public ContactDto getRecipient() {
		return recipient;
	}

	public void setRecipient(ContactDto recipient) {
		this.recipient = recipient;
	}

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public Map<String, String> getDocuments() {
		return documents;
	}

	public void setDocuments(Map<String, String> documents) {
		this.documents = documents;
	}

	public boolean isCanDeleteDocument() {
		return canDeleteDocument;
	}

	public void setCanDeleteDocument(boolean canDeleteDocument) {
		this.canDeleteDocument = canDeleteDocument;
	}

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
	}

	public long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Set<String> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Set<String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	/**
	 * Helpers
	 */
	public void addMimeTypes(String mimeType) {
		this.mimeTypes.add(mimeType);
	}
}
