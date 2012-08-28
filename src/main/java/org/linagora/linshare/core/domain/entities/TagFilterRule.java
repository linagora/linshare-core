package org.linagora.linshare.core.domain.entities;

import java.util.Set;
import java.util.HashSet;


public abstract class TagFilterRule {

	public TagFilterRule() {
	}
	
	protected Long id;
	
	protected String regexp;
	
	protected Set<TagFilterRuleTagAssociation> tagFilterRuleTagAssociation = new HashSet<TagFilterRuleTagAssociation>();
	
	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setRegexp(String value) {
		this.regexp = value;
	}
	
	public String getRegexp() {
		return regexp;
	}

	public Set<TagFilterRuleTagAssociation> getTagFilterRuleTagAssociation() {
		return tagFilterRuleTagAssociation;
	}

	public void setTagFilterRuleTagAssociation(Set<TagFilterRuleTagAssociation> tagFilterRuleTagAssociation) {
		this.tagFilterRuleTagAssociation = tagFilterRuleTagAssociation;
	}
	
	public abstract Boolean isTrue(Account actor);

	
}
