/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.domain.vo;

import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class ThreadVo extends AccountVo {
	
	private static final long serialVersionUID = 907135796857640950L;

	protected String name;
	
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
	}

	public String getName() {
		return name;
	}

	@Validate("required")
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getLsUuid() == null) ? 0 :
			this.getLsUuid().hashCode());
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
	
	/*
	 * Transformers
	 */
	public static Function<Thread, ThreadVo> toVo() {
		return new Function<Thread, ThreadVo>() {
			@Override
			public ThreadVo apply(Thread arg0) {
				return new ThreadVo(arg0);
			}
		};
	}
	
	/*
	 * Filters
	 */
	public static Predicate<ThreadVo> equalTo(final String uuid) {
		return Predicates.equalTo(new ThreadVo(uuid, ""));
	}
}
