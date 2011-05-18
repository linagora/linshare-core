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
package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface DomainFacade {
	
	public DomainVo createDomain(DomainVo domainVo) throws BusinessException;
	public DomainPatternVo createDomainPattern(DomainPatternVo domainPatternVo) throws BusinessException;
	public LDAPConnectionVo createLDAPConnection(LDAPConnectionVo ldapConnectionVo) throws BusinessException;
	public LDAPConnectionVo retrieveLDAPConnection(String identifier) throws BusinessException;
	public DomainVo retrieveDomain(String identifier) throws BusinessException;
	public DomainPatternVo retrieveDomainPattern(String identifier) throws BusinessException;
	public void deleteDomain(String identifier) throws BusinessException;
	public List<String> getAllDomainIdentifiers() throws BusinessException;
	public boolean userCanCreateGuest(UserVo userVo) throws BusinessException;
	public List<DomainVo> findAllDomains() throws BusinessException;

}
