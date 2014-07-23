package org.linagora.linshare.webservice.delegation.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.webservice.dto.GenericUserDto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Document")
@ApiModel(value = "Document", description = "A Document")
public class DocumentDto extends org.linagora.linshare.webservice.dto.DocumentDto {

	@ApiModelProperty(value = "Owner")
	protected GenericUserDto owner;

	public DocumentDto(DocumentEntry de) {
		super(de);
		this.owner = new GenericUserDto((User) de.getEntryOwner());
	}

	public GenericUserDto getOwner() {
		return owner;
	}

	public void setOwner(GenericUserDto owner) {
		this.owner = owner;
	}
}
