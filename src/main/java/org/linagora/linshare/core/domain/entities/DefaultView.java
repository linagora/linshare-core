package org.linagora.linshare.core.domain.entities;

public class DefaultView {
	
	public DefaultView() {
	}
	
	public DefaultView(String identifier, View view, ViewContext viewContext) {
		super();
		this.identifier = identifier;
		this.view = view;
		this.viewContext = viewContext;
	}

	private long id;
	
	private String identifier;
	
	private View view;
	
	private ViewContext viewContext;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public ViewContext getViewContext() {
		return viewContext;
	}

	public void setViewContext(ViewContext viewContext) {
		this.viewContext = viewContext;
	}
}
