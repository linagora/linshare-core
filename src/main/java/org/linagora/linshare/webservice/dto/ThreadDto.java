package org.linagora.linshare.webservice.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;

@XmlRootElement(name = "Thread")
public class ThreadDto extends AccountDto {

	protected String name;
	
	protected List<ThreadMemberDto> members;

	public ThreadDto(Thread thread) {
		super(thread);
		this.name = thread.getName();
	}
	
	public ThreadDto(Thread thread, Set<ThreadMember> members) {
		super(thread);
		this.name = thread.getName();
		this.members = new ArrayList<ThreadMemberDto>();
		for (ThreadMember member : members) {
			this.members.add(new ThreadMemberDto(member));
		}
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

	public List<ThreadMemberDto> getMembers() {
		return members;
	}

	public void setMembers(List<ThreadMemberDto> members) {
		this.members = members;
	}
}
