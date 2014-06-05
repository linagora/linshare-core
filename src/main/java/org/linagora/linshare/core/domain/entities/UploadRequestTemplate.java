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
package org.linagora.linshare.core.domain.entities;

public class UploadRequestTemplate {

	private long id;

	private String name;

	private String description;

	private Long durationBeforeActivation;

	private Long unitBeforeActivation;

	private Long durationBeforeExpiry;

	private Long unitBeforeExpiry;

	private Boolean groupMode;

	private Boolean depositMode;

	private Long maxFile;

	private Long maxFileSize;

	private Long maxDepositSize;

	private String locale;

	private Boolean secured;

	private Long dayBeforeNotification;

	private Boolean prolongationMode;

	public UploadRequestTemplate() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getDurationBeforeActivation() {
		return durationBeforeActivation;
	}

	public void setDurationBeforeActivation(Long durationBeforeActivation) {
		this.durationBeforeActivation = durationBeforeActivation;
	}

	public Long getUnitBeforeActivation() {
		return unitBeforeActivation;
	}

	public void setUnitBeforeActivation(Long unitBeforeActivation) {
		this.unitBeforeActivation = unitBeforeActivation;
	}

	public Long getDurationBeforeExpiry() {
		return durationBeforeExpiry;
	}

	public void setDurationBeforeExpiry(Long durationBeforeExpiry) {
		this.durationBeforeExpiry = durationBeforeExpiry;
	}

	public Long getUnitBeforeExpiry() {
		return unitBeforeExpiry;
	}

	public void setUnitBeforeExpiry(Long unitBeforeExpiry) {
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

	public Long getMaxFile() {
		return maxFile;
	}

	public void setMaxFile(Long maxFile) {
		this.maxFile = maxFile;
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
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

}
