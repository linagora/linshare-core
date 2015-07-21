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
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;

public class UploadRequestTemplateVo implements Serializable {

	private static final long serialVersionUID = 6506743228628907140L;

	@Validate("required")
	private String name;

	@NonVisual
	private String uuid;

	private String description2;

	private Long durationBeforeActivation;

    @Validate("required")
	private TimeUnit unitBeforeActivation;

	private Long durationBeforeExpiry;

    @Validate("required")
	private TimeUnit unitBeforeExpiry;

	private Boolean groupMode;

	private Boolean depositMode;

	private Long maxFileCount;

	private Long maxFileSize;

	private Long maxDepositSize;

	private Language locale;

	private Boolean secured;

	private Long dayBeforeNotification;

	@NonVisual
	private Boolean prolongationMode;

	private Date creationDate;

	private Date modificationDate;

	@Inject
	public UploadRequestTemplateVo() {
		super();
		unitBeforeActivation = TimeUnit.DAY;
		unitBeforeExpiry = TimeUnit.DAY;
	}

	public UploadRequestTemplateVo(UploadRequestTemplate t) {
		name = t.getName();
		uuid = t.getUuid();
		description2 = t.getDescription();
		durationBeforeActivation = t.getDurationBeforeActivation();
		unitBeforeActivation = t.getUnitBeforeActivation() == null ? TimeUnit.DAY
				: TimeUnit.fromInt(t.getUnitBeforeActivation());
		durationBeforeExpiry = t.getDurationBeforeExpiry();
		unitBeforeExpiry = t.getUnitBeforeExpiry() == null ? TimeUnit.DAY
				: TimeUnit.fromInt(t.getUnitBeforeExpiry());
		groupMode = t.getGroupMode();
		depositMode = t.getDepositMode();
		maxFileCount = t.getMaxFile();
		maxFileSize = t.getMaxFileSize();
		maxDepositSize = t.getMaxDepositSize();
		locale = Language.fromTapestryLocale(t.getLocale());
		secured = t.getSecured();
		dayBeforeNotification = t.getDayBeforeNotification();
		prolongationMode = t.getProlongationMode();
		creationDate = t.getCreationDate();
		modificationDate = t.getModificationDate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description) {
		this.description2 = description;
	}

	public Long getDurationBeforeActivation() {
		return durationBeforeActivation;
	}

	public void setDurationBeforeActivation(Long durationBeforeActivation) {
		this.durationBeforeActivation = durationBeforeActivation;
	}

	public TimeUnit getUnitBeforeActivation() {
		return unitBeforeActivation;
	}

	public void setUnitBeforeActivation(TimeUnit unitBeforeActivation) {
		this.unitBeforeActivation = unitBeforeActivation;
	}

	public Long getDurationBeforeExpiry() {
		return durationBeforeExpiry;
	}

	public void setDurationBeforeExpiry(Long durationBeforeExpiry) {
		this.durationBeforeExpiry = durationBeforeExpiry;
	}

	public TimeUnit getUnitBeforeExpiry() {
		return unitBeforeExpiry;
	}

	public void setUnitBeforeExpiry(TimeUnit unitBeforeExpiry) {
		this.unitBeforeExpiry = unitBeforeExpiry;
	}

	public Boolean getGroupMode() {
		return groupMode;
	}

	public void setGroupMode(Boolean groupMode) {
		this.groupMode = groupMode;
	}

	public Boolean getDepositMode() {
		return depositMode;
	}

	public void setDepositMode(Boolean depositMode) {
		this.depositMode = depositMode;
	}

	public Long getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Long maxFile) {
		this.maxFileCount = maxFile;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}

	public Boolean getSecured() {
		return secured;
	}

	public void setSecured(Boolean secured) {
		this.secured = secured;
	}

	public Long getDayBeforeNotification() {
		return dayBeforeNotification;
	}

	public void setDayBeforeNotification(Long dayBeforeNotification) {
		this.dayBeforeNotification = dayBeforeNotification;
	}

	public Boolean getProlongationMode() {
		return prolongationMode;
	}

	public void setProlongationMode(Boolean prolongationMode) {
		this.prolongationMode = prolongationMode;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/*
	 * used as label for tapestry' select
	 */
	@Override
	public String toString() {
		return name;
	}

	public UploadRequestTemplate toEntity() {
		UploadRequestTemplate ret = new UploadRequestTemplate();

		ret.setName(name);
		ret.setDescription(description2);
		ret.setDurationBeforeActivation(durationBeforeActivation);
		if (unitBeforeActivation != null) {
			ret.setUnitBeforeActivation(unitBeforeActivation.toLong());
		}
		ret.setDurationBeforeExpiry(durationBeforeExpiry);
		if (unitBeforeExpiry != null) {
			ret.setUnitBeforeExpiry(unitBeforeExpiry.toLong());
		}
		ret.setGroupMode(groupMode);
		ret.setDepositMode(depositMode);
		ret.setMaxFile(maxFileCount);
		ret.setMaxFileSize(maxFileSize);
		ret.setMaxDepositSize(maxDepositSize);
		if (locale != null) {
			ret.setLocale(locale.toString());
		}
		ret.setSecured(secured);
		ret.setDayBeforeNotification(dayBeforeNotification);
		ret.setProlongationMode(prolongationMode);
		return ret;
	}

	public UploadRequestTemplate toEntity(UploadRequestTemplate t) {
		t.setDescription(description2);
		t.setDurationBeforeActivation(durationBeforeActivation);
		if (unitBeforeActivation != null) {
			t.setUnitBeforeActivation(unitBeforeActivation.toLong());
		}
		t.setDurationBeforeExpiry(durationBeforeExpiry);
		if (unitBeforeExpiry != null) {
			t.setUnitBeforeExpiry(unitBeforeExpiry.toLong());
		}
		t.setGroupMode(groupMode);
		t.setDepositMode(depositMode);
		t.setMaxFile(maxFileCount);
		t.setMaxFileSize(maxFileSize);
		t.setMaxDepositSize(maxDepositSize);
		if (locale != null) {
			t.setLocale(locale.toString());
		}
		t.setSecured(secured);
		t.setDayBeforeNotification(dayBeforeNotification);
		t.setProlongationMode(prolongationMode);
		return t;
	}
}
