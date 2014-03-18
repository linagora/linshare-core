package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailConfigRepository;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MailConfigBusinessServiceImpl implements MailConfigBusinessService {

	private final MailConfigRepository mailConfigRepository;
	private final AbstractDomainRepository abstractDomainRepository;

	public MailConfigBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailConfigRepository mailConfigRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailConfigRepository = mailConfigRepository;
	}

	@Override
	public MailConfig findByUuid(String uuid) {
		return mailConfigRepository.findByUuid(uuid);
	}

	@Override
	public void create(MailConfig cfg) throws BusinessException {
		MailConfig rootCfg = abstractDomainRepository.getUniqueRootDomain()
				.getCurrentMailConfiguration();

		// copy root domain's mailconfig
		cfg.setMailContents(Sets.newHashSet(rootCfg.getMailContents()));
		cfg.setMailFooters(Maps.newHashMap(rootCfg.getMailFooters()));
		cfg.setMailLayoutHtml(rootCfg.getMailLayoutHtml());
		cfg.setMailLayoutText(rootCfg.getMailLayoutText());

		mailConfigRepository.create(cfg);
	}

	@Override
	public void update(MailConfig cfg) throws BusinessException {
		mailConfigRepository.update(cfg);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		this.delete(findByUuid(uuid));
	}

	@Override
	public void delete(MailConfig cfg) throws BusinessException {
		// TODO : check usage of this cfg by domains
		
		mailConfigRepository.delete(cfg);
	}
}
