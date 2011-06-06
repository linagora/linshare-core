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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.DomainTransformer;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.core.service.UserService;

public class DomainFacadeImpl implements DomainFacade {

	private DomainService domainService;
	private DomainTransformer domainTransformer;
	private UserService userService;
	private LDAPQueryService ldapQueryService;

	public DomainFacadeImpl(DomainService domainService,
			DomainTransformer domainTransformer, 
			UserService userService,
			LDAPQueryService ldapQueryService) {
		this.domainService = domainService;
		this.domainTransformer = domainTransformer;
		this.userService = userService;
		this.ldapQueryService = ldapQueryService;
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

	public void deleteDomain(String identifier, UserVo actorVo) throws BusinessException {
		User actor = userService.findUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		if (actor.getRole() != Role.SUPERADMIN) {
			throw new BusinessException("Cannot delete domain because actor is not super admin");
		}
		Domain domain = domainService.retrieveDomain(identifier);
		List<User> users = ldapQueryService.getAllDomainUsers(domain, null);
		for (User user : users) {
			userService.deleteUser(user.getLogin(), actor, false);
		}
		users = userService.findUsersInDB(identifier);
		for (User user : users) {
			userService.deleteUser(user.getLogin(), actor, false);
		}
		domainService.deleteDomain(identifier);
	}
	
	public void deleteConnection(String connectionToDelete, UserVo actorVo)
			throws BusinessException {
		User actor = userService.findUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		if (actor.getRole() != Role.SUPERADMIN) {
			throw new BusinessException("Cannot delete connection because actor is not super admin");
		}
		
		if (!connectionIsDeletable(connectionToDelete, actorVo)) {
			throw new BusinessException("Cannot delete connection because still used by domains");
		}
		domainService.deleteConnection(connectionToDelete);
	}
	
	public void deletePattern(String patternToDelete, UserVo actorVo)
	throws BusinessException {
		User actor = userService.findUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		if (actor.getRole() != Role.SUPERADMIN) {
			throw new BusinessException("Cannot delete pattern because actor is not super admin");
		}
		if (!patternIsDeletable(patternToDelete, actorVo)) {
			throw new BusinessException("Cannot delete pattern because still used by domains");
		}
		domainService.deletePattern(patternToDelete);
	}
	
	public boolean connectionIsDeletable(String connectionToDelete, UserVo actor)
			throws BusinessException {
		List<DomainVo> domains = findAllDomains();
		boolean used = false;
		for (DomainVo domainVo : domains) {
			if (domainVo.getLdapConnection().getIdentifier().equals(connectionToDelete)) {
				used = true;
				break;
			}
		}
		return (!used);
	}
	
	public boolean patternIsDeletable(String patternToDelete, UserVo actor)
			throws BusinessException {
		List<DomainVo> domains = findAllDomains();
		boolean used = false;
		for (DomainVo domainVo : domains) {
			if (domainVo.getPattern().getIdentifier().equals(patternToDelete)) {
				used = true;
				break;
			}
		}
		return (!used);
	}
	
	public List<String> getAllDomainIdentifiers() throws BusinessException {
		return domainService.getAllDomainIdentifiers();
	}
	
	public boolean userCanCreateGuest(UserVo userVo) throws BusinessException {
		User user = userService.findUser(userVo.getMail(), userVo.getDomainIdentifier());
		return domainService.userCanCreateGuest(user);
	}
	
	public List<DomainVo> findAllDomains() throws BusinessException {
		return domainTransformer.disassembleList(domainService.findAllDomains());
	}
	
	public List<DomainPatternVo> findAllDomainPatterns()
			throws BusinessException {
		List<DomainPattern> domainPatterns = domainService.findAllDomainPatterns();
		List<DomainPatternVo> ret = new ArrayList<DomainPatternVo>();
		for (DomainPattern domainPattern : domainPatterns) {
			ret.add(new DomainPatternVo(domainPattern));
		}
		return ret;
	}
	
	public List<LDAPConnectionVo> findAllLDAPConnections()
			throws BusinessException {
		List<LDAPConnection> ldapConns = domainService.findAllLDAPConnections();
		List<LDAPConnectionVo> ret = new ArrayList<LDAPConnectionVo>();
		for (LDAPConnection ldapConn : ldapConns) {
			ret.add(new LDAPConnectionVo(ldapConn));
		}
		return ret;
	}
	
	public void updateLDAPConnection(LDAPConnectionVo ldapConn)
			throws BusinessException {
		domainService.updateLDAPConnection(new LDAPConnection(ldapConn));
	}
	
	public void updateDomainPattern(DomainPatternVo domainPattern)
			throws BusinessException {
		domainService.updateDomainPattern(new DomainPattern(domainPattern));
	}
	
	public void updateDomain(DomainVo domain) throws BusinessException {
		domainService.updateDomain(domain.getIdentifier(), domain.getDifferentialKey(), new LDAPConnection(domain.getLdapConnection()), new DomainPattern(domain.getPattern()));
	}

}
