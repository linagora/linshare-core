/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class UploadRequestEntryVo implements Serializable {

	private static final long serialVersionUID = 4781015539583214255L;

	@NonVisual
	private String uuid;

	private long size;

	private String name;

	private Date creationDate;

	@NonVisual
	private DocumentVo document;

	public UploadRequestEntryVo() {
	}

	public UploadRequestEntryVo(UploadRequestEntry e) {
		this.uuid = e.getUuid();
		this.size = e.getSize();
		this.name = e.getName();
		this.creationDate = e.getCreationDate().getTime();
		if (e.getDocumentEntry() != null) {
			this.document = new DocumentVo(e.getDocumentEntry());
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public DocumentVo getDocument() {
		return document;
	}

	public void setDocumentUuid(DocumentVo document) {
		this.document = document;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UploadRequestEntryVo))
			return false;
		UploadRequestEntryVo other = (UploadRequestEntryVo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	/*
	 * Filters
	 */
	public static Predicate<? super UploadRequestEntryVo> equalTo(String uuid) {
		UploadRequestEntryVo test = new UploadRequestEntryVo();

		test.setUuid(uuid);
		return Predicates.equalTo(test);
	}
}
