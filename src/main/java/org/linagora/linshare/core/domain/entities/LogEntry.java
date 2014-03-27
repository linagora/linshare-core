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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.vo.UserVo;

/**
 * Abstract class for the log entry. Extended as FileLogEntry, UserLogEntry,
 * ShareLogEntry
 * 
 * @author ncharles
 * 
 */
public abstract class LogEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7185688904383875993L;

	private Long persistenceId;

	protected Calendar actionDate;

	protected String actorMail;

	protected String actorFirstname;

	protected String actorLastname;

	protected String actorDomain;

	protected LogAction logAction;

	protected String description;

	/**
	 * This field must be protected because of hibernate which need absolutly a
	 * default constructor
	 */
	protected LogEntry() {
		this.persistenceId = null;
		this.actionDate = null;
		this.actorMail = null;
		this.actorDomain = null;
		this.actorFirstname = null;
		this.actorLastname = null;
		this.logAction = null;
		this.description = null;
	}

	/**
	 * Constructor for authentication process
	 * 
	 * @param actor
	 * @param logAction
	 * @param description
	 */
	public LogEntry(UserVo actor, LogAction logAction, String description) {
		this.actorDomain = actor.getDomainIdentifier();
		this.actorMail = actor.getMail();
		this.actorFirstname = actor.getFirstName();
		this.actorLastname = actor.getLastName();
		this.logAction = logAction;
		this.description = description;
		this.actionDate = new GregorianCalendar();
	}

	public LogEntry(Account actor, LogAction logAction, String description) {

		this.actorDomain = actor.getDomainId();
		if (isUser(actor)) {
			User user = (User) actor;
			this.actorMail = user.getMail();
			this.actorFirstname = user.getFirstName();
			this.actorLastname = user.getLastName();

		} else {
			this.actorMail = actor.getLsUuid();
			this.actorFirstname = "";
			this.actorLastname = "";
		}
		this.logAction = logAction;
		this.description = description;
		this.actionDate = new GregorianCalendar();

	}

	public LogEntry(String actorMail, String actorFirstname,
			String actorLastname, String actorDomain, LogAction logAction,
			String description) {
		this.actionDate = new GregorianCalendar();
		this.actorMail = actorMail;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorDomain = actorDomain;
		this.logAction = logAction;
		this.description = description;
	}

	public static boolean isUser(Account actor) {
		if (actor.getAccountType().equals(AccountType.GUEST)
				|| actor.getAccountType().equals(AccountType.INTERNAL)
				|| actor.getAccountType().equals(AccountType.ROOT))
			return true;
		return false;
	}

	public Calendar getActionDate() {
		return actionDate;
	}

	public String getActorMail() {
		return actorMail;
	}

	public String getActorFirstname() {
		return actorFirstname;
	}

	public String getActorLastname() {
		return actorLastname;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public LogAction getLogAction() {
		return logAction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

	/**
	 * Format:
	 * 	USER_ACTIVITY:logAction:actorDomain:actorMail:description
	 */
	@Override
	public String toString() {
		return "USER_ACTIVITY:" + logAction + ":"
				+ (actorDomain == null ? "null domain" : actorDomain) + ":"
				+ actorMail + ":" + description;
	}

}
