package org.linagora.linshare.core.domain.entities;

public class LdapAttribute {
	
	private Long id;
	
	private String field;
	
	private String attribute;
	
	private Boolean sync;
	
	private Boolean system;
	
	private Boolean enable;
	
	
	public LdapAttribute() {
	}
	
	public LdapAttribute(String field, String attribute, Boolean sync, Boolean system, Boolean enable) {
		super();
		this.field = field;
		this.attribute = attribute;
		this.sync = sync;
		this.system = system;
		this.enable = enable;
	}
	
	public LdapAttribute(String field, String attribute) {
		super();
		this.field = field;
		this.attribute = attribute;
		this.sync = false;
		this.system = true;
		this.enable = true;
	}

	@SuppressWarnings("unused")
	private void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setField(String value) {
		this.field = value;
	}
	
	public String getField() {
		return field;
	}
	
	public void setAttribute(String value) {
		this.attribute = value;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public void setSync(boolean value) {
		setSync(new Boolean(value));
	}
	
	public void setSync(Boolean value) {
		this.sync = value;
	}
	
	public Boolean getSync() {
		return sync;
	}
	
	public void setSystem(boolean value) {
		setSystem(new Boolean(value));
	}
	
	public void setSystem(Boolean value) {
		this.system = value;
	}
	
	public Boolean getSystem() {
		return system;
	}
	
	public void setEnable(boolean value) {
		setEnable(new Boolean(value));
	}
	
	public void setEnable(Boolean value) {
		this.enable = value;
	}
	
	public Boolean getEnable() {
		return enable;
	}
	
}
