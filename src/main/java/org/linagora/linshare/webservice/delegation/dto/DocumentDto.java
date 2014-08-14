package org.linagora.linshare.webservice.delegation.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.webservice.dto.GenericUserDto;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/*
 * The objects document DTO and delegation document DTO has the same outside name.
 * JaxB does not allow this.
 * That's why we have to set the name space to Delegation.
 */
@XmlType(namespace="Delegation")
@XmlRootElement(name = "Document")
@ApiModel(value = "Document", description = "A Document")
public class DocumentDto extends org.linagora.linshare.webservice.dto.DocumentDto {

	@ApiModelProperty(value = "Owner")
	protected GenericUserDto owner;

	public DocumentDto() {
		super();
	}

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

	/*
	 * Transformers
	 */
	public static Function<DocumentEntry, DocumentDto> toDelegationVo() {
		return new Function<DocumentEntry, DocumentDto>() {
			@Override
			public DocumentDto apply(DocumentEntry arg0) {
				return new DocumentDto(arg0);
			}
		};
	}

}
