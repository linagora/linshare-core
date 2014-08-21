package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.vo.UploadPropositionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadPropositionFacade {

	List<UploadPropositionVo> findAllVisibles(UserVo actorVo)
			throws BusinessException;

	void accept(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException;

	void reject(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException;
}
