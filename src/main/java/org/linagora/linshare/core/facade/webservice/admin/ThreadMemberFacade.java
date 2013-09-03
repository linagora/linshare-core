package org.linagora.linshare.core.facade.webservice.admin;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public interface ThreadMemberFacade extends AdminGenericFacade {
	
	public ThreadMemberDto get(Long id) throws BusinessException;

	public void update(ThreadMemberDto dto) throws BusinessException;

	public void delete(ThreadMemberDto dto) throws BusinessException;
}
