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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

public class UploadRequestVo implements Serializable, Cloneable {

	private static final long serialVersionUID = 1068995245715097010L;

	private String uuid;

	@Validate(value = "required")
	private String subject;

	private String body;

	private Integer size;

	private Integer maxFileCount;

	private Long maxDepositSize;

	private Long maxFileSize;

	private UploadRequestStatus status;

	private Date activationDate;

	private Date creationDate;

	private Date modificationDate;

	private Date notificationDate;

	private Date expiryDate;

	private Boolean canDelete;

	private Boolean canClose;

	private Boolean groupedMode;

	private Boolean canEditExpiryDate;

	@Validate(value = "required")
	private Language locale = Language.ENGLISH;

	private boolean secured;

	private UserVo owner;

	@Validate(value = "required")
	private String recipient;

	private List<Contact> recipients = Lists.newArrayList();

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
		groupedMode = false;
		canEditExpiryDate = req.isCanEditExpiryDate();
		locale = Language.fromTapestryLocale(req.getLocale());
		secured = req.isSecured();
		owner = new UserVo(req.getOwner());

		for (UploadRequestUrl u: req.getUploadRequestURLs()) {
			recipients.add(new Contact(u.getContact().getMail()));
		}
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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Boolean getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Boolean getCanClose() {
		return canClose;
	}

	public Boolean getGroupedMode() {
		return groupedMode;
	}

	public void setGroupedMode(Boolean groupedMode) {
		this.groupedMode = groupedMode;
	}

	public void setCanClose(Boolean canClose) {
		this.canClose = canClose;
	}

	public Boolean getCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(Boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public boolean getSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}

	public UserVo getOwner() {
		return owner;
	}

	public void setOwner(UserVo owner) {
		this.owner = owner;
	}

	public List<Contact> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Contact> recipients) {
		this.recipients = recipients;
	}

	public BeanModel<UploadRequestVo> getModel() {
		return model;
	}

	public void setModel(BeanModel<UploadRequestVo> model) {
		this.model = model;
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

	/*
	 * Transformers
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

	public void fromTemplate(UploadRequestTemplateVo t) {
		if (t.getDepositMode() != null) {
			canDelete = !t.getDepositMode();
		}
		if (t.getMaxFileCount() != null) {
			maxFileCount = t.getMaxFileCount().intValue();
		}
		if (t.getMaxFileSize() != null) {
			maxFileSize = t.getMaxFileSize();
		}
		if (t.getMaxDepositSize() != null) {
			maxDepositSize = t.getMaxDepositSize();
		}
		if (t.getLocale() != null) {
			locale = t.getLocale();
		}
		if (t.getSecured() != null) {
			secured = t.getSecured();
		}
		if (t.getProlongationMode() != null) {
			canEditExpiryDate = t.getProlongationMode();
		}

		Calendar d;

		if (t.getDurationBeforeActivation() != null) {
			d = GregorianCalendar.getInstance();
			d.add(t.getUnitBeforeActivation().toCalendarValue(), t
					.getDurationBeforeActivation().intValue());
			activationDate = d.getTime();
		}
		if (t.getDurationBeforeExpiry() != null) {
			d = GregorianCalendar.getInstance();
			d.add(t.getUnitBeforeExpiry().toCalendarValue(), t
					.getDurationBeforeExpiry().intValue());
			expiryDate = d.getTime();
		}
		if (t.getDayBeforeNotification() != null) {
			d = GregorianCalendar.getInstance();
			d.add(Calendar.DATE, t.getDayBeforeNotification().intValue());
			notificationDate = d.getTime();
		}
	}
}
