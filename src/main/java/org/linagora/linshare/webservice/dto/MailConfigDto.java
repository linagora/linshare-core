/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.webservice.dto;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooterLang;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "MailConfig")
@ApiModel(value = "MailConfig", description = "")
public class MailConfigDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Domain")
	private String domain;

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Visible")
	private boolean visible;

	@ApiModelProperty(value = "CreationDate")
	private Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	private Date modificationDate;

	@ApiModelProperty(value = "MailLayoutHtml")
	private String mailLayoutHtml;

	@ApiModelProperty(value = "MailLayoutText")
	private String mailLayoutText;

	@ApiModelProperty(value = "MailFooters")
	private Map<Integer, String> mailFooterLangs = Maps.newHashMap();

	@ApiModelProperty(value = "MailContents")
	private Set<String> mailContentLangs = Sets.newHashSet();

	public MailConfigDto() {
	}

	public MailConfigDto(MailConfig config) {
		super();
		this.uuid = config.getUuid();
		this.domain = config.getDomain().getIdentifier();
		this.name = config.getName();
		this.visible = config.isVisible();
		this.creationDate = new Date(config.getCreationDate().getTime());
		this.modificationDate = new Date(config.getModificationDate().getTime());
		this.mailLayoutHtml = config.getMailLayoutHtml().getUuid();
		this.mailLayoutText = config.getMailLayoutText().getUuid();

		Set<MailContentLang> mcls = config.getMailContents();
		Map<Integer, MailFooterLang> mfls = config.getMailFooters();

		for (MailContentLang mcl : mcls) {
			this.mailContentLangs.add(mcl.getUuid());
		}
		for (Entry<Integer, MailFooterLang> e : mfls.entrySet()) {
			this.mailFooterLangs.put(e.getKey(), e.getValue().getUuid());
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
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

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getMailLayoutHtml() {
		return mailLayoutHtml;
	}

	public void setMailLayoutHtml(String mailLayoutHtml) {
		this.mailLayoutHtml = mailLayoutHtml;
	}

	public String getMailLayoutText() {
		return mailLayoutText;
	}

	public void setMailLayoutText(String mailLayoutText) {
		this.mailLayoutText = mailLayoutText;
	}

	public Map<Integer, String> getMailFooters() {
		return mailFooterLangs;
	}

	public void setMailFooters(Map<Integer, String> mailFooterLangs) {
		this.mailFooterLangs = mailFooterLangs;
	}

	public Set<String> getMailContentLangs() {
		return mailContentLangs;
	}

	public void setMailContentLangs(Set<String> mailContentLangs) {
		this.mailContentLangs = mailContentLangs;
	}
}
