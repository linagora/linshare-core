package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ShareEntry;


@XmlRootElement(name = "Share")
public class ShareDto extends EntryDto {

	private Boolean ciphered;
	
	private String type;
	
	private Long size;
	
	private Long downloaded;
	
	private String receiver;
	

	public ShareDto(ShareEntry de) {
		super(de);
		this.ciphered = de.getDocumentEntry().getCiphered();
		this.type = de.getDocumentEntry().getDocument().getType();
		this.size = de.getDocumentEntry().getDocument().getSize();
		this.downloaded = de.getDownloaded();
		this.receiver = de.getRecipient().getLsUuid();
	}
	
	public ShareDto() {
		super();
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}
