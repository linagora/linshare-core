package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Role;

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
