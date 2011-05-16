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
import org.linagora.linShare.core.domain.transformers.impl.DomainTransformer;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.DomainService;

public class DomainFacadeImpl implements DomainFacade {

	private DomainService domainService;
	private DomainTransformer domainTransformer;

	public DomainFacadeImpl(DomainService domainService,
			DomainTransformer domainTransformer) {
		this.domainService = domainService;
		this.domainTransformer = domainTransformer;
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

}
