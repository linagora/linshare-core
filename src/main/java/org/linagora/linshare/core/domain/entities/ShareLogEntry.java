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

import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.LogAction;

public class ShareLogEntry extends FileLogEntry {

	private static final long serialVersionUID = -2189443188392440017L;

	private String targetMail;

	private String targetFirstname;

	private String targetLastname;

	private String targetDomain;

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

	public ShareLogEntry(Account actor, DocumentEntry document, LogAction logAction, String description, Date expirationDate) {

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
		// FIXME : Calendar hack : temporary hack on expiry date
		Calendar expiryCal = Calendar.getInstance();
		expiryCal.setTime(expirationDate);
		this.expirationDate = expiryCal;

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

	/*
	 * Wrapper for ease of use
	 * 
	 * This constructor is used for Share Download logging
	 */
	public ShareLogEntry(Account actor, LogAction logAction, String description, ShareEntry shareEntry, Account target) {
		this(actor, logAction, description, shareEntry.getDocumentEntry().getName(),
				shareEntry.getDocumentEntry().getSize(),
				shareEntry.getDocumentEntry().getType(),
				target,
				shareEntry.getExpirationDate());
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

	public ShareLogEntry(Account actor, LogAction logAction, String description, ShareEntry share) {
		super(actor, logAction, description, share.getName(), share.getSize(), share.getType());
		this.expirationDate = share.getExpirationDate();
		this.targetMail = "";
		this.targetFirstname = "";
		this.targetLastname = "";
		this.targetDomain = "";
	}

	public static ShareLogEntry hasCopiedAShare(Account actor, ShareEntry share) {
		ShareLogEntry res = new ShareLogEntry(actor, LogAction.SHARE_COPY, "Copy of a sharing", share);
		return res;
	}

	public static ShareLogEntry hasDownloadedAShare(Account actor, ShareEntry share) {
		Account target = share.getEntryOwner();
		ShareLogEntry res = new ShareLogEntry(actor, LogAction.SHARE_DOWNLOAD, "Download of a sharing, shared by " + target.getAccountReprentation(), share);
		res.targetDomain = target.getDomainId();
		if(isUser(target)) {
			User user = (User)target;
			res.targetMail = user.getMail();
			res.targetFirstname = user.getFirstName();
			res.targetLastname = user.getLastName();
		} else {
			res.targetMail = target.getLsUuid();
			res.targetFirstname = "";
			res.targetLastname = "";
		}
		return res;
	}

	public static ShareLogEntry aShareWasDownloaded(Account actor, ShareEntry share) {
		ShareLogEntry res = new ShareLogEntry(share.getEntryOwner(), LogAction.SHARE_DOWNLOADED, "Share was downloaded by " + actor.getAccountReprentation(), share);
		res.targetDomain = actor.getDomainId();
		if(isUser(actor)) {
			User user = (User)actor;
			res.targetMail = user.getMail();
			res.targetFirstname = user.getFirstName();
			res.targetLastname = user.getLastName();
		} else {
			res.targetMail = actor.getLsUuid();
			res.targetFirstname = "";
			res.targetLastname = "";
		}
		return res;
	}
}
