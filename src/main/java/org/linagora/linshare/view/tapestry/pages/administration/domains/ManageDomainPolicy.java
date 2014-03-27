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
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

package org.linagora.linshare.view.tapestry.pages.administration.domains;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.vo.AllowAllDomainVo;
import org.linagora.linshare.core.domain.vo.AllowDomainVo;
import org.linagora.linshare.core.domain.vo.DenyDomainVo;
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = { "../../../components/jquery/jquery-1.7.2.js","../../../components/jquery/jquery.ui.core.js","../../../components/jquery/jquery.ui.widget.min.js","../../../components/jquery/jquery.ui.mouse.min.js",
		"../../../components/jquery/jquery.ui.sortable.min.js","ManageDomainPolicy.js" }, stylesheet = {"../../../components/jquery/jquery-ui-1.8.21.custom.css","ManageDomainPolicy.css" })
public class ManageDomainPolicy {

	private static Logger logger = LoggerFactory.getLogger(ManageDomainPolicy.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@Inject
	private DomainPolicyFacade domainPolicyFacade;

	@SessionState(create = false)
	@Property
	private DomainPolicyVo domainPolicyVo;

	@SessionState
	private UserVo loginUser;

	@Property
	private String _rules;

	@Property
	private DomainAccessRuleVo _rule;

	@Property
	private long _ruleIdentifier;

	@Property
	private int indexRule;

	@Inject
	private Messages messages;

	@Property
	private String tabPos;

	@Property
	@Persist
	@Validate("required")
	private DomainAccessRuleType ruleToAdd;

	@Property
	private String domainSelection;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Property
	private boolean onTop;

	@Property
	private boolean reset;

	@Persist
	@Property
	private List<String> domains;

	@Persist
	@Property
	private boolean showAddRuleForm;

	public List<DomainAccessRuleVo> getRulesList() {
		List<DomainAccessRuleVo> rulesVo = new ArrayList<DomainAccessRuleVo>();
		rulesVo = domainPolicyVo.getDomainAccessPolicy().getRules();
		return rulesVo;
	}

	public void onActivate(String identifier) throws BusinessException {
		domainPolicyVo = domainPolicyFacade.retrieveDomainPolicy(identifier);
		domains = domainFacade.findAllDomainIdentifiers();
		ruleToAdd = DomainAccessRuleType.ALLOW;
		showAddRuleForm = false;
	}

	public void onRemove(long _ruleIdentifier) throws BusinessException {
		DomainAccessRuleVo ruleToDelete = domainPolicyFacade.retrieveDomainAccessRule(_ruleIdentifier);
		domainPolicyFacade.deleteDomainAccessRule(domainPolicyVo, ruleToDelete);
		domainPolicyVo = domainPolicyFacade.retrieveDomainPolicy(domainPolicyVo.getIdentifier());
	}

	public Object onActionFromCancel() {
		domainPolicyVo = null;
		return Index.class;
	}

	public Object onSuccessFromForm() throws BusinessException {
		if (tabPos != null) {
			domainPolicyFacade.setAndSortDomainAccessRuleList(domainPolicyVo,tabPos);
		}
		domainPolicyFacade.updateDomainPolicy(loginUser, domainPolicyVo);
		domainPolicyVo = null;
		return Index.class;

	}

	public void onActionFromAdd() {
		showAddRuleForm = true;
	}

	public void onSelectedFromReset() {
		reset = true;
	}

	public void onSuccessFromAddRuleForm() throws BusinessException {
		if (reset != true) {
			DomainAccessRuleVo rule = domainPolicyFacade.getDomainAccessRuleVoFromSelect(ruleToAdd, domainSelection);
			if (onTop) {
				domainPolicyFacade.insertRuleOnTopOfList(domainPolicyVo, rule);
			} else {
				domainPolicyVo.getDomainAccessPolicy().addRule(rule);
			}
			domainPolicyFacade.updateDomainPolicy(loginUser, domainPolicyVo);
			domainPolicyVo = domainPolicyFacade.retrieveDomainPolicy(domainPolicyVo.getIdentifier());
			ruleToAdd = DomainAccessRuleType.ALLOW;
		}
		showAddRuleForm = false;
	}

	public String getRuleDescription() {
		if (_rule instanceof AllowDomainVo) {
			return String.format((messages.get("pages.administration.domains.index.createdomainpolicy.allow")),((AllowDomainVo) _rule).getDomainIdentifier());
		} else if (_rule instanceof DenyDomainVo) {
			return String.format((messages.get("pages.administration.domains.index.createdomainpolicy.deny")),((DenyDomainVo) _rule).getDomainIdentifier());
		} else if (_rule instanceof AllowAllDomainVo) {
			return String.format((messages.get("pages.administration.domains.index.createdomainpolicy.allow_all")));
		} else {
			return String.format((messages.get("pages.administration.domains.index.createdomainpolicy.deny_all")));
		}
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
