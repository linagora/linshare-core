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
package org.linagora.linshare.webservice.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.LogAction;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "LogCriteria")
@ApiModel(value = "LogCriteria", description = "Criteria used to search the history")
public class LogCriteriaDto {

	@XmlElement(name = "actorMails")
	@ApiModelProperty(value = "List of actor's mail")
	private List<String> actorMails; // The selected user

	@XmlElement(name = "actorFirstname")
	@ApiModelProperty(value = "First name of the actor")
	private String actorFirstname;

	@XmlElement(name = "actorLastname")
	@ApiModelProperty(value = "Last name of the actor")
	private String actorLastname;

	@XmlElement(name = "actorDomain")
	@ApiModelProperty(value = "Domain of the actor")
	private String actorDomain;

	@XmlElement(name = "targetMails")
	@ApiModelProperty(value = "List of target's mail")
	private List<String> targetMails; // The list of the selected users

	@XmlElement(name = "targetFirstname")
	@ApiModelProperty(value = "First name of the target")
	private String targetFirstname;

	@XmlElement(name = "targetLastname")
	@ApiModelProperty(value = "Last name of the target")
	private String targetLastname;

	@XmlElement(name = "targetDomain")
	@ApiModelProperty(value = "Domain of the target")
	private String targetDomain;

	@XmlElement(name = "beforeDate")
	@ApiModelProperty(value = "The starting date")
	private Calendar beforeDate; // The begin date for the search

	@XmlElement(name = "beforeDate")
	@ApiModelProperty(value = "The ending date")
	private Calendar afterDate; // The end date for the search

	@XmlElement(name = "logActions")
	@ApiModelProperty(value = "A list of actions")
	private List<LogAction> logActions; // The list of Actions to retrieve

	@XmlElement(name = "fileName")
	@ApiModelProperty(value = "The file name")
	private String fileName; // file name to find

	@XmlElement(name = "fileExtension")
	@ApiModelProperty(value = "The file extension")
	private String fileExtension;

	public LogCriteriaDto() {
		targetMails = new ArrayList<String>();
	}

	public LogCriteriaDto(List<String> actorMails, String actorFirstname,
			String actorLastname, String actorDomain, List<String> targetMails,
			String targetFirstname, String targetLastname, String targetDomain,
			Calendar beforeDate, Calendar afterDate, List<LogAction> logActions) {
		this.actorMails = actorMails;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorDomain = actorDomain;
		this.targetMails = targetMails;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.targetDomain = targetDomain;
		this.beforeDate = beforeDate;
		this.afterDate = afterDate;
		this.logActions = logActions;
	}

	public List<String> getActorMails() {
		return actorMails;
	}

	public void setActorMails(List<String> mails) {
		this.actorMails = mails;
	}

	public Calendar getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Calendar beforeDate) {
		this.beforeDate = beforeDate;
	}

	public Calendar getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Calendar afterDate) {
		this.afterDate = afterDate;
	}

	public List<LogAction> getLogActions() {
		return logActions;
	}

	public void setLogActions(List<LogAction> logActions) {
		this.logActions = logActions;
	}

	public String getActorFirstname() {
		return actorFirstname;
	}

	public void setActorFirstname(String firstname) {
		this.actorFirstname = firstname;
	}

	public String getActorLastname() {
		return actorLastname;
	}

	public void setActorLastname(String lastname) {
		this.actorLastname = lastname;
	}

	public List<String> getTargetMails() {
		return targetMails;
	}

	public void setTargetMails(List<String> targetMails) {
		this.targetMails = targetMails;
	}

	public String getTargetFirstname() {
		return targetFirstname;
	}

	public void setTargetFirstname(String targetFirstname) {
		this.targetFirstname = targetFirstname;
	}

	public String getTargetLastname() {
		return targetLastname;
	}

	public void setTargetLastname(String targetLastname) {
		this.targetLastname = targetLastname;
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

	public void setActorDomain(String actorDomain) {
		this.actorDomain = actorDomain;
	}

	public void setTargetDomain(String targetDomain) {
		this.targetDomain = targetDomain;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public String getTargetDomain() {
		return targetDomain;
	}

}
