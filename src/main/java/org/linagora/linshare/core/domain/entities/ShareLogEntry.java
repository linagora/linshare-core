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
	
	
	public ShareLogEntry(Account actor, ShareEntry share,	LogAction logAction, String description) {
		
		super(actor, logAction, description, share.getName(), share.getSize(), share.getType());
		
		Account target = share.getRecipient();
		this.targetDomain = target.getDomainId();
		if(isUser(target)) {
			User user = (User)target;
			this.targetMail = user.getMail();
			this.targetFirstname = user.getFirstName();
			this.targetLastname = user.getLastName();
		} else {
			this.targetMail = target.getLsUuid();
			this.targetFirstname = "";
			this.targetLastname = "";
		}
		this.expirationDate = share.getExpirationDate();
		
	}
	
	public ShareLogEntry(Account actor, AnonymousShareEntry share,	LogAction logAction, String description) {
		
		super(actor, logAction, description, share.getName(), share.getSize(), share.getType());
		
		this.targetDomain = "";
		this.targetMail = share.getAnonymousUrl().getContact().getMail();
		this.targetFirstname = "";
		this.targetLastname = "";
		this.expirationDate = share.getExpirationDate();
	}
	
	public ShareLogEntry(Account actor, DocumentEntry document, LogAction logAction, String description, Calendar expirationDate) {
		
		super(actor, logAction, description, document.getName(), document.getSize(), document.getType());
		
		Account target = document.getEntryOwner();
		this.targetDomain = target.getDomainId();
		if(isUser(target)) {
			User user = (User)target;
			this.targetMail = user.getMail();
			this.targetFirstname = user.getFirstName();
			this.targetLastname = user.getLastName();
		} else {
			this.targetMail = target.getLsUuid();
			this.targetFirstname = "";
			this.targetLastname = "";
		}
		this.expirationDate = expirationDate;
		
	}

	
	
	public ShareLogEntry(Account actor, LogAction logAction, String description, String fileName, Long fileSize, String fileType, Account target, Calendar expirationDate) {
		super(actor, logAction, description, fileName, fileSize, fileType);

		this.targetDomain = target.getDomainId();
		if(isUser(target)) {
			User user = (User)target;
			this.targetMail = user.getMail();
			this.targetFirstname = user.getFirstName();
			this.targetLastname = user.getLastName();
		} else {
			this.targetMail = target.getLsUuid();
			this.targetFirstname = "";
			this.targetLastname = "";
		}
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
