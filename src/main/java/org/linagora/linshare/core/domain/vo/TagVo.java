package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.EntryTagAssociation;

public class TagVo {
	
	protected String name;
	
	protected String tagEnumValue;

	public TagVo(String name) {
		super();
		this.name = name;
	}
	
	public TagVo(String name, String tagEnumValue) {
		super();
		this.name = name;
		this.tagEnumValue = tagEnumValue;
	}
	
	public TagVo(EntryTagAssociation entryTag) {
		this.name = entryTag.getTag().getName();
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
		return name + ":" + tagEnumValue;
	}
}
