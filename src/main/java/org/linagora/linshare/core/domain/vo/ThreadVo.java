package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;

public class ThreadVo extends AccountVo {
	
	private static final long serialVersionUID = 907135796857640950L;

	protected final String name;
	
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
	
	public ThreadViewVo getView() {
		return view;
	}

	public void setView(ThreadViewVo view) {
		this.view = view;
	}
}
