/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.notifications.context;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ThreadMember;

public class WorkGroupWarnUpdatedMemberEmailContext extends EmailContext{

	protected ThreadMember threadMember;

	protected Account owner;

	public WorkGroupWarnUpdatedMemberEmailContext(ThreadMember threadMember, Account owner) {
		super(threadMember.getUser().getDomain(), false);
		this.threadMember = threadMember;
		this.owner = owner;
	}

	public ThreadMember getThreadMember() {
		return threadMember;
	}

	public void setThreadMember(ThreadMember threadMember) {
		this.threadMember = threadMember;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORKGROUP_WARN_UPDATED_MEMBER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORKGROUP_WARN_UPDATED_MEMBER;
	}

	@Override
	public String getMailRcpt() {
		return threadMember.getUser().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return owner.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(threadMember, "Missing threadMember");
		Validate.notNull(owner, "Missing owner");
	}

}
