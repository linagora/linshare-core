package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MailContentBusinessService;
import org.linagora.linshare.core.business.service.MailFooterBusinessService;
import org.linagora.linshare.core.business.service.MailLayoutBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentType;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.MailConfigService;

import com.google.common.collect.Lists;

public class MailConfigServiceImpl implements MailConfigService {

	private final AbstractDomainService abstractDomainService;

	private final MailConfigBusinessService mailConfigBusinessService;

	private final MailContentBusinessService mailContentBusinessService;

	private final MailFooterBusinessService mailFooterBusinessService;

	private final MailLayoutBusinessService mailLayoutBusinessService;

	public MailConfigServiceImpl(
			final AbstractDomainService abstractDomainService,
			final MailConfigBusinessService mailConfigBusinessService,
			final MailContentBusinessService mailContentBusinessService,
			final MailFooterBusinessService mailFooterBusinessService,
			final MailLayoutBusinessService mailLayoutBusinessService) {
		super();
		this.abstractDomainService = abstractDomainService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.mailContentBusinessService = mailContentBusinessService;
		this.mailFooterBusinessService = mailFooterBusinessService;
		this.mailLayoutBusinessService = mailLayoutBusinessService;
	}

	@Override
	public List<MailConfig> findAllConfigs(User actor, String domainId)
			throws BusinessException {
		List<MailConfig> configs = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.equals(actor.getDomainId())) {
				configs.addAll(d.getMailConfigs());
			} else {
				for (MailConfig c : d.getMailConfigs()) {
					if (c.getVisible()) {
						configs.add(c);
					}
				}
			}
		}
		return configs;
	}

	@Override
	public MailConfig findConfigByUuid(User actor, String uuid)
			throws BusinessException {
		MailConfig ret = mailConfigBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : " + ret.getUuid());
		return ret;
	}

	@Override
	public void createConfig(User actor, MailConfig config)
			throws BusinessException {
		if (!actor.getDomain().equals(config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.create(config.getDomain(), config);
	}

	@Override
	public void updateConfig(User actor, MailConfig config)
			throws BusinessException {
		if (!actor.getDomain().equals(config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.update(config);
	}

	@Override
	public void deleteConfig(User actor, String uuid) throws BusinessException {
		MailConfig val = mailConfigBusinessService.findByUuid(uuid);

		if (!actor.getDomain().equals(val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.delete(val);
	}

	@Override
	public List<MailContent> findAllContents(User actor, String domainId)
			throws BusinessException {
		List<MailContent> contents = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.equals(actor.getDomainId())) {
				contents.addAll(d.getMailContents());
			} else {
				for (MailContent c : d.getMailContents()) {
					if (c.getVisible()) {
						contents.add(c);
					}
				}
			}
		}
		return contents;
	}

	@Override
	public MailContent findContentByUuid(User actor, String uuid)
			throws BusinessException {
		MailContent ret = mailContentBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail content : "
					+ ret.getUuid());
		return ret;
	}

	@Override
	public MailContent findContentFromDomain(User actor, String domainId, Language lang,
			MailContentType type) throws BusinessException {
		MailContent ret = mailContentBusinessService.find(domainId, lang, type);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail content : "
					+ ret.getUuid());
		return ret;
	}

	@Override
	public void createContent(User actor, MailContent content)
			throws BusinessException {
		if (!actor.getDomain().equals(content.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail content in this domain "
					+ actor.getDomainId());
		mailContentBusinessService.create(content.getDomain(), content);
	}

	@Override
	public void updateContent(User actor, MailContent content)
			throws BusinessException {
		if (!actor.getDomain().equals(content.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail content in this domain "
					+ actor.getDomainId());
		mailContentBusinessService.update(content);
	}

	@Override
	public void deleteContent(User actor, String uuid) throws BusinessException {
		MailContent val = mailContentBusinessService.findByUuid(uuid);

		if (!actor.getDomain().equals(val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail content in this domain "
					+ actor.getDomainId());
		mailContentBusinessService.delete(val);
	}

	@Override
	public List<MailFooter> findAllFooters(User actor, String domainId)
			throws BusinessException {
		List<MailFooter> footers = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.equals(actor.getDomainId())) {
				footers.addAll(d.getMailFooters());
			} else {
				for (MailFooter c : d.getMailFooters()) {
					if (c.getVisible()) {
						footers.add(c);
					}
				}
			}
		}
		return footers;
	}

	@Override
	public MailFooter findFooterByUuid(User actor, String uuid)
			throws BusinessException {
		MailFooter ret = mailFooterBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail footer : " + ret.getUuid());
		return ret;
	}

	@Override
	public void createFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (!actor.getDomain().equals(footer.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail footer in this domain "
					+ actor.getDomainId());
		mailFooterBusinessService.create(footer.getDomain(), footer);
	}

	@Override
	public void updateFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (!actor.getDomain().equals(footer.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail footer in this domain "
					+ actor.getDomainId());
		mailFooterBusinessService.update(footer);
	}

	@Override
	public void deleteFooter(User actor, String uuid) throws BusinessException {
		MailFooter val = mailFooterBusinessService.findByUuid(uuid);

		if (!actor.getDomain().equals(val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail footer in this domain "
					+ actor.getDomainId());
		mailFooterBusinessService.delete(val);
	}

	@Override
	public List<MailLayout> findAllLayouts(User actor, String domainId)
			throws BusinessException {
		List<MailLayout> layouts = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.equals(actor.getDomainId())) {
				layouts.addAll(d.getMailLayouts());
			} else {
				for (MailLayout c : d.getMailLayouts()) {
					if (c.getVisible()) {
						layouts.add(c);
					}
				}
			}
		}
		return layouts;
	}

	@Override
	public MailLayout findLayoutByUuid(User actor, String uuid)
			throws BusinessException {
		MailLayout ret = mailLayoutBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail layout : " + ret.getUuid());
		return ret;
	}

	@Override
	public void createLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (!actor.getDomain().equals(layout.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail layout in this domain "
					+ actor.getDomainId());
		mailLayoutBusinessService.create(null, layout);
	}

	@Override
	public void updateLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (!actor.getDomain().equals(layout.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail layout in this domain "
					+ actor.getDomainId());
		mailLayoutBusinessService.update(layout);
	}

	@Override
	public void deleteLayout(User actor, String uuid) throws BusinessException {
		MailLayout val = mailLayoutBusinessService.findByUuid(uuid);

		if (!actor.getDomain().equals(val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail layout in this domain "
					+ actor.getDomainId());
		mailLayoutBusinessService.delete(val);
	}

	@Override
	public boolean hasRights(User actor, MailConfig config) {
		if (config.getVisible()) {
			return isInParentDomains(actor.getDomain(), config.getDomain());
		} else {
			return actor.getDomain().equals(config.getDomain());
		}
	}

	@Override
	public boolean hasRights(User actor, MailContent content) {
		if (content.getVisible()) {
			return isInParentDomains(actor.getDomain(), content.getDomain());
		} else {
			return actor.getDomain().equals(content.getDomain());
		}
	}

	@Override
	public boolean hasRights(User actor, MailFooter footer) {
		if (footer.getVisible()) {
			return isInParentDomains(actor.getDomain(), footer.getDomain());
		} else {
			return actor.getDomain().equals(footer.getDomain());
		}
	}

	@Override
	public boolean hasRights(User actor, MailLayout layout) {
		if (layout.getVisible()) {
			return isInParentDomains(actor.getDomain(), layout.getDomain());
		} else {
			return actor.getDomain().equals(layout.getDomain());
		}
	}

	private List<AbstractDomain> getParentDomains(String id)
			throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(id);

		if (domain == null)
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXISTS,
					"Invalid domain identifier.");
		return getParentDomains(domain);
	}

	private List<AbstractDomain> getParentDomains(AbstractDomain src) {
		List<AbstractDomain> parents = Lists.newArrayList();
		AbstractDomain parent = src;

		while (parent != null) {
			parents.add(parent);
			parent = parent.getParentDomain();
		}
		return parents;
	}

	private boolean isInParentDomains(AbstractDomain haystack,
			AbstractDomain needle) {
		return getParentDomains(haystack).contains(needle);
	}
}
