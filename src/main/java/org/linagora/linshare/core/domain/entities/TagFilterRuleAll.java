package org.linagora.linshare.core.domain.entities;

public class TagFilterRuleAll extends TagFilterRule {

	@Override
	public Boolean isTrue(Account actor) {
		return true;
	}

}
