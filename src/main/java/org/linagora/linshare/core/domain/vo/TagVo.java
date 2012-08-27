package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.EntryTagAssociation;

public class TagVo {
	
	protected String name;
	
	protected String tagEnumValue;
	
	protected Boolean isTagEnum;

	public TagVo(String name) {
		super();
		this.name = name;
		this.isTagEnum = false;
	}
	
	public TagVo(String name, String tagEnumValue) {
		super();
		this.name = name;
		this.tagEnumValue = tagEnumValue;
		this.isTagEnum = false;
	}
	
	public TagVo(EntryTagAssociation entryTag) {
		this.name = entryTag.getTag().getName();
		this.isTagEnum = true;
		if(entryTag.getTagEnumValue() != null)
			this.tagEnumValue = entryTag.getTagEnumValue().getValue();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if(tagEnumValue == null)
			return name;
		else
			return name + ":" + tagEnumValue;
	}

	public String getTagEnumValue() {
		return tagEnumValue;
	}

	public void setTagEnumValue(String tagEnumValue) {
		this.tagEnumValue = tagEnumValue;
	}

	
}
