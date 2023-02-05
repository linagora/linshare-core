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
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
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

	public GenericUploadRequestEmailBuilder(
			TemplateEngine templateEngine,
			MailActivationBusinessService mailActivationBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			DomainBusinessService domainBusinessService,
			FileDataStore fileDataStore) {
		super(templateEngine, mailActivationBusinessService, functionalityReadOnlyService, domainBusinessService,
				fileDataStore);
	}

	protected List<MailContact> getRecipients(UploadRequest request) {
		List<MailContact> recipients = Lists.newArrayList();
		for (UploadRequestUrl u : request.getUploadRequestURLs()) {
			recipients.add(new MailContact(u.getContact()));
		}
		return recipients;
	}

	protected List<MailContact> getRecipients(List<Contact> contacts) {
		return contacts.stream().map(c -> new MailContact(c)).collect(Collectors.toList());
	}

	protected List<MailContact> getRecipients(UploadRequestGroup uploadrequestGroup) {
		List<MailContact> recipients = Lists.newArrayList();
		for (UploadRequest request : uploadrequestGroup.getUploadRequests()) {
			for (UploadRequestUrl u : request.getUploadRequestURLs()) {
				recipients.add(new MailContact(u.getContact()));
			}
		}
		return recipients;
	}

	protected List<Document> getUploadRequestDocuments(boolean warnOwner, UploadRequest request, UploadRequestUrl requestUrl) {
		List<Document> documents = Lists.newArrayList();
		Set<UploadRequestUrl> requestURLs = request.getUploadRequestURLs();
		for (UploadRequestUrl uploadRequestUrl : requestURLs) {
			Set<UploadRequestEntry> entries = uploadRequestUrl.getUploadRequestEntries();
			for (UploadRequestEntry e : entries) {
				Document d = new Document(e);
				String href = null;
				String linshareURL = null;
				if (warnOwner) {
					linshareURL = getLinShareUrl(request.getUploadRequestGroup().getOwner());
					href = getUploadRequestEntryLink(linshareURL, request.getUploadRequestGroup().getUuid(),
							request.getUuid(), e.getUuid());
				} else {
					linshareURL = getLinShareUploadRequestUrl(request.getUploadRequestGroup().getOwner().getDomain());
					href = uploadRequestUrl.getFullUrl(linshareURL);
					d.setMine(isMine(requestUrl, uploadRequestUrl));
				}
				d.setHref(href);
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
		ctx.setVariable("subject", "Subject of Upload Request");
		ctx.setVariable("warnOwner", warnOwner);
		return ctx;
	}
}