package org.linagora.linshare.core.domain.entities;

public class CustomisationEntry {

	private long id;

	private String lang;

	private String value;

	public CustomisationEntry() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
