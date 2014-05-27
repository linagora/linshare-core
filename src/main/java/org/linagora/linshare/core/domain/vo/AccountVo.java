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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;

public class AccountVo implements Serializable {

	private static final long serialVersionUID = -5114405866304880819L;

	protected final String lsUuid;
	
	protected final AccountType accountType;
	
	protected final Date creationDate;
	
	protected final Date modificationDate;
	
	protected final boolean destroyed;
	
	protected final String domainIdentifier;
	
	protected String ownerLsUuid;
	
	protected Role role = Role.SIMPLE;
	
	protected String locale;
	
	protected boolean enable;


	// constructor just for test
	public AccountVo(String uuid) {
		super();
		this.lsUuid = uuid;
		this.accountType = null;
		this.creationDate = null;
		this.modificationDate = null;
		this.destroyed = false;
		this.domainIdentifier = null;
		this.ownerLsUuid = null;
		this.role = null;
		this.locale = null;
		this.enable = false;
	}

	public AccountVo(Account account) {
		super();
		this.lsUuid = account.getLsUuid();
		this.accountType = account.getAccountType();
		this.creationDate = account.getCreationDate();
		this.modificationDate = account.getModificationDate();
		this.locale = account.getLocale();
		this.enable = account.isEnable();
		this.destroyed = account.isDestroyed();
		if(account.getOwner() != null) {
			this.ownerLsUuid = account.getOwner().getLsUuid();
		}
		this.domainIdentifier = account.getDomain().getIdentifier();
	}

	public String getOwnerLsUuid() {
		return ownerLsUuid;
	}

	public void setOwnerLsUuid(String ownerLsUuid) {
		this.ownerLsUuid = ownerLsUuid;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getLsUuid() {
		return lsUuid;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}
	
	
}
