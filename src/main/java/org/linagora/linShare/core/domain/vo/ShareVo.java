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
package org.linagora.linShare.core.domain.vo;

import java.util.Calendar;


public class ShareVo {

	/** the document's owner */
	private final UserVo sender;
	
	/** the recipient of the document */
	private final UserVo receiver;
	
	/** the shared document */
	private final ShareDocumentVo document;
	
	/** the expiration date of the share*/
	private final Calendar expirationDate;
	
	/** is the sharing is still active or not */
	private final Boolean shareActive;

	/** is the document has been downloaded */
	private final Boolean downloaded;
	
	/**
	 * The comment when the share is done.
	 */
	private final String comment;

	public ShareVo(UserVo sender, UserVo receiver, ShareDocumentVo document,
			Calendar expirationDate, Boolean shareActive, Boolean downloaded,
			String comment) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.document = document;
		this.expirationDate = expirationDate;
		this.shareActive = shareActive;
		this.downloaded = downloaded;
		this.comment = comment;
	}

	public UserVo getSender() {
		return sender;
	}

	public UserVo getReceiver() {
		return receiver;
	}

	public ShareDocumentVo getDocument() {
		return document;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public Boolean getShareActive() {
		return shareActive;
	}

	public Boolean getDownloaded() {
		return downloaded;
	}

	public String getComment() {
		return comment;
	}
	
	
	
}
