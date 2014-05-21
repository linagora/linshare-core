package org.linagora.linshare.core.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.exception.BusinessException;

public interface MimePolicyService {

	MimePolicy create(Account actor, MimePolicy mimePolicy) throws BusinessException;

	MimePolicy delete(Account actor, MimePolicy mimePolicy) throws BusinessException;

	MimePolicy find(Account actor, String uuid) throws BusinessException;

	Set<MimePolicy> findAllEditable(Account actor) throws BusinessException;

	Set<MimePolicy> findAllUsable(Account actor) throws BusinessException;

	MimePolicy update(Account actor, MimePolicy mimePolicyDto) throws BusinessException;
}
