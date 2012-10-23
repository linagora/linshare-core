package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.ThreadMember;

public class ThreadMemberVo implements Comparable<ThreadMemberVo> {
	
	private UserVo user;
	
	private boolean canUpload;
	
	private boolean admin;

	
	public ThreadMemberVo(ThreadMember threadMember) {
		super();
		this.user = new UserVo(threadMember.getUser());
		this.admin = threadMember.getAdmin();
		this.canUpload = this.admin || threadMember.getCanUpload();
	}

	public ThreadMemberVo(UserVo user, boolean canUpload, boolean admin) {
		super();
		this.user = user;
		this.canUpload = canUpload;
		this.admin = admin;
	}

	public UserVo getUser() {
		return user;
	}

	public void setUser(UserVo user) {
		this.user = user;
	}

	public boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public String getFormattedName() {
		return (admin ? "(@) " : canUpload ? "(+) " : "(-) ") + user.getFullName();
	}

	@Override
	public int compareTo(ThreadMemberVo o) {
		if (this.admin) {
			return o.admin ? this.user.compareTo(o.getUser()) : -1;
		}
		if (this.canUpload)
			return o.admin ? 1 : o.canUpload ? this.user.compareTo(o.getUser()) : -1;
		return o.admin || o.canUpload ? 1 : this.user.compareTo(o.getUser());
	}
}
