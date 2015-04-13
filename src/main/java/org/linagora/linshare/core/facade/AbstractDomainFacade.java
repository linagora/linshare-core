/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractDomainFacade {

	public List<String> getAllDomainIdentifiers() throws BusinessException;

	public List<String> getAllDomainIdentifiers(UserVo actorVo)
			throws BusinessException;

	public boolean userCanCreateGuest(UserVo userVo) throws BusinessException;

	public boolean canCreateGuestDomain(String domainIdentifier)
			throws BusinessException;

	public boolean guestDomainAllowed(String domainIdentifier)
			throws BusinessException;

	public List<AbstractDomainVo> findAllTopAndSubDomain();

	public GuestDomainVo findGuestDomain(String topDomainIdentifier);

	public boolean isCustomLogoActive(UserVo actorVo) throws BusinessException;

	public boolean isCustomLogoActiveInRootDomain() throws BusinessException;

	public String getCustomLogoUrl(UserVo actorVo) throws BusinessException;

	public String getCustomLogoUrlInRootDomain() throws BusinessException;

	public String getCustomLogoLink(UserVo actorVo) throws BusinessException;

	public String getCustomLogoLinkInRootDomain() throws BusinessException;

	public List<ShareExpiryRule> getShareExpiryRules(String domainIdentifier)
			throws BusinessException;

	public void updateShareExpiryRules(UserVo actorVo, String domainIdentifier,
			List<ShareExpiryRule> shareExpiryRules) throws BusinessException;

	/**
	 * return the current used space of this domain
	 * 
	 * @param domainIdentifier
	 * @return
	 * @throws BusinessException
	 */
	public Long getUsedSpace(String domainIdentifier) throws BusinessException;

	/**
	 * check that encrypt, signature is possible on the server side just do a
	 * call to crypt function.
	 */
	public boolean checkPlatformEncryptSupportedAlgo();

	String getDomainWelcomeMessagesValue(UserVo userVo, SupportedLanguage lang);
}
