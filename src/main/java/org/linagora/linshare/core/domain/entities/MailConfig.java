/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailLayout;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MailConfig {

	private long id;

	private MailLayout mailLayoutHtml;

	private AbstractDomain domain;

	private String name;

	private boolean visible;

	private MailLayout mailLayoutText;

	private Date creationDate;

	private Date modificationDate;

	private String uuid;

	private Map<Integer, MailFooterLang> mailFooters = Maps.newHashMap();

	private Set<MailContentLang> mailContents = Sets.newHashSet();

	public MailConfig() {
	}

	public long getId() {
		return id;
	}

	private void setId(long id) {
		this.id = id;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getName() {
		return name;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public java.util.Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public java.util.Date getModificationDate() {
		return modificationDate;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setDomain(AbstractDomain value) {
		this.domain = value;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setMailFooters(Map<Integer, MailFooterLang> value) {
		this.mailFooters = value;
	}

	public Map<Integer, MailFooterLang> getMailFooters() {
		return mailFooters;
	}

	public void setMailLayoutHtml(MailLayout value) {
		this.mailLayoutHtml = value;
	}

	public MailLayout getMailLayoutHtml() {
		return mailLayoutHtml;
	}

	public void setMailLayoutText(MailLayout value) {
		this.mailLayoutText = value;
	}

	public MailLayout getMailLayoutText() {
		return mailLayoutText;
	}

	public String toString() {
		return String.valueOf(getId());
	}

	public Set<MailContentLang> getMailContents() {
		return mailContents;
	}

	public void setMailContents(Set<MailContentLang> mailContents) {
		this.mailContents = mailContents;
	}

}
