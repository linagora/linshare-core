package org.linagora.linshare.core.domain.entities;

public class TagFilterRuleTagAssociation {

	public TagFilterRuleTagAssociation() {
	}
	
	private Long id;
	
	private Tag tag;
	
	private TagEnumValue tagEnumValue;
	

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public Long getORMID() {
		return getId();
	}
	
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public void setTagEnumValue(TagEnumValue value) {
		this.tagEnumValue = value;
	}
	
	public TagEnumValue getTagEnumValue() {
		return tagEnumValue;
	}
}