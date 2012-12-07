package org.linagora.linshare.core.domain.entities;

public class ViewContext {
	public ViewContext() {
	}
	
	private Long id;
	
	private String name;
	
	private String description;
	
	private DefaultView defaultView;
	
	private java.util.Set<View> views = new java.util.HashSet<View>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DefaultView getDefaultView() {
		return defaultView;
	}

	public void setDefaultView(DefaultView defaultView) {
		this.defaultView = defaultView;
	}

	public java.util.Set<View> getViews() {
		return views;
	}

	public void setViews(java.util.Set<View> views) {
		this.views = views;
	}
	
}
