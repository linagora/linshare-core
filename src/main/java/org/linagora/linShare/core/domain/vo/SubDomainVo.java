package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Role;

public class SubDomainVo extends AbstractDomainVo {
	
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
	public SubDomainVo(AbstractDomain entity) {
		super(entity);
		if(entity.getParentDomain() != null) {
			this.setParentDomainIdentifier(entity.getParentDomain().getIdentifier());
		}
	}

	public SubDomainVo() {
		super();
		this.defaultRole = Role.SIMPLE;
	}

	
}
