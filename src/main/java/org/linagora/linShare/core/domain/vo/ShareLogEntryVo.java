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

import org.linagora.linShare.core.domain.LogAction;

public class ShareLogEntryVo extends FileLogEntryVo {
	
	private final String targetMail;

	private final String targetFirstname;
	
	private final String targetLastname;
	
	private final Calendar expirationDate;

	public ShareLogEntryVo(Calendar actionDate, String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction,
			String description, String fileName, Long fileSize,
			String fileType, String targetMail, String targetFirstname,
			String targetLastname, final Calendar expirationDate) {
		super(actionDate, actorMail, actorFirstname, actorLastname, actorDomain,
				logAction,
				description, fileName, fileSize, fileType);
		this.targetMail = targetMail;
		this.targetFirstname = targetFirstname;
		this.targetLastname = targetLastname;
		if (expirationDate!=null)
			this.expirationDate = (Calendar)expirationDate.clone();
		else 
			this.expirationDate = null;
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
	
	
}
