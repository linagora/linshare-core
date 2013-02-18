package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Thread;

@XmlRootElement(name = "Thread")
public class ThreadDto extends AccountDto{

	protected String name;

	public ThreadDto(Thread t) {
		super(t);
		this.name = t.getName();
	}
	
	public ThreadDto() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
