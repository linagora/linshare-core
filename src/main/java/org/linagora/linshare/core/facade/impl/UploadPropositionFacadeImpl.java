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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadPropositionFacadeImpl implements UploadPropositionFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFacadeImpl.class);

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
			if (e.isPending()) {
				ret.add(new UploadPropositionVo(e));
			}
		}
		return ret;
	}

	@Override
	public void accept(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException {
		logger.debug("actorVo: " + actorVo, ", prop: " + prop.getUuid());
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadProposition e = uploadPropositionService.find(actor,
				prop.getUuid());

		uploadPropositionService.accept(actor, e);
	}

	@Override
	public void reject(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException {
		logger.debug("actorVo: " + actorVo, ", prop: " + prop.getUuid());
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadProposition e = uploadPropositionService.find(actor,
				prop.getUuid());

		uploadPropositionService.reject(actor, e);
	}
}
