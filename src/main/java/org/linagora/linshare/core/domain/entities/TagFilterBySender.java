package org.linagora.linshare.core.domain.entities;

public class TagFilterBySender extends TagFilterRule {
	
	@Override
	public Boolean isTrue(Account actor) {
		return false;
	}

}
