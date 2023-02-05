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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "MailContainer")
@Schema(name = "MailContainer", description = "")
public class MailContainerDto {

	protected String subject;

	protected String content;

	protected String language;

	protected String type;

	public MailContainerDto() {
		super();
	}

	public MailContainerDto(MailContentType type) {
		super();
		this.type = type.toString();
	}

	public MailContainerDto(String subject, String content, String language, String type) {
		super();
		this.subject = subject;
		this.content = content;
		this.language = language;
		this.type = type;
	}

	public MailContainerDto(MailContainerWithRecipient build, MailContentType type) {
		super();
		if (build != null) {
			this.subject = build.getSubject();
			this.content = build.getContent();
			this.language = build.getLanguage().toString();
			this.type = type.toString();
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
