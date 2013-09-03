package org.linagora.linshare.core.facade.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public interface ThreadFacade extends AdminGenericFacade {

	public List<ThreadDto> getAll();

	public ThreadDto get(String uuid);

	public List<ThreadMemberDto> getMembers(String uuid);

	public void addMember(String uuid, ThreadMemberDto member)
			throws BusinessException;

	public void delete(String uuid) throws BusinessException;
}