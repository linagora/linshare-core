package org.linagora.linshare.core.domain.entities;

public class View {
	public View() {
	}
	
	private Long id;
	
	private Account owner;
	
	private ViewContext context;
	
	private String name;
	
	private boolean _public;
	
	private java.util.Set<ViewTagAsso> viewTagAsso = new java.util.HashSet<ViewTagAsso>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public ViewContext getContext() {
		return context;
	}

	public void setContext(ViewContext context) {
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean is_public() {
		return _public;
	}

	public void set_public(boolean _public) {
		this._public = _public;
	}

	public java.util.Set<ViewTagAsso> getViewTagAsso() {
		return viewTagAsso;
	}

	public void setViewTagAsso(java.util.Set<ViewTagAsso> viewTagAsso) {
		this.viewTagAsso = viewTagAsso;
	}
}

