package org.linagora.linshare.core.domain.entities;

import java.util.Set;
import java.util.HashSet;

public class TagFilter {
	
	public TagFilter() {
	}
	
	private Long id;
	
	private String name;
	
	private Set<TagFilterRule> rules = new HashSet<TagFilterRule>();
	
	
	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setRules(Set<TagFilterRule> value) {
		this.rules = value;
	}
	
	public Set<TagFilterRule> getRules() {
		return rules;
	}
	
}

