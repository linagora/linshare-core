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
package org.linagora.linshare.core.domain.entities;

public class ShareRecipientStatistic {

    protected String recipientType;
    protected String recipientUuid;
    protected String recipientMail;
    protected String domainUuid;
    protected String domainLabel;
    protected Long shareCount;
    protected Long shareTotalSize;

    public ShareRecipientStatistic() {
    }

    public ShareRecipientStatistic(String recipientType, String recipientUuid, String recipientMail,
                                   String domainUuid, String domainLabel,
                                   Long shareCount, Long shareTotalSize) {
        this.recipientType = recipientType;
        this.recipientUuid = recipientUuid;
        this.recipientMail = recipientMail;
        this.domainUuid = domainUuid;
        this.domainLabel = domainLabel;
        this.shareCount = shareCount;
        this.shareTotalSize = shareTotalSize;
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

    @Override
    public String toString() {
        return "ShareRecipientStatistic [" +
                "recipientType='" + recipientType + '\'' +
                ", recipientUuid='" + recipientUuid + '\'' +
                ", recipientMail='" + recipientMail + '\'' +
                ", domainUuid='" + domainUuid + '\'' +
                ", domainLabel='" + domainLabel + '\'' +
                ", shareCount=" + shareCount +
                ", shareTotalSize=" + shareTotalSize +
                ']';
    }

    public static String getCsvHeader() {
        return "recipientType,recipientUuid,recipientMail,domainUuid,domainLabel,shareCount,shareTotalSize";
    }

    public String toCsvLine() {
        return recipientType + ',' + recipientUuid + ',' + recipientMail + ',' + domainUuid + ','
                + domainLabel + ',' + shareCount + ',' + shareTotalSize;
    }
}
