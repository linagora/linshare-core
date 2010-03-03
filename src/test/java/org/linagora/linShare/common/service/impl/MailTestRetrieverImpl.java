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
package org.linagora.linShare.common.service.impl;

import org.linagora.linShare.common.service.MailTestRetriever;

public class MailTestRetrieverImpl implements MailTestRetriever{

	private final String sender;
	private final String recipient;
	
	public MailTestRetrieverImpl(String sender,String recipient){
		this.sender=sender;
		this.recipient=recipient;
	}

	/**
	 * @see MailTestRetriever#getRecipientMail()
	 */
	public String getRecipientMail() {
		return recipient;
	}
	/**
	 * @see MailTestRetriever#getSenderMail()
	 */
	public String getSenderMail() {
		return sender;
	}

	
	
}
