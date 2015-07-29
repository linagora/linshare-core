package org.linagora.linshare.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailActivationAdminDto;

public interface MailActivationRestService {

	List<MailActivationAdminDto> findAll(String domainId)
			throws BusinessException;

	MailActivationAdminDto find(String domainId, String mailActivationId)
			throws BusinessException;

	MailActivationAdminDto update(MailActivationAdminDto mailActivation)
			throws BusinessException;

	void delete(MailActivationAdminDto mailActivation) throws BusinessException;

}
