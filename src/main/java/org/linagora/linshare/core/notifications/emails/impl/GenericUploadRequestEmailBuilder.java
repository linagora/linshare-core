/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.notifications.context.GenericUploadRequestEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Request;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public abstract class GenericUploadRequestEmailBuilder extends EmailBuilder {

	public GenericUploadRequestEmailBuilder() {
		super();
	}

	public GenericUploadRequestEmailBuilder(TemplateEngine templateEngine, boolean insertLicenceTerm,
			MailActivationBusinessService mailActivationBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService, DomainBusinessService domainBusinessService) {
		super(templateEngine, insertLicenceTerm, mailActivationBusinessService, functionalityReadOnlyService,
				domainBusinessService);
	}

	protected List<MailContact> getRecipients(UploadRequest request) {
		List<MailContact> recipients = Lists.newArrayList();
		for (UploadRequestUrl u : request.getUploadRequestURLs()) {
			recipients.add(new MailContact(u.getContact()));
		}
		return recipients;
	}

	protected List<Document> getDocuments(boolean warnOwner, UploadRequest request, UploadRequestUrl requestUrl) {
		List<Document> documents = Lists.newArrayList();
		Set<UploadRequestUrl> requestURLs = request.getUploadRequestURLs();
		for (UploadRequestUrl uploadRequestUrl : requestURLs) {
			Set<UploadRequestEntry> entries = uploadRequestUrl.getUploadRequestEntries();
			for (UploadRequestEntry e : entries) {
				Document d = new Document(e);
				if (warnOwner) {
					String href = getOwnerDocumentLink(getUrlTemplateForDocuments(), e.getDocumentEntry().getUuid());
					d.setHref(href);
				} else {
					d.setMine(isMine(requestUrl, uploadRequestUrl));
				}
				documents.add(d);
			}
		}
		return documents;
	}

	private Boolean isMine(UploadRequestUrl requestUrl, UploadRequestUrl uploadRequestUrl) {
		Boolean mine = null;
		if (requestUrl != null) {
			mine = false;
			if (uploadRequestUrl.equals(requestUrl)) {
				mine = true;
			}
		}
		return mine;
	}

	protected String getLinShareUploadRequestUrl(AbstractDomain domain) {
		return functionalityReadOnlyService.getUploadRequestFunctionality(domain).getValue();
	}

	protected String getUploadRequestUrl(GenericUploadRequestEmailContext emailCtx) {
		UploadRequestUrl requestUrl = emailCtx.getRequestUrl();
		if (requestUrl != null) {
			return requestUrl.getFullUrl(getLinShareUploadRequestUrl(emailCtx.getFromDomain()));
		}
		return null;
	}

	protected Context newTmlContext(GenericUploadRequestEmailContext emailCtx) {
		String linshareURL = getLinShareUrl(emailCtx.getOwner());
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("linshareURL", linshareURL);
		UploadRequestUrl requestUrl = emailCtx.getRequestUrl();
		if (requestUrl != null) {
			ctx.setVariable("request", new Request(requestUrl));
			ctx.setVariable("requestRecipient", new MailContact(requestUrl.getContact()));
		} else {
			ctx.setVariable("request", new Request(emailCtx.getUploadRequest()));
		}
		if (emailCtx.isWarnOwner()) {
			// there is no UI in the current user interface for now.
			ctx.setVariable("requestUrl", linshareURL);
		} else {
			ctx.setVariable("requestUrl", getUploadRequestUrl(emailCtx));
		}
		ctx.setVariable("requestOwner", new MailContact(emailCtx.getOwner()));
		ctx.setVariable("subject", emailCtx.getUploadRequest().getUploadRequestGroup().getSubject());
		ctx.setVariable("warnOwner", emailCtx.isWarnOwner());
		return ctx;
	}

	protected Context newFakeContext(Language language, boolean warnOwner) {
		return newFakeContext(language, warnOwner, true);
	}

	protected Context newFakeContext(Language language, boolean warnOwner, boolean addRecipient) {
		Context ctx = super.newFakeContext(language);
		ctx.setVariable("requestOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		if (addRecipient) {
			ctx.setVariable("requestRecipient", new MailContact("unknown@linshare.org"));
		}
		ctx.setVariable("request", new Request("My test subject", new Date(), getFakeExpirationDate(), 8, 1));
		if (warnOwner) {
			ctx.setVariable("requestUrl", fakeLinshareURL);
		} else {
			ctx.setVariable("requestUrl", fakeLinshareURL + "/#fakeURL/uuid");
		}
		ctx.setVariable("subject", "upload request sujet");
		ctx.setVariable("warnOwner", warnOwner);
		return ctx;
	}
}