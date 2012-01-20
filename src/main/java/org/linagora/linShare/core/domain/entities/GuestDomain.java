package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.DomainType;
import org.linagora.linShare.core.domain.vo.GuestDomainVo;

public class GuestDomain extends AbstractDomain {

	public GuestDomain() {
	}
	
	public GuestDomain(GuestDomainVo guestDomain) {
		super(guestDomain);
	}

	public GuestDomain(String identifier, String label) {
		super(identifier, label);
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.GUESTDOMAIN;
	}
}
