/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;

public class AnonymousShareEntryMto extends EntryMto {

	protected Long dowloaded;

	protected DocumentMto documentEntry;

	protected AnonymousUrlMto url;

	public AnonymousShareEntryMto() {
		super();
	}

	public AnonymousShareEntryMto(AnonymousShareEntry entry) {
		super(entry, false);
	}

	public AnonymousShareEntryMto(AnonymousShareEntry entry, boolean withOwner) {
		super(entry, withOwner);
		this.dowloaded = entry.getDownloaded();
		this.url = new AnonymousUrlMto(entry.getAnonymousUrl());
		this.documentEntry = new DocumentMto(entry.getDocumentEntry());
	}

	public Long getDowloaded() {
		return dowloaded;
	}

	public void setDowloaded(Long dowloaded) {
		this.dowloaded = dowloaded;
	}

	public DocumentMto getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentMto documentEntry) {
		this.documentEntry = documentEntry;
	}

	public AnonymousUrlMto getUrl() {
		return url;
	}

	public void setUrl(AnonymousUrlMto url) {
		this.url = url;
	}
}
