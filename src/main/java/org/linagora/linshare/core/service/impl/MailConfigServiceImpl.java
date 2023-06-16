/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service.impl;

import static org.linagora.linshare.core.exception.BusinessErrorCode.DOMAIN_FORBIDDEN;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MailContentBusinessService;
import org.linagora.linshare.core.business.service.MailFooterBusinessService;
import org.linagora.linshare.core.business.service.MailLayoutBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class MailConfigServiceImpl implements MailConfigService {

	private final DomainBusinessService domainBusinessService;

	private final MailConfigBusinessService mailConfigBusinessService;

	private final MailContentBusinessService mailContentBusinessService;

	private final MailFooterBusinessService mailFooterBusinessService;

	private final MailLayoutBusinessService mailLayoutBusinessService;

	private final DomainPermissionBusinessService permissionService;

	private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	protected final boolean templatingOverrideReadonlyMode;

	protected static Logger logger = LoggerFactory.getLogger(MailConfigServiceImpl.class);

	public MailConfigServiceImpl(
			final DomainBusinessService domainBusinessService,
			final MailConfigBusinessService mailConfigBusinessService,
			final MailContentBusinessService mailContentBusinessService,
			final MailFooterBusinessService mailFooterBusinessService,
			final MailLayoutBusinessService mailLayoutBusinessService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			boolean templatingReadonlyMode,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService
			) {
		super();
		this.domainBusinessService = domainBusinessService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.mailContentBusinessService = mailContentBusinessService;
		this.mailFooterBusinessService = mailFooterBusinessService;
		this.mailLayoutBusinessService = mailLayoutBusinessService;
		this.permissionService = domainPermissionBusinessService;
		this.templatingOverrideReadonlyMode = templatingReadonlyMode;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
	}

	@Override
	public List<MailConfig> findAllConfigs(User actor, String domainId)
			throws BusinessException {
		return getParentDomains(domainId).stream()
				.flatMap(domain -> domain.getMailConfigs().stream())
				.filter(config -> config.isVisible()
						|| StringUtils.equals(domainId, config.getDomain().getUuid()))
				.collect(Collectors.toList());
	}

	@Override
	public MailConfig findConfigByUuid(User actor, String uuid)
			throws BusinessException {
		MailConfig ret = mailConfigBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : " + ret.getUuid());
		return ret;
	}

	@Override
	public MailConfig createConfig(User actor, MailConfig config)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_FORBIDDEN, "Actor "
					+ actor + " cannot create a mail config in this domain "
					+ actor.getDomainId());
		config.setName(sanitizerInputHtmlBusinessService.strictClean(config.getName()));
		return mailConfigBusinessService.create(config.getDomain(), config);
	}

	@Override
	public MailConfig updateConfig(User actor, MailConfig config)
			throws BusinessException {
		if (isForbidden(actor, config.getDomain(), config.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
		config.setName(sanitizerInputHtmlBusinessService.strictClean(config.getName()));
		return mailConfigBusinessService.update(config);
	}

	@Override
	public MailConfig deleteConfig(User actor, String uuid) throws BusinessException {
		MailConfig config = mailConfigBusinessService.findByUuid(uuid);
		if (isForbidden(actor, config.getDomain(), config.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail config in this domain "
					+ actor.getDomainId());
		}
		mailConfigBusinessService.delete(config);
		return config;
	}

	@Override
	public List<MailContent> findAllContents(User actor, String domainId)
			throws BusinessException {
		List<MailContent> contents = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getUuid().equals(actor.getDomainId())) {
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
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail content : "
					+ ret.getUuid());
		return ret;
	}

	@Override
	public MailContent findContentFromDomain(User actor, String domainId,
			Language lang, MailContentType type) throws BusinessException {
		MailContent ret = mailContentBusinessService.find(domainId, lang, type);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail content : "
					+ ret.getUuid());
		return ret;
	}

	@Override
	public MailContent createContent(User actor, MailContent content)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, content.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_FORBIDDEN, "Actor "
					+ actor + " cannot create a mail content in this domain "
					+ actor.getDomainId());
		content.setDescription(sanitizerInputHtmlBusinessService.strictClean(content.getDescription()));
		return mailContentBusinessService.create(content.getDomain(), content);
	}

	@Override
	public MailContent updateContent(User actor, MailContent content)
			throws BusinessException {
		if (isForbidden(actor, content.getDomain(), content.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail content in this domain "
					+ actor.getDomainId());
		}
		content.setDescription(sanitizerInputHtmlBusinessService.strictClean(content.getDescription()));
		return mailContentBusinessService.update(content);
	}

	@Override
	public MailContent deleteContent(User actor, String uuid) throws BusinessException {
		MailContent val = mailContentBusinessService.findByUuid(uuid);
		if (isForbidden(actor, val.getDomain(), val.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail content in this domain "
					+ actor.getDomainId());
		}
		mailContentBusinessService.delete(val);
		return val;
	}

	@Override
	public MailContentLang findContentLangByUuid(User actor, String uuid)
			throws BusinessException {
		MailContentLang ret = mailConfigBusinessService
				.findContentLangByUuid(uuid);

		if (ret != null && !hasRights(actor, ret.getMailConfig())) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENTLANG_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : "
					+ ret.getMailConfig().getUuid());
		}
		return ret;
	}

	@Override
	public MailContentLang createContentLang(User actor, MailContentLang contentLang)
			throws BusinessException {
		MailConfig config = contentLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILCONTENTLANG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		return mailConfigBusinessService.createContentLang(contentLang);
	}

	@Override
	public MailContentLang updateContentLang(User actor, MailContentLang contentLang)
			throws BusinessException {
		if (isForbidden(actor, contentLang.getMailConfig().getDomain(), contentLang.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
		return mailConfigBusinessService.updateContentLang(contentLang);
	}

	@Override
	public void deleteContentLang(User actor, String uuid)
			throws BusinessException {
		MailContentLang contentLang = findContentLangByUuid(actor, uuid);
		if (isForbidden(actor, contentLang.getMailConfig().getDomain(), contentLang.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENTLANG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
		mailConfigBusinessService.deleteContentLang(contentLang);
	}

	@Override
	public List<MailFooter> findAllFooters(User actor, String domainId)
			throws BusinessException {
		List<MailFooter> footers = Lists.newArrayList();

		for (AbstractDomain d : getParentDomains(domainId)) {
			if (d.getUuid().equals(actor.getDomainId())) {
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
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail footer : " + ret.getUuid());
		return ret;
	}

	@Override
	public MailFooter createFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, footer.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_FORBIDDEN, "Actor "
					+ actor + " cannot create a mail footer in this domain "
					+ actor.getDomainId());
		footer.setDescription(sanitizerInputHtmlBusinessService.strictClean(footer.getDescription()));
		return mailFooterBusinessService.create(footer.getDomain(), footer);
	}

	@Override
	public MailFooter updateFooter(User actor, MailFooter footer)
			throws BusinessException {
		if (isForbidden(actor, footer.getDomain(), footer.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail footer in this domain "
					+ actor.getDomainId());
		}
		footer.setDescription(sanitizerInputHtmlBusinessService.strictClean(footer.getDescription()));
		return mailFooterBusinessService.update(footer);
	}

	@Override
	public MailFooter deleteFooter(User actor, String uuid) throws BusinessException {
		MailFooter footer = mailFooterBusinessService.findByUuid(uuid);
		if (isForbidden(actor, footer.getDomain(), footer.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail footer in this domain "
					+ actor.getDomainId());
		}
		mailFooterBusinessService.delete(footer);
		return footer;
	}

	@Override
	public MailFooterLang findFooterLangByUuid(User actor, String uuid)
			throws BusinessException {
		MailFooterLang ret = mailConfigBusinessService
				.findFooterLangByUuid(uuid);

		if (ret != null && !hasRights(actor, ret.getMailConfig())) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTERLANG_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail config : "
					+ ret.getMailConfig().getUuid());
		}
		return ret;
	}

	@Override
	public MailFooterLang createFooterLang(User actor, MailFooterLang footerLang)
			throws BusinessException {
		MailConfig config = footerLang.getMailConfig();

		if (!permissionService.isAdminforThisDomain(actor, config.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILFOOTERLANG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		return mailConfigBusinessService.createFooterLang(footerLang);
	}

	@Override
	public MailFooterLang updateFooterLang(User actor, MailFooterLang footerLang)
			throws BusinessException {
		if (isForbidden(actor, footerLang.getMailConfig().getDomain(), footerLang.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTERLANG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
		return mailConfigBusinessService.updateFooterLang(footerLang);
	}

	@Override
	public void deleteFooterLang(User actor, String uuid)
			throws BusinessException {
		MailFooterLang footerLang = findFooterLangByUuid(actor, uuid);
		if (isForbidden(actor, footerLang.getMailConfig().getDomain(), footerLang.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTERLANG_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail config in this domain "
					+ actor.getDomainId());
		}
		mailConfigBusinessService.deleteFooterLang(footerLang);
	}

	@Override
	public List<MailLayout> findAllLayouts(User actor, String domainId)
			throws BusinessException {
		return getParentDomains(domainId).stream()
				.flatMap(domain -> domain.getMailLayouts().stream())
				.filter(config -> config.isVisible()
						|| StringUtils.equals(domainId, config.getDomain().getUuid()))
				.collect(Collectors.toList());
	}

	@Override
	public MailLayout findLayoutByUuid(User actor, String uuid)
			throws BusinessException {
		MailLayout ret = mailLayoutBusinessService.findByUuid(uuid);

		if (ret != null && !hasRights(actor, ret))
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_FORBIDDEN, "Actor "
					+ actor + " cannot see this mail layout : " + ret.getUuid());
		return ret;
	}

	@Override
	public MailLayout createLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (!permissionService.isAdminforThisDomain(actor, layout.getDomain()))
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_FORBIDDEN, "Actor "
					+ actor + " cannot create a mail layout in this domain "
					+ actor.getDomainId());
		layout.setDescription(sanitizerInputHtmlBusinessService.strictClean(layout.getDescription()));
		return mailLayoutBusinessService.create(layout.getDomain(), layout);
	}

	@Override
	public MailLayout updateLayout(User actor, MailLayout layout)
			throws BusinessException {
		if (isForbidden(actor, layout.getDomain(), layout.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_FORBIDDEN, "Actor "
					+ actor + " cannot update a mail layout in this domain "
					+ actor.getDomainId());
		}
		String strPattern = "<div data-th-insert=\"copyright :: copyright\">";
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(layout.getLayout());
		if(!matcher.find()) {
			logger.error("Missing copyright footer : {}", strPattern);
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_DO_NOT_REMOVE_COPYRIGHT_FOOTER, "You do not have the right to remove copyright footer.");
		}
		layout.setDescription(sanitizerInputHtmlBusinessService.strictClean(layout.getDescription()));
		return mailLayoutBusinessService.update(layout);
	}

	@Override
	public MailLayout deleteLayout(User actor, String uuid) throws BusinessException {
		MailLayout layout = mailLayoutBusinessService.findByUuid(uuid);
		if (isForbidden(actor, layout.getDomain(), layout.isReadonly())) {
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_FORBIDDEN, "Actor "
					+ actor + " cannot delete a mail layout in this domain "
					+ actor.getDomainId());
		}
		mailLayoutBusinessService.delete(layout);
		return layout;
	}

	@Override
	public boolean hasRights(User actor, MailConfig config) {
//		if (!permissionService.isAdminforThisDomain(actor, config.getDomain())) {
//			if (config.isVisible()) {
//				return isInParentDomains(actor.getDomain(), config.getDomain());
//			} else {
//				return actor.getDomain().equals(config.getDomain());
//			}
//		}
		return true;
	}

	@Override
	public boolean hasRights(User actor, MailContent content) {
		return true;
//		if (content.isVisible()) {
//			return isInParentDomains(actor.getDomain(), content.getDomain());
//		} else {
//			return actor.getDomain().equals(content.getDomain());
//		}
	}

	@Override
	public boolean hasRights(User actor, MailFooter footer) {
		return true;
//		if (footer.getVisible()) {
//			return isInParentDomains(actor.getDomain(), footer.getDomain());
//		} else {
//			return actor.getDomain().equals(footer.getDomain());
//		}
	}

	@Override
	public boolean hasRights(User actor, MailLayout layout) {
		return true;
//		if (layout.isVisible()) {
//			return isInParentDomains(actor.getDomain(), layout.getDomain());
//		} else {
//			return actor.getDomain().equals(layout.getDomain());
//		}
	}

	@Override
	public boolean isTemplatingOverrideReadonlyMode() {
		return this.templatingOverrideReadonlyMode;
	}

	/*
	 * Helpers
	 */

	private List<AbstractDomain> getParentDomains(String id)
			throws BusinessException {
		AbstractDomain domain = domainBusinessService.find(id);
		if (domain == null)
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
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

	private boolean isForbidden(User actor, AbstractDomain domain, boolean readonly) {
		boolean forbidden = false;
		if (readonly) {
			if (!templatingOverrideReadonlyMode) {
				forbidden = true;
			}
		}
		if (!permissionService.isAdminforThisDomain(actor, domain)) {
			forbidden = true;
		}
		return forbidden;
	}

	@Override
	public void assign(Account actor, String domainUuid, String mailConfigUuid) {
		Validate.notEmpty(domainUuid, "Domain uuid must be set.");
		Validate.notEmpty(mailConfigUuid, "Mail config uuid must be set.");

		AbstractDomain domain = domainBusinessService.find(domainUuid);
		if (!domain.isManagedBy(actor)) {
			throw new BusinessException(DOMAIN_FORBIDDEN, "You are not allowed to manage domain " + domainUuid);
		}
		MailConfig mailConfig = mailConfigBusinessService.findByUuid(mailConfigUuid);
		if (!domain.isAncestry(mailConfig.getDomain().getUuid())){
			throw new BusinessException("Mail config " + mailConfigUuid + " cannot be added to domain " + domainUuid);
		}
		if (!mailConfig.isVisible() && !StringUtils.equals(domainUuid,mailConfig.getDomain().getUuid())){
			throw new BusinessException("Mail config " + mailConfigUuid + " is private thus cannot be assigned to " + domainUuid);
		}

		domain.setCurrentMailConfiguration(mailConfig);
		domainBusinessService.update(domain);
	}

	@Override
	public Set<AbstractDomain> findAllAssociatedDomains(MailConfig mailConfig) {
		Validate.notNull(mailConfig, "Mail config uuid must be set.");

		return ImmutableSet.copyOf(domainBusinessService.loadRelativeDomains(mailConfig));
	}
}
