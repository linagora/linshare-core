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
package org.linagora.linShare.core.Facade.impl;

import java.util.List;

import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.DomainTransformer;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.UserService;

public class DomainFacadeImpl implements DomainFacade {

	private DomainService domainService;
	private DomainTransformer domainTransformer;
	private UserService userService;

	public DomainFacadeImpl(DomainService domainService,
			DomainTransformer domainTransformer, 
			UserService userService) {
		this.domainService = domainService;
		this.domainTransformer = domainTransformer;
		this.userService = userService;
	}

	public DomainVo createDomain(DomainVo domainVo) throws BusinessException {
		return domainTransformer.disassemble(domainService
				.createDomain(domainVo));
	}

	public DomainPatternVo createDomainPattern(DomainPatternVo domainPatternVo)
			throws BusinessException {
		return new DomainPatternVo(
				domainService.createDomainPattern(domainPatternVo));
	}

	public LDAPConnectionVo createLDAPConnection(
			LDAPConnectionVo ldapConnectionVo) throws BusinessException {
		return new LDAPConnectionVo(
				domainService.createLDAPConnection(ldapConnectionVo));
	}

	public LDAPConnectionVo retrieveLDAPConnection(String identifier)
			throws BusinessException {
		return new LDAPConnectionVo(
				domainService.retrieveLDAPConnection(identifier));
	}

	public DomainVo retrieveDomain(String identifier) throws BusinessException {
		return domainTransformer.disassemble(domainService
				.retrieveDomain(identifier));
	}

	public DomainPatternVo retrieveDomainPattern(String identifier)
			throws BusinessException {
		return new DomainPatternVo(
				domainService.retrieveDomainPattern(identifier));
	}

	public void deleteDomain(String identifier) throws BusinessException {
		domainService.deleteDomain(identifier);
	}
	
	public List<String> getAllDomainIdentifiers() throws BusinessException {
		return domainService.getAllDomainIdentifiers();
	}
	
	public boolean userCanCreateGuest(UserVo userVo) throws BusinessException {
		User user = userService.findUser(userVo.getMail(), userVo.getDomainIdentifier());
		return domainService.userCanCreateGuest(user);
	}

}
