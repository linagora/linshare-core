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
package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.LogAction;

public class ShareLogEntry extends FileLogEntry {

	private static final long serialVersionUID = -2189443188392440017L;

	private final String targetMail;

	private final String targetFirstname;
	
	private final String targetLastname;
	
	private final String targetDomain;

	private final Calendar expirationDate;
	
	protected ShareLogEntry() {
		super();
		this.targetMail = null;
		this.targetFirstname = null;
		this.targetLastname = null;
		this.targetDomain = null;
		this.expirationDate = null;
	}
	
	
	public ShareLogEntry(User actor, ShareEntry share,	LogAction logAction, String description) {
		
		this.actorMail = actor.getMail();
		this.actorFirstname = actor.getFirstName();
		this.actorLastname = actor.getLastName();
		this.actorDomain = actor.getDomainId();
		this.logAction = logAction;
		this.description = description;
		
		DocumentEntry doc = share.getDocumentEntry();
		this.fileName = doc.getName();
		this.fileSize = doc.getSize();
		this.fileType = doc.getType();
		
		User owner = (User) share.getEntryOwner();
		this.targetMail = owner.getMail();
		this.targetFirstname = owner.getFirstName();
		this.targetDomain = owner.getDomainId();
		this.targetLastname = owner.getLastName();
		this.expirationDate = share.getExpirationDate();
		
		
	}
	
	
	
	
	public ShareLogEntry(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction,
			String description, String fileName, Long fileSize,
			String fileType, 
			String targetMail, String targetFirstname,
			String targetLastname, String targetDomain,
			Calendar expirationDate) {
		super(actionDate, actorMail, actorFirstname, actorLastname, actorDomain,
				logAction,
				description, fileName, fileSize, fileType);
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetDomain = targetDomain;
		this.targetLastname = targetLastname;
		this.expirationDate = expirationDate;
	}

	public ShareLogEntry(String actorMail,
			String actorFirstname, String actorLastname, String actorDomain, 
			LogAction logAction,
			String description, String fileName, Long fileSize,
			String fileType, 
			String targetMail, String targetFirstname,
			String targetLastname, String targetDomain,
			Calendar expirationDate) {
		super(actorMail, actorFirstname, actorLastname, actorDomain,
				logAction,
				description, fileName, fileSize, fileType);
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		this.targetDomain = targetDomain;
		this.expirationDate = expirationDate;
	}
	
	public String getTargetMail() {
		return targetMail;
	}

	public String getTargetFirstname() {
		return targetFirstname;
	}

	public String getTargetLastname() {
		return targetLastname;
	}
	public Calendar getExpirationDate() {
		return expirationDate;
	}
	
	public String getTargetDomain() {
		return targetDomain;
	}
	
	
	
	

}
