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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.exception.TechnicalException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MailConfig implements Cloneable {

	private long id;

	private MailLayout mailLayoutHtml;

	private AbstractDomain domain;

	private String name;

	private boolean visible;

	private boolean readonly;

	private Date creationDate;

	private Date modificationDate;

	private String uuid;

	private Map<Integer, MailFooterLang> mailFooters = Maps.newHashMap();

	private Set<MailContentLang> mailContentLangs = Sets.newHashSet();

	public MailConfig() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MailLayout getMailLayoutHtml() {
		return mailLayoutHtml;
	}

	public void setMailLayoutHtml(MailLayout mailLayoutHtml) {
		this.mailLayoutHtml = mailLayoutHtml;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<Integer, MailFooterLang> getMailFooters() {
		return mailFooters;
	}

	public void setMailFooters(Map<Integer, MailFooterLang> mailFooters) {
		this.mailFooters = mailFooters;
	}

	public Set<MailContentLang> getMailContentLangs() {
		return mailContentLangs;
	}

	public void setMailContentLangs(Set<MailContentLang> mailContents) {
		this.mailContentLangs = mailContents;
	}

	@Override
	public MailConfig clone() throws CloneNotSupportedException {
		// Every properties are clones, except domain.
		MailConfig mc = null;
		try {
			mc = (MailConfig) super.clone();
			mc.id = 0;
			mc.mailLayoutHtml = mailLayoutHtml.clone();
			mc.mailFooters = Maps.newHashMap();
			Set<Entry<Integer,MailFooterLang>> entrySet = mailFooters.entrySet();
			for (Entry<Integer, MailFooterLang> entry : entrySet) {
				mc.mailFooters.put(entry.getKey(), entry.getValue().clone());
			}
			mc.mailContentLangs = Sets.newHashSet();
			for (MailContentLang mailContentLang : mailContentLangs) {
				mc.mailContentLangs.add(mailContentLang.clone());
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return mc;
}

	/*
	 * Helpers
	 */

	/**
	 * Find a Footer by its Language
	 * @param lang
	 * @return
	 */
	public MailFooter findFooter(final Language lang) {
		MailFooterLang f = mailFooters.get(lang.toInt());

		if (f == null)
			throw new TechnicalException(
					"No MailFooter matching the language: " + lang);
		return f.getMailFooter();
	}

	/**
	 * Find a MailContent by its Language and MailContentType
	 * 
	 * @param lang
	 * @param type
	 * @return
	 */
	public MailContent findContent(final Language lang,
			final MailContentType type) {
		MailContentLang needle = new MailContentLang(lang, type);

		for (MailContentLang mcl : mailContentLangs) {
			if (mcl.businessEquals(needle))
				return mcl.getMailContent();
		}
		throw new TechnicalException(
				"No MailContent matching the [Language,MailContentType] pair: ["
						+ lang + "," + type + "]");
	}

	public void replaceMailContent(final Language lang,
			final MailContentType type, MailContent replace) {

		MailContentLang needle = new MailContentLang(lang, type);
		needle.setMailContent(replace);
		boolean found = false;
		for (MailContentLang mcl : mailContentLangs) {
			if (mcl.businessEquals(needle)) {
				mailContentLangs.remove(mcl);
				mailContentLangs.add(needle);
				found = true;
				break;
			}
		}
		if (!found) {
			throw new TechnicalException(
					"No MailContent matching the [Language,MailContentType] pair: ["
							+ lang + "," + type + "]");
		}
	}

	@Override
	public String toString() {
		return "MailConfig [domain=" + domain + ", name=" + name + ", visible=" + visible + ", readonly=" + readonly
				+ ", uuid=" + uuid + "]";
	}
}
