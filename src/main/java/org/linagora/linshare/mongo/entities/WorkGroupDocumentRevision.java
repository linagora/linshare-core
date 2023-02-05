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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.utils.DocumentUtils;

@XmlRootElement(name = "SharedSpaceDocumentRevision")
public class WorkGroupDocumentRevision extends WorkGroupDocument {

	public WorkGroupDocumentRevision() {
		super();
		this.nodeType = WorkGroupNodeType.DOCUMENT_REVISION;
	}

	public WorkGroupDocumentRevision(Account author, String name, Document document, WorkGroup workGroup,
			WorkGroupNode nodeParent) {
		super(author, name, document, workGroup, nodeParent);
		this.size = document.getSize();
		this.mimeType = document.getType();
		this.humanMimeType = DocumentUtils.getHumanMimeType(mimeType);
		this.documentUuid = document.getUuid();
		this.uploadDate = new Date();
		this.sha256sum = document.getSha256sum();
		this.hasThumbnail = document.getHasThumbnail();
		this.nodeType = WorkGroupNodeType.DOCUMENT_REVISION;
	}

	@Override
	public String toString() {
		return "WorkGroupDocumentRevision -> [ " + super.toString() + "]";
	}
}
