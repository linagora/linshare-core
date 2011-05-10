package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface DomainService {
	
	public Domain createDomain(DomainVo domainVo) throws BusinessException;
	public DomainPattern createDomainPattern(DomainPatternVo domainPatternVo) throws BusinessException;
	public LDAPConnection createLDAPConnection(LDAPConnectionVo ldapConnectionVo) throws BusinessException;
	public LDAPConnection retrieveLDAPConnection(String identifier) throws BusinessException;
	public Domain retrieveDomain(String identifier) throws BusinessException;
	public DomainPattern retrieveDomainPattern(String identifier) throws BusinessException;
	public void deleteDomain(String identifier) throws BusinessException;

}
