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
package org.linagora.linshare.core.domain.vo;

import java.util.Date;

import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

public class UploadRequestVo {

	private String uuid;

	private String subject;

	private String body;

	private int size;

	private int maxFileCount;

	private long maxDepositSize;

	private long maxFileSize;

	private UploadRequestStatus status;

	private Date activationDate;

	private Date creationDate;

	private Date modificationDate;

	private Date notificationDate;

	private Date expiryDate;

	private boolean canDelete;

	private boolean canClose;

	private boolean canEditExpiryDate;

	private Language locale = Language.ENGLISH;

	private boolean secured;

	private UserVo owner;

	private String recipient;

	@NonVisual
	private BeanModel<UploadRequestVo> model;

	public UploadRequestVo() {
		super();
	}

	public UploadRequestVo(UploadRequest req) {
		uuid = req.getUuid();
		subject = req.getUploadRequestGroup().getSubject();
		body = req.getUploadRequestGroup().getBody();
		size = req.getUploadRequestEntries().size();
		maxFileCount = req.getMaxFileCount();
		maxDepositSize = req.getMaxDepositSize();
		maxFileSize = req.getMaxFileSize();
		status = req.getStatus();
		activationDate = req.getActivationDate();
		creationDate = req.getCreationDate();
		modificationDate = req.getModificationDate();
		notificationDate = req.getNotificationDate();
		expiryDate = req.getExpiryDate();
		canDelete = req.isCanDelete();
		canClose = req.isCanClose();
		canEditExpiryDate = req.isCanEditExpiryDate();
		locale = Language.fromTapestryLocale(req.getLocale());
		secured = req.isSecured();
		owner = new UserVo(req.getOwner());
	}

	@NonVisual
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(int maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public boolean isCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public UserVo getOwner() {
		return owner;
	}

	public void setOwner(UserVo owner) {
		this.owner = owner;
	}

	public BeanModel<UploadRequestVo> getModel() {
		return model;
	}

	public void setModel(BeanModel<UploadRequestVo> model) {
		this.model = model;
	}

	/*
	 * Transformer
	 */
	public UploadRequest toEntity() {
		UploadRequest ret = new UploadRequest();

		// ret.setUploadRequestGroup(uploadRequestGroup); // FIXME TODO
		ret.setMaxFileCount(maxFileCount);
		ret.setMaxDepositSize(maxDepositSize);
		ret.setMaxFileSize(maxFileSize);
		ret.setActivationDate(activationDate);
		ret.setCreationDate(creationDate);
		ret.setModificationDate(modificationDate);
		ret.setNotificationDate(notificationDate);
		ret.setExpiryDate(expiryDate);
		ret.setCanDelete(canDelete);
		ret.setCanClose(canClose);
		ret.setCanEditExpiryDate(canEditExpiryDate); // TODO functionality
		ret.setLocale(locale.getTapestryLocale());
		ret.setSecured(secured);
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UploadRequestVo))
			return false;
		UploadRequestVo other = (UploadRequestVo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@NonVisual
	public boolean isClosed() {
		return this.getStatus().equals(UploadRequestStatus.STATUS_CLOSED);
	}

	@NonVisual
	public boolean isVisible() {
		return Lists.newArrayList(UploadRequestStatus.STATUS_CREATED,
				UploadRequestStatus.STATUS_ENABLED,
				UploadRequestStatus.STATUS_CLOSED).contains(this.getStatus());
	}

	/*
	 * Filters
	 */
	public static Predicate<? super UploadRequestVo> equalTo(String uuid) {
		UploadRequestVo test = new UploadRequestVo();

		test.setUuid(uuid);
		return Predicates.equalTo(test);
	}

	public void fromTemplate(UploadRequestTemplateVo template) {
		subject = template.getName(); // TODO XXX FIXME
	}
}
