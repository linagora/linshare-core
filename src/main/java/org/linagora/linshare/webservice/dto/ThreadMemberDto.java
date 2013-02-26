package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ThreadMember;

@XmlRootElement(name = "ThreadMember")
public class ThreadMemberDto {
	
	private Long id;
	
	private String role;
	
	private String firstName;
	
	private String lastName;
	
	private String userUuid;
	
	private String threadUuid;
	
	private static enum Roles {
		NORMAL,
		RESTRICTED,
		ADMIN;
	}
	
	public ThreadMemberDto() {
		super();
	}
	
	public ThreadMemberDto(ThreadMember member) {
		super();
		this.id = member.getId();
		this.firstName = member.getUser().getFirstName();
		this.lastName = member.getUser().getLastName();
		this.role = (member.getAdmin() ? Roles.ADMIN : member.getCanUpload() ?
				Roles.NORMAL : Roles.RESTRICTED).name().toLowerCase();
		this.userUuid = member.getUser().getLsUuid();
		this.threadUuid = member.getThread().getLsUuid();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getThreadUuid() {
		return threadUuid;
	}

	public void setThreadUuid(String threadUuid) {
		this.threadUuid = threadUuid;
	}
}
