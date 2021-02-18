/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.LogActionV1;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "LogCriteria")
@Schema(name = "LogCriteria", description = "Criteria used to search the history")
public class LogCriteriaDto {

	@Schema(description = "List of target's mail")
	private List<String> targetMails; // The list of the selected users

	@Schema(description = "First name of the target")
	private String targetFirstName;

	@Schema(description = "Last name of the target")
	private String targetLastName;

	@Schema(description = "Domain of the target")
	private String targetDomain;

	@Schema(description = "The starting date")
	private Date beforeDate; // The begin date for the search

	@Schema(description = "The ending date")
	private Date afterDate; // The end date for the search

	@Schema(description = "A list of actions")
	private List<LogActionV1> logActions; // The list of Actions to retrieve

	@Schema(description = "The file name")
	private String fileName; // file name to find

	@Schema(description = "The file extension")
	private String fileExtension;

	public LogCriteriaDto() {
		targetMails = new ArrayList<String>();
	}

	public LogCriteriaDto(List<String> targetMails,
			String targetFirstname, String targetLastname, String targetDomain,
			Date beforeDate, Date afterDate, List<LogActionV1> logActions) {
		this.targetMails = targetMails;
		this.targetFirstName = targetFirstname;
		this.targetLastName = targetLastname;
		this.targetDomain = targetDomain;
		this.beforeDate = beforeDate;
		this.afterDate = afterDate;
		this.logActions = logActions;
	}

	public Date getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}

	public Date getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}

	public List<LogActionV1> getLogActions() {
		return logActions;
	}

	public void setLogActions(List<LogActionV1> logActions) {
		this.logActions = logActions;
	}

	public List<String> getTargetMails() {
		return targetMails;
	}

	public void setTargetMails(List<String> targetMails) {
		this.targetMails = targetMails;
	}

	public String getTargetFirstName() {
		return targetFirstName;
	}

	public void setTargetFirstName(String targetFirstname) {
		this.targetFirstName = targetFirstname;
	}

	public String getTargetLastName() {
		return targetLastName;
	}

	public void setTargetLastName(String targetLastname) {
		this.targetLastName = targetLastname;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setTargetDomain(String targetDomain) {
		this.targetDomain = targetDomain;
	}

	public String getTargetDomain() {
		return targetDomain;
	}

}
