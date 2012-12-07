package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.TagEnum;
import org.linagora.linshare.core.domain.entities.TagEnumValue;

public class TagEnumVo extends TagVo {

	private Boolean notNull;
	
	private List<String> enumValues = new ArrayList<String>();
	
	public TagEnumVo(TagEnum tag) {
		super(tag.getName());
		this.isTagEnum = true;
		this.notNull=tag.getNotNull();
		for (TagEnumValue tagValue : tag.getEnumValues()) {
			this.enumValues.add(tagValue.getValue()); 
		}
	}

	public Boolean getNotNull() {
		return notNull;
	}

	public void setNotNull(Boolean notNull) {
		this.notNull = notNull;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}
}
