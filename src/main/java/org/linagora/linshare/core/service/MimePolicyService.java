package org.linagora.linshare.core.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessException;

public interface MimePolicyService {

	MimePolicy create(Account actor, String domainId, MimePolicy mimePolicy) throws BusinessException;

	void delete(Account actor, MimePolicy mimePolicy) throws BusinessException;

	MimePolicy find(Account actor, String uuid, boolean full) throws BusinessException;

	Set<MimeType> findAllMyMimeTypes(Account actor) throws BusinessException;

	Set<MimePolicy> findAll(Account actor, String domainIdentifier, boolean onlyCurrentDomain) throws BusinessException;

	MimePolicy update(Account actor, MimePolicy mimePolicyDto) throws BusinessException;

	public MimePolicy enableAllMimeTypes(Account actor, String uuid) throws BusinessException;

	public MimePolicy disableAllMimeTypes(Account actor, String uuid) throws BusinessException;
}
