/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

public class CopyResource {

	protected TargetKind kind;

	protected String resourceUuid;

	protected String name;

	protected String documentUuid;

	protected String contextUuid;

	protected String contextName;

	protected Long size;

	protected Boolean ciphered;

	protected String comment;

	protected String metaData;

	protected CopyMto copyFrom;

	public CopyResource(TargetKind kind, ShareEntry entry) {
		super();
		this.kind = kind;
		this.resourceUuid = entry.getUuid();
		this.size = entry.getSize();
		this.documentUuid = entry.getDocumentEntry().getDocument().getUuid();
		this.ciphered = entry.getDocumentEntry().getCiphered();
		this.name = entry.getName();
		this.comment = entry.getComment();
		this.metaData = entry.getMetaData();
		this.copyFrom = new CopyMto(entry);
	}

	public CopyResource(TargetKind kind, DocumentEntry entry) {
		super();
		this.kind = kind;
		this.resourceUuid = entry.getUuid();
		this.size = entry.getSize();
		this.documentUuid = entry.getDocument().getUuid();
		this.ciphered = entry.getCiphered();
		this.name = entry.getName();
		this.comment = entry.getComment();
		this.metaData = entry.getMetaData();
		this.copyFrom = new CopyMto(entry);
	}

	public CopyResource(TargetKind kind, Thread workGroup, WorkGroupDocument entry) {
		super();
		this.kind = kind;
		this.resourceUuid = entry.getUuid();
		this.size = entry.getSize();
		this.documentUuid = entry.getDocumentUuid();
		this.ciphered = entry.getCiphered();
		this.name = entry.getName();
		this.metaData = entry.getMetaData();
		// there is no need to recipients to know the name of the source workgroup.
		this.copyFrom = new CopyMto(entry, workGroup, false);
	}

	public TargetKind getKind() {
		return kind;
	}

	public void setKind(TargetKind kind) {
		this.kind = kind;
	}

	public String getResourceUuid() {
		return resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getContextUuid() {
		return contextUuid;
	}

	public void setContextUuid(String contextUuid) {
		this.contextUuid = contextUuid;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CopyMto getCopyFrom() {
		return copyFrom;
	}

	public void setCopyFrom(CopyMto copyFrom) {
		this.copyFrom = copyFrom;
	}

}
