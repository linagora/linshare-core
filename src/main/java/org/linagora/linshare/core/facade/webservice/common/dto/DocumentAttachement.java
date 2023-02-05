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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "documentAttachement", namespace = "http://org/linagora/linshare/webservice/jaxrs")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) //default
@XmlType(name = "DocumentAttachement")
public class DocumentAttachement {

	private DataHandler documentDataHandler;

	private String filename;

	private String comment;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@XmlMimeType("application/octet-stream")
	public DataHandler getDocument() {
		return documentDataHandler;
	}
	public void setDocument(DataHandler documentDataHandler) {
		this.documentDataHandler = documentDataHandler;
	}

	@Override
	public String toString() {
		return "DocumentAttachement [documentDataHandler="
				+ documentDataHandler + ", filename=" + filename + ", comment="
				+ comment + "]";
	}

}
