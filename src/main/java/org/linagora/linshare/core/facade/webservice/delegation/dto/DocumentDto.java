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
package org.linagora.linshare.core.facade.webservice.delegation.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

/*
 * The objects document DTO and delegation document DTO has the same outside name.
 * JaxB does not allow this.
 * That's why we have to set the name space to Delegation.
 */
@XmlType(namespace = "Delegation")
@XmlRootElement(name = "Document")
@Schema(name = "Document", description = "A Document")
public class DocumentDto extends
		org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto {

	@Schema(description = "Owner")
	protected GenericUserDto owner;

	public DocumentDto() {
		super();
	}

	public DocumentDto(AsyncTaskDto asyncTask,
			DocumentTaskContext documentTaskContext) {
		super(asyncTask, documentTaskContext);
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
