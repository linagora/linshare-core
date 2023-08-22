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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AdminV5", description = "A linshare share recipient statistic")
public class ShareRecipientStatisticDto {
	@Schema(description = "Recipient's type")
	private String recipientType;

	@Schema(description = "Recipient's uuid")
	private String recipientUuid;

	@Schema(description = "Recipient's mail")
	private String recipientMail;

	@Schema(description = "Domain's uuid")
	private String domainUuid;

	@Schema(description = "Domain's label")
	private String domainLabel;

	@Schema(description = "Shares count for this recipient")
	private Long shareCount;

	@Schema(description = "Sum of sizes of all document shared to this recipient")
	private Long shareTotalSize;

	public ShareRecipientStatisticDto() {
		super();
	}

	protected ShareRecipientStatisticDto(ShareRecipientStatistic statistic) {
		this.recipientType = statistic.getRecipientType();
		this.recipientUuid = statistic.getRecipientUuid();
		this.recipientMail = statistic.getRecipientMail();
		this.domainUuid = statistic.getDomainUuid();
		this.domainLabel = statistic.getDomainLabel();
		this.shareTotalSize = statistic.getShareTotalSize();
		this.shareCount = statistic.getShareCount();
	}

	public String getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	public String getRecipientUuid() {
		return recipientUuid;
	}

	public void setRecipientUuid(String recipientUuid) {
		this.recipientUuid = recipientUuid;
	}

	public String getRecipientMail() {
		return recipientMail;
	}

	public void setRecipientMail(String recipientMail) {
		this.recipientMail = recipientMail;
	}

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainLabel) {
		this.domainLabel = domainLabel;
	}

	public Long getShareCount() {
		return shareCount;
	}

	public void setShareCount(Long shareCount) {
		this.shareCount = shareCount;
	}

	public Long getShareTotalSize() {
		return shareTotalSize;
	}

	public void setShareTotalSize(Long shareTotalSize) {
		this.shareTotalSize = shareTotalSize;
	}

	/*
	 * Transformers
	 */
	public static Function<ShareRecipientStatistic, ShareRecipientStatisticDto> toDto() {
		return new Function<ShareRecipientStatistic, ShareRecipientStatisticDto>() {
			@Override
			public ShareRecipientStatisticDto apply(ShareRecipientStatistic arg0) {
				return new ShareRecipientStatisticDto(arg0);
			}
		};
	}

}
