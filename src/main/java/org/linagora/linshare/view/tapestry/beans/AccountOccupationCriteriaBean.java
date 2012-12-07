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
package org.linagora.linshare.view.tapestry.beans;
import java.util.ArrayList;
import java.util.List;


public class AccountOccupationCriteriaBean {

	private List<String> actorMails;
	
	private String actorFirstname;
	
	private String actorLastname;
	
	private String actorDomain;

	
	public AccountOccupationCriteriaBean(){
		actorMails = new ArrayList<String>();
	}
	
	public AccountOccupationCriteriaBean(List<String> actorMails, 
			String actorFirstname, String actorLastname) {
		this.actorMails = actorMails;
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
	}


	public List<String> getActorMails() {
		return actorMails;
	}


	public void setActorMails(List<String> mails) {
		this.actorMails = mails;
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

	public String getActorDomain() {
		return actorDomain;
	}

	public void setActorDomain(String actorDomain) {
		this.actorDomain = actorDomain;
	}
	
}

