/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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
package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionMatchType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UploadPropositionFilter")
@Document(collection = "upload_proposition_filter")
public class UploadPropositionFilter {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "DomainUuid")
	protected String domainUuid;

	@Schema(description = "Name")
	protected String name;

	@Schema(description = "MatchType")
	protected UploadPropositionMatchType matchType;

	@Schema(description = "UploadPropositionAction")
	protected UploadPropositionActionType uploadPropositionAction;

	@Schema(description = "Enabled")
	protected Boolean enabled;

	@Schema(description = "Order")
	protected Integer order;

	@Schema(description = "UploadPropositionRules")
	protected List<UploadPropositionRule> uploadPropositionRules;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	public UploadPropositionFilter() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}

	public UploadPropositionFilter(String uuid, String domainUuid, String name, UploadPropositionMatchType matchType,
			UploadPropositionActionType uploadPropositionAction, Boolean enabled, Integer order,
			List<UploadPropositionRule> uploadPropositionRules, Date creationDate, Date modificationDate) {
		super();
		this.uuid = uuid;
		this.domainUuid = domainUuid;
		this.name = name;
		this.matchType = matchType;
		this.uploadPropositionAction = uploadPropositionAction;
		this.enabled = enabled;
		this.order = order;
		this.uploadPropositionRules = uploadPropositionRules;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public UploadPropositionFilter(UploadPropositionFilter initFilter) {
		super();
		this.id = initFilter.getId();
		this.uuid = initFilter.getUuid();
		this.domainUuid = initFilter.getDomainUuid();
		this.name = initFilter.getName();
		this.matchType = initFilter.getMatchType();
		this.uploadPropositionAction = initFilter.getUploadPropositionAction();
		this.enabled = initFilter.isEnabled();
		this.enabled = initFilter.isEnabled();
		this.order = initFilter.getOrder();
		this.uploadPropositionRules = initFilter.getUploadPropositionRules();
		this.creationDate = initFilter.getCreationDate();
		this.modificationDate = initFilter.getModificationDate();
		this.creationDate = initFilter.getCreationDate();
		this.modificationDate = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UploadPropositionMatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(UploadPropositionMatchType matchType) {
		this.matchType = matchType;
	}

	public UploadPropositionActionType getUploadPropositionAction() {
		return uploadPropositionAction;
	}

	public void setUploadPropositionAction(UploadPropositionActionType actionType) {
		this.uploadPropositionAction = actionType;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<UploadPropositionRule> getUploadPropositionRules() {
		return uploadPropositionRules;
	}

	public void setUploadPropositionRules(List<UploadPropositionRule> uploadPropositionRules) {
		this.uploadPropositionRules = uploadPropositionRules;
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

}
