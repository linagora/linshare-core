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
import java.util.Map;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnWorkgroupDocumentUpdatedContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WorkGroupWarnWorkgroupDocumentUpdatedEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.WORKGROUP_WARN_WORKGROUP_DOCUMENT_UPDATED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WorkGroupWarnWorkgroupDocumentUpdatedContext emailCtx = (WorkGroupWarnWorkgroupDocumentUpdatedContext) context;
		return buildMailContainer(emailCtx, Maps.newHashMap());
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMember workGroupMember = getNewFakeSharedSpaceMember("work_group_name-1");
		WorkGroupFolder folder = new WorkGroupFolder();
		folder.setName("folder_name");
		WorkGroupDocument wgDocument = new WorkGroupDocument();
		wgDocument.setName("New document");
		wgDocument.setSize(1457L);
		wgDocument.setModificationDate(new Date());
		ctx.setVariable("owner", workGroupMember.getAccount());
		ctx.setVariable("workGroupMember", workGroupMember);
		ctx.setVariable("document", wgDocument);
		ctx.setVariable("workGroupLink", getWorkGroupLink(fakeLinshareURL, "fake_uuid"));
		ctx.setVariable("folder", folder);
		ctx.setVariable("workGroupFolderLink", getWorkGroupFolderLink(fakeLinshareURL, workGroupMember.getNode().getUuid(),
				"work_group_name", "fake_uuid", "Folder_name"));
		ctx.setVariable("workGroupDocumentLink", getWorkGroupDocumentLink(fakeLinshareURL, workGroupMember.getNode().getUuid(),
				"work_group_name", "fake_uuid", "Folder_name", "fake_uuid"));
		ctx.setVariable("linshareURL", fakeLinshareURL);
		res.add(ctx);
		return res;
	}

	protected MailContainerWithRecipient buildMailContainer(WorkGroupWarnWorkgroupDocumentUpdatedContext emailCtx,
			Map<String, Object> variables) {
		Context ctx = new Context(emailCtx.getLocale());
		MailContact owner = null;
		if (emailCtx.getOwner() instanceof SystemAccount) {
			owner = new MailContact(emailCtx.getOwner().getMail());

		} else {
			owner = new MailContact((User) emailCtx.getOwner());
		}
		variables.put("owner", owner);
		SharedSpaceMember workGroupMember = emailCtx.getWorkgroupMember();
		WorkGroupDocument workGroupDocument = emailCtx.getDocument();
		WorkGroupNode folder = emailCtx.getFolder();
		String linshareURL = getLinShareUrl(emailCtx.getOwner());
		variables.put("document", workGroupDocument);
		variables.put("linshareURL", linshareURL);
		variables.put("workGroupMember", workGroupMember);
		variables.put("folder", folder);
		variables.put("workGroupLink", getWorkGroupLink(linshareURL, workGroupMember.getNode().getUuid()));
		variables.put("workGroupFolderLink", getWorkGroupFolderLink(linshareURL, workGroupMember.getNode().getUuid(),
				workGroupMember.getNode().getName(), folder.getUuid(), folder.getName()));
		variables.put("workGroupDocumentLink", getWorkGroupDocumentLink(linshareURL, workGroupMember.getNode().getUuid(),
				workGroupMember.getNode().getName(), folder.getUuid(), folder.getName(), workGroupDocument.getUuid()));
		ctx.setVariables(variables);
		MailConfig cfg = emailCtx.getOwner().getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}
}
