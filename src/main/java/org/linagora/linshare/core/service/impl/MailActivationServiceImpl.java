package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailActivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class MailActivationServiceImpl extends AbstractFunctionalityServiceImpl<MailActivation> implements MailActivationService {

	protected final Logger logger = LoggerFactory.getLogger(MailActivationServiceImpl.class);

	final private MailActivationBusinessService businessService;

	public MailActivationServiceImpl(
			DomainBusinessService domainBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService,
			MailActivationBusinessService mailActivationBusinessService) {
		super(domainBusinessService, domainPermissionBusinessService);
		this.businessService = mailActivationBusinessService;
	}

	@Override
	public List<MailActivation> findAll(Account actor, String domainId)
			throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		AbstractDomain domain = getDomain(actor, domainId);
		List<MailActivation> res = Lists.newArrayList();
		res.addAll(businessService.getAllFunctionalities(domain, excludes));
		return res;
	}

	@Override
	public MailActivation find(Account actor, String domainId, String mailActivationId)
			throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notEmpty(mailActivationId);
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		logger.debug("looking for mailActivation : " + mailActivationId + " in domain "+ domainId);
		AbstractDomain domain = getDomain(actor, domainId);
		return businessService.getFunctionality(domain, mailActivationId);
	}

	@Override
	public MailActivation update(Account actor, String domainId,
			MailActivation mailActivation) throws BusinessException {
		Validate.notNull(domainId);
		Validate.notNull(mailActivation.getIdentifier());
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		AbstractDomain domain = getDomain(actor, domainId);
		if (checkUpdateRights(actor, domain, mailActivation)) {
			businessService.update(domainId, mailActivation);
		}
		return businessService.getFunctionality(domain, mailActivation.getIdentifier());
	}
}
