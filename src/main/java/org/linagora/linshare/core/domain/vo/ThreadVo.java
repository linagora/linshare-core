package org.linagora.linshare.core.domain.vo;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;

public class ThreadVo extends AccountVo {
	
	private static final long serialVersionUID = 907135796857640950L;

	protected String name;
	
	@NonVisual
	protected ThreadViewVo view;


	// constructor just for test
	public ThreadVo(String uuid, String name) {
		super(uuid);
		this.name = name;
	}
	
	public ThreadVo(Account account, String name) {
		super(account);
		this.name = name;
	}
	
	public ThreadVo(Thread thread) {
		super(thread);
		this.name = thread.getName();
		this.view = new ThreadViewVo(thread.getCurrentThreadView());
	}

	public String getName() {
		return name;
	}

	@Validate("required")
	public void setName(String name) {
		this.name = name;
	}
	
	public ThreadViewVo getView() {
		return view;
	}

	public void setView(ThreadViewVo view) {
		this.view = view;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getLsUuid() == null) ? 0 : this.getLsUuid().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThreadVo other = (ThreadVo) obj;
		if (this.getLsUuid() == null) {
			if (other.getLsUuid() != null)
				return false;
		} else if (!this.getLsUuid().equals(other.getLsUuid()))
			return false;
		return true;
	}
	
}
