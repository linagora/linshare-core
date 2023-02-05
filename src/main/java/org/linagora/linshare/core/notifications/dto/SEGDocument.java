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
package org.linagora.linshare.core.notifications.dto;

import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;

import com.google.common.collect.Lists;

/**
 * @author FMartin
 *
 */
public class SEGDocument {

	protected String uuid;

	protected String name;

	protected Long size;

	protected String href;

	protected Boolean oneDownloaded = false;

	protected Boolean allDownloaded = false;

	protected List<SEGShare> shares;

	public SEGDocument(DocumentEntry de) {
		super();
		this.uuid = de.getUuid();
		this.name = de.getName();
		this.size = de.getSize();
		this.shares = Lists.newArrayList();
	}

	public SEGDocument(String name, Long size) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.size = size;
		this.shares = Lists.newArrayList();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public boolean isOneDownloaded() {
		return oneDownloaded;
	}

	public void setOneDownloaded(Boolean oneDownloaded) {
		this.oneDownloaded = oneDownloaded;
	}

	public Boolean isAllDownloaded() {
		return allDownloaded;
	}

	public void setAllDownloaded(Boolean allDownloaded) {
		this.allDownloaded = allDownloaded;
	}

	public List<SEGShare> getShares() {
		return shares;
	}

	public void setShares(List<SEGShare> shares) {
		this.shares = shares;
	}

	public void addShare(Entry entry) {
		if (entry instanceof ShareEntry) {
			ShareEntry share = (ShareEntry) entry;
			shares.add(new SEGShare(share.getRecipient(), share.getDownloaded() > 0));
		} else {
			AnonymousShareEntry share = (AnonymousShareEntry) entry;
			shares.add(new SEGShare(share.getAnonymousUrl().getContact(), share.getDownloaded() > 0));
		}
	}

	public void addShare(MailContact mailContact, boolean downloaded) {
		shares.add(new SEGShare(mailContact, downloaded));
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String toString() {
		return "ShareGroupDocument [uuid=" + uuid + ", name=" + name + ", size=" + size + ", href=" + href
				+ ", oneDownloaded=" + oneDownloaded + ", allDownloaded=" + allDownloaded + "]";
	}

}
