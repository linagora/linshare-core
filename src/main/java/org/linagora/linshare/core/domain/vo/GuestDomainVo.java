package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Role;

public class GuestDomainVo extends AbstractDomainVo {
	
	private  String parentDomainIdentifier;

	public String getParentDomainIdentifier() {
		return parentDomainIdentifier;
	}

	public void setParentDomainIdentifier(String parentDomainIdentifier) {
		this.parentDomainIdentifier = parentDomainIdentifier;
	}

	/**
	 * @param entity
	 */
	public GuestDomainVo(AbstractDomain entity) {
		super(entity);
		if(entity.getParentDomain() != null) {
			this.setParentDomainIdentifier(entity.getParentDomain().getIdentifier());
		}
	}

	public GuestDomainVo() {
		super();
		this.defaultRole = Role.SIMPLE;
	}
}
