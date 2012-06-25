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
package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.constants.UserType;


public class DisplayableAccountOccupationEntryVo implements Comparable<DisplayableAccountOccupationEntryVo>  {

	private final String actorFirstname;
	
	private final String actorLastname;
	
	private final String actorMail;
	
	private final UserType actorType;
	
	private final Long userUsedQuota;
	
	private final Long userAvailableQuota;
	
	private final Long userTotalQuota;

	public DisplayableAccountOccupationEntryVo(String actorFirstname,
			String actorLastname, String actorMail, UserType actorType,
			Long userAvailableQuota, Long userTotalQuota, Long userUsedQuota) {
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorMail = actorMail;
		this.actorType = actorType;
		this.userAvailableQuota = userAvailableQuota;
		this.userTotalQuota = userTotalQuota;
		this.userUsedQuota = userUsedQuota;
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
	
	public UserType getActorType() {
		return actorType;
	}

	public Long getUserAvailableQuota() {
		return userAvailableQuota;
	}

	public Long getUserTotalQuota() {
		return userTotalQuota;
	}
	
	public Long getUserUsedQuota() {
		return userUsedQuota;
	}
	
	public int compareTo(DisplayableAccountOccupationEntryVo o) {
		return this.userAvailableQuota.compareTo(o.getUserAvailableQuota());
	}
	
}
