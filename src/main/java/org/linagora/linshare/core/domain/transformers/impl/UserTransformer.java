/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.domain.transformers.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.UserType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.Transformer;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.GuestRepository;

public class UserTransformer implements Transformer<User, UserVo> {

	private final GuestRepository guestRepository;
	
	
	public UserTransformer(GuestRepository guestRepository) {
		super();
		this.guestRepository = guestRepository;
	}

	public User assemble(UserVo valueObject) {
		throw new TechnicalException("Should not be used");
		
	}

	public List<User> assembleList(List<UserVo> valueObjectList) {
		throw new TechnicalException("Should not be used");
	}

	public UserVo disassemble(User entityObject) {
		if (UserType.GUEST.equals(entityObject.getAccountType())) {
			return new UserVo(guestRepository.findByMail(entityObject.getMail()));
		} else  {
			return new UserVo(entityObject);
		}
	}

	public List<UserVo> disassembleList(List<User> entityObjectList) {
		throw new TechnicalException("Should not be used");
	}

	
}
