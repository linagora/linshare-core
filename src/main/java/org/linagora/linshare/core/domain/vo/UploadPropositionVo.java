/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.UploadProposition;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class UploadPropositionVo implements Serializable {

	private static final long serialVersionUID = -1644957309374227845L;

	private long id;

	private String domain;

	private String uuid;

	private UploadPropositionStatus status;

	private String subject;

	private String body;

	private String mail;

	private String firstName;

	private String lastName;

	private String domaineSource;

	private String recipientMail;

	private Date creationDate;

	private Date modificationDate;

	public UploadPropositionVo() {
		super();
	}

	public UploadPropositionVo(UploadProposition p) {
		super();
		this.id = p.getId();
		this.domain = p.getDomain().getIdentifier();
		this.uuid = p.getUuid();
		this.status = p.getStatus();
		this.subject = p.getSubject();
		this.body = p.getBody();
		this.mail = p.getMail();
		this.firstName = p.getFirstName();
		this.lastName = p.getLastName();
		this.domaineSource = p.getDomainSource();
		this.recipientMail = p.getRecipientMail();
		this.creationDate = p.getCreationDate();
		this.modificationDate = p.getModificationDate();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public UploadPropositionStatus getStatus() {
		return status;
	}

	public void setStatus(UploadPropositionStatus status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDomaineSource() {
		return domaineSource;
	}

	public void setDomaineSource(String domaineSource) {
		this.domaineSource = domaineSource;
	}

	public String getRecipientMail() {
		return recipientMail;
	}

	public void setRecipientMail(String recipientMail) {
		this.recipientMail = recipientMail;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/*
	 * Transformer
	 */
	public UploadProposition toEntity() {
		UploadProposition ret = new UploadProposition();

		return ret;
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
		if (!(obj instanceof UploadPropositionVo))
			return false;
		UploadPropositionVo other = (UploadPropositionVo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@NonVisual
	public boolean isPending() {
		return status.equals(UploadPropositionStatus.SYSTEM_PENDING);
	}

	/*
	 * Filters
	 */
	public static Predicate<? super UploadPropositionVo> equalTo(String uuid) {
		UploadPropositionVo test = new UploadPropositionVo();

		test.setUuid(uuid);
		return Predicates.equalTo(test);
	}
}
