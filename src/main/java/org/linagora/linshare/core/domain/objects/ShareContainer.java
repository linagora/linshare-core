package org.linagora.linshare.core.domain.objects;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.webservice.dto.DocumentDto;

import com.google.common.collect.Sets;

public class ShareContainer {

	protected String subject;

	protected String message;

	protected String locale;

	protected Boolean secured;

	protected String inReplyTo;

	protected String references;

	protected Date expiryDate;

	/**
	 * True if at least one document is encrypted. It will be used by
	 * notification service to add a link towards the Applet used to decrypt the
	 * document.
	 */
	protected boolean encrypted;

	protected Set<String> externalRecipients = Sets.newHashSet();

	protected Set<User> internalRecipients = Sets.newHashSet();

	protected Set<DocumentEntry> documents = Sets.newHashSet();

	public ShareContainer(String subject, String message, Boolean secured) {
		super();
		this.subject = subject;
		this.message = message;
		this.locale = "en";
		this.secured = secured;
		this.encrypted = false;
	}

	public ShareContainer() {
		super();
		this.encrypted = false;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Boolean getSecured() {
		return secured;
	}

	public void setSecured(Boolean secured) {
		this.secured = secured;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public Set<String> getExternalRecipients() {
		return externalRecipients;
	}

	public void setExternalRecipients(Set<String> externalRecipients) {
		this.externalRecipients = externalRecipients;
	}

	public Set<User> getInternalRecipients() {
		return internalRecipients;
	}

	public void setInternalRecipients(Set<User> internalRecipients) {
		this.internalRecipients = internalRecipients;
	}

	public Set<DocumentEntry> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<DocumentEntry> documents) {
		this.documents = documents;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void addDocumentsVo(List<DocumentVo> documentVos) {
		Validate.notNull(documentVos, "documentVos list must not be null.");
		for (DocumentVo documentVo : documentVos) {
			DocumentEntry d = new DocumentEntry();
			d.setUuid(documentVo.getIdentifier());
			this.documents.add(d);

		}
	}

	public void addDocumentsDto(Set<DocumentDto> documentsDto) {
		Validate.notNull(documentsDto, "documentsDto list must not be null.");
		for (DocumentDto documentDto : documentsDto) {
			DocumentEntry d = new DocumentEntry();
			d.setUuid(documentDto.getUuid());
			this.documents.add(d);
		}
	}

	public void addRecipient(List<String> recipientsEmail) {
		Validate.notNull(recipientsEmail, "mails " + "list must not be null.");
		for (String mail : recipientsEmail) {
			externalRecipients.add(mail);
		}
	}
	
	public void updateEncryptedStatus() {
		this.encrypted = false;
		for (DocumentEntry d : this.documents) {
			if (d.getCiphered()) {
				this.encrypted = true;
				break;
			}
		}
	}
}
