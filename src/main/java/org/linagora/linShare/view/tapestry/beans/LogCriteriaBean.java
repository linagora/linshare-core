/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.beans;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.view.tapestry.enums.CriterionMatchMode;


public class LogCriteriaBean {

	private List<String> actorMails; //The list of the selected users
	
	private String actorFirstname;
	
	private String actorLastname;

	private List<String> targetMails; //The list of the selected users
	
	private String targetFirstname;
	
	private String targetLastname;

	private Calendar beforeDate; //The begin date for the search
	
	private Calendar afterDate; //The end date for the search
	
	private List<LogAction> logActions; //The list of Actions to retrieve

	private String fileName; //file name to find

	private String fileExtension;
	
	private CriterionMatchMode fileNameMatchMode;

	
	public LogCriteriaBean(){
		actorMails = new ArrayList<String>();
		targetMails = new ArrayList<String>();
		fileNameMatchMode = CriterionMatchMode.START;
	}
	
	public LogCriteriaBean(List<String> actorMails, String actorFirstname, String actorLastname,
			List<String> targetMails, String targetFirstname, String targetLastname,
			Calendar beforeDate, Calendar afterDate, List<LogAction> logActions) {
		this.actorMails = actorMails;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.targetMails = targetMails;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.beforeDate = beforeDate;
		this.afterDate = afterDate;
		this.logActions = logActions;
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

	public CriterionMatchMode getFileNameMatchMode() {
		return fileNameMatchMode;
	}

	public void setFileNameMatchMode(CriterionMatchMode fileNameMatchMode) {
		this.fileNameMatchMode = fileNameMatchMode;
	}
	
}

