package org.linagora.linshare.core.facade.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UploadPropositionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UploadPropositionFacade;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class UploadPropositionFacadeImpl implements UploadPropositionFacade {

	private final UserService userService;
	private final UploadPropositionService uploadPropositionService;

	public UploadPropositionFacadeImpl(UserService userService,
			UploadPropositionService uploadPropositionService) {
		super();
		this.userService = userService;
		this.uploadPropositionService = uploadPropositionService;
	}

	@Override
	public List<UploadPropositionVo> findAllVisibles(UserVo actorVo)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		List<UploadProposition> res = uploadPropositionService.findAll(actor);
		List<UploadPropositionVo> ret = Lists.newArrayList();
		for (UploadProposition e : res) {
			ret.add(new UploadPropositionVo(e));
		}
		return ret;
	}
}
