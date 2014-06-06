/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MailContentBusinessService;
import org.linagora.linshare.core.business.service.MailFooterBusinessService;
import org.linagora.linshare.core.business.service.MailLayoutBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
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

	private final DomainPermissionBusinessService permissionService;

	public MailConfigServiceImpl(
			final AbstractDomainService abstractDomainService,
			final MailConfigBusinessService mailConfigBusinessService,
			final MailContentBusinessService mailContentBusinessService,
			final MailFooterBusinessService mailFooterBusinessService,
			final MailLayoutBusinessService mailLayoutBusinessService,
			final DomainPermissionBusinessService domainPermissionBusinessService) {
		super();
		this.abstractDomainService = abstractDomainService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.mailContentBusinessService = mailContentBusinessService;
		this.mailFooterBusinessService = mailFooterBusinessService;
		this.mailLayoutBusinessService = mailLayoutBusinessService;
		this.permissionService = domainPermissionBusinessService;
	}

	@Override
	public List<MailConfig> findAllConfigs(User actor, String domainId)
			throws BusinessException {
		List<MailConfig> configs = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getIdentifier().equals(actor.getDomainId())) {
				configs.addAll(d.getMailConfigs());
			} else {
				for (MailConfig c : d.getMailConfigs()) {
					if (c.isVisible()) {
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
	public MailConfig createConfig(User actor, MailConfig config)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail config in this domain "
					+ actor.getDomainId());
		return mailConfigBusinessService.create(config.getDomain(), config);
	}

	@Override
	public MailConfig updateConfig(User actor, MailConfig config)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		return mailConfigBusinessService.update(config);
	}

	@Override
	public void deleteConfig(User actor, String uuid) throws BusinessException {
		MailConfig config = mailConfigBusinessService.findByUuid(uuid);
		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.delete(config);
	}

	@Override
	public List<MailContent> findAllContents(User actor, String domainId)
			throws BusinessException {
		List<MailContent> contents = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getIdentifier().equals(actor.getDomainId())) {
				contents.addAll(d.getMailContents());
			} else {
				for (MailContent c : d.getMailContents()) {
					if (c.isVisible()) {
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
	public MailContent findContentFromDomain(User actor, String domainId,
			Language lang, MailContentType type) throws BusinessException {
		MailContent ret = mailContentBusinessService.find(domainId, lang, type);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail content : "
					+ ret.getUuid());
		return ret;
	}

	@Override
	public List<MailContent> findAllUsableContents(MailConfig mailConfig,
			MailContentType mailContentType, Language lang)
			throws BusinessException {

		AbstractDomain currentDomain = mailConfig.getDomain();
		List<MailContent> all = mailContentBusinessService.findAll(
				currentDomain, lang, mailContentType);
		AbstractDomain parent = currentDomain.getParentDomain();
		while (parent != null) {
			all.addAll(mailContentBusinessService.findAll(currentDomain, lang,
					mailContentType));
			parent = parent.getParentDomain();
		}
		return all;
	}

	@Override
	public MailContent createContent(User actor, MailContent content)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, content.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail content in this domain "
					+ actor.getDomainId());
		return mailContentBusinessService.create(content.getDomain(), content);
	}

	@Override
	public void updateContent(User actor, MailContent content)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, content.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail content in this domain "
					+ actor.getDomainId());
		mailContentBusinessService.update(content);
	}

	@Override
	public void deleteContent(User actor, String uuid) throws BusinessException {
		MailContent val = mailContentBusinessService.findByUuid(uuid);
		if (!permissionService.isAdminforThisDomain(actor, val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail content in this domain "
					+ actor.getDomainId());
		mailContentBusinessService.delete(val);
	}

	@Override
	public MailContentLang findContentLangByUuid(User actor, String uuid)
			throws BusinessException {
		MailContentLang ret = mailConfigBusinessService
				.findContentLangByUuid(uuid);

		if (ret != null && !hasRights(actor, ret.getMailConfig())) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : "
					+ ret.getMailConfig().getUuid());
		}
		return ret;
	}

	@Override
	public void createContentLang(User actor, MailContentLang contentLang)
			throws BusinessException {
		MailConfig config = contentLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.createContentLang(contentLang);
	}

	@Override
	public void updateContentLang(User actor, MailContentLang contentLang)
			throws BusinessException {
		MailConfig config = contentLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.updateContentLang(contentLang);
	}

	@Override
	public void deleteContentLang(User actor, String uuid)
			throws BusinessException {
		try {
			MailContentLang contentLang = findContentLangByUuid(actor, uuid);
			mailConfigBusinessService.deleteContentLang(contentLang);
		} catch (BusinessException e) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
	}

	@Override
	public List<MailFooter> findAllFooters(User actor, String domainId)
			throws BusinessException {
		List<MailFooter> footers = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getIdentifier().equals(actor.getDomainId())) {
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
	public List<MailFooter> findAllUsableFooters(MailConfig mailConfig,
			Language lang) {
		AbstractDomain currentDomain = mailConfig.getDomain();
		List<MailFooter> all = mailFooterBusinessService.findAll(currentDomain,
				lang);
		AbstractDomain parent = currentDomain.getParentDomain();
		while (parent != null) {
			all.addAll(mailFooterBusinessService.findAll(currentDomain, lang));
			parent = parent.getParentDomain();
		}
		return all;
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
	public MailFooter createFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, footer.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail footer in this domain "
					+ actor.getDomainId());
		return mailFooterBusinessService.create(footer.getDomain(), footer);
	}

	@Override
	public void updateFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, footer.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail footer in this domain "
					+ actor.getDomainId());
		mailFooterBusinessService.update(footer);
	}

	@Override
	public void deleteFooter(User actor, String uuid) throws BusinessException {
		MailFooter val = mailFooterBusinessService.findByUuid(uuid);

		if (!permissionService.isAdminforThisDomain(actor, val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail footer in this domain "
					+ actor.getDomainId());
		mailFooterBusinessService.delete(val);
	}

	@Override
	public MailFooterLang findFooterLangByUuid(User actor, String uuid)
			throws BusinessException {
		MailFooterLang ret = mailConfigBusinessService
				.findFooterLangByUuid(uuid);

		if (ret != null && !hasRights(actor, ret.getMailConfig())) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : "
					+ ret.getMailConfig().getUuid());
		}
		return ret;
	}

	@Override
	public void createFooterLang(User actor, MailFooterLang footerLang)
			throws BusinessException {
		MailConfig config = footerLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.createFooterLang(footerLang);
	}

	@Override
	public void updateFooterLang(User actor, MailFooterLang footerLang)
			throws BusinessException {
		MailConfig config = footerLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		mailConfigBusinessService.updateFooterLang(footerLang);
	}

	@Override
	public void deleteFooterLang(User actor, String uuid)
			throws BusinessException {
		try {
			MailFooterLang footerLang = findFooterLangByUuid(actor, uuid);
			mailConfigBusinessService.deleteFooterLang(footerLang);
		} catch (BusinessException e) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
	}

	@Override
	public List<MailLayout> findAllLayouts(User actor, String domainId)
			throws BusinessException {
		List<MailLayout> layouts = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getIdentifier().equals(actor.getDomainId())) {
				layouts.addAll(d.getMailLayouts());
			} else {
				for (MailLayout c : d.getMailLayouts()) {
					if (c.isVisible()) {
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
	public MailLayout createLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, layout.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot create a mail layout in this domain "
					+ actor.getDomainId());
		return mailLayoutBusinessService.create(layout.getDomain(), layout);
	}

	@Override
	public void updateLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, layout.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail layout in this domain "
					+ actor.getDomainId());
		mailLayoutBusinessService.update(layout);
	}

	@Override
	public void deleteLayout(User actor, String uuid) throws BusinessException {
		MailLayout val = mailLayoutBusinessService.findByUuid(uuid);

		if (!permissionService.isAdminforThisDomain(actor, val.getDomain()))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail layout in this domain "
					+ actor.getDomainId());
		mailLayoutBusinessService.delete(val);
	}

	@Override
	public boolean hasRights(User actor, MailConfig config) {
		// if (!permissionService.isAdminforThisDomain(actor,
		// config.getDomain())) {
		// if (config.isVisible()) {
		// return isInParentDomains(actor.getDomain(), config.getDomain());
		// } else {
		// return actor.getDomain().equals(config.getDomain());
		// }
		// }
		return true;
	}

	@Override
	public boolean hasRights(User actor, MailContent content) {
		return true;
		// if (content.isVisible()) {
		// return isInParentDomains(actor.getDomain(), content.getDomain());
		// } else {
		// return actor.getDomain().equals(content.getDomain());
		// }
	}

	@Override
	public boolean hasRights(User actor, MailFooter footer) {
		return true;
		// if (footer.getVisible()) {
		// return isInParentDomains(actor.getDomain(), footer.getDomain());
		// } else {
		// return actor.getDomain().equals(footer.getDomain());
		// }
	}

	@Override
	public boolean hasRights(User actor, MailLayout layout) {
		return true;
		// if (layout.isVisible()) {
		// return isInParentDomains(actor.getDomain(), layout.getDomain());
		// } else {
		// return actor.getDomain().equals(layout.getDomain());
		// }
	}

	/*
	 * Helpers
	 */

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
