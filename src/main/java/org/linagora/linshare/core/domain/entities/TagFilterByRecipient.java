package org.linagora.linshare.core.domain.entities;

public class TagFilterByRecipient extends TagFilterRule {

	@Override
	public Boolean isTrue(Account actor) {
		return false;
	}

}
