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
package org.linagora.linshare.view.tapestry.beans;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.constants.LogActionV1;
import org.linagora.linshare.view.tapestry.enums.CriterionMatchMode;


public class LogCriteriaBean {

	private List<String> actorMails; //The list of the selected users
	
	private String actorFirstname;
	
	private String actorLastname;
	
	private String actorDomain;

	private List<String> targetMails; //The list of the selected users
	
	private String targetFirstname;
	
	private String targetLastname;
	
	private String targetDomain;

	private Calendar beforeDate; //The begin date for the search
	
	private Calendar afterDate; //The end date for the search
	
	private List<LogActionV1> logActions; //The list of Actions to retrieve

	private String fileName; //file name to find

	private String fileExtension;
	
	private CriterionMatchMode fileNameMatchMode;

	
	public LogCriteriaBean(){
		actorMails = new ArrayList<String>();
		targetMails = new ArrayList<String>();
		fileNameMatchMode = CriterionMatchMode.START;
	}
	
	public LogCriteriaBean(List<String> actorMails, String actorFirstname, String actorLastname, String actorDomain,
			List<String> targetMails, String targetFirstname, String targetLastname, String targetDomain,
			Calendar beforeDate, Calendar afterDate, List<LogActionV1> logActions, String fileName, String fileExtension) {
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
		this.fileName = fileName;
		this.fileExtension = fileExtension;
		fileNameMatchMode = CriterionMatchMode.START;
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

	public List<LogActionV1> getLogActions() {
		return logActions;
	}

	public void setLogActions(List<LogActionV1> logActions) {
		this.logActions = logActions;
	}

	public void addLogActions(LogActionV1 logAction) {
		if (this.logActions == null) {
			this.logActions = new ArrayList<LogActionV1>();
		}
		this.logActions.add(logAction);
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

	public CriterionMatchMode getFileNameMatchMode() {
		return fileNameMatchMode;
	}

	public void setFileNameMatchMode(CriterionMatchMode fileNameMatchMode) {
		this.fileNameMatchMode = fileNameMatchMode;
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

