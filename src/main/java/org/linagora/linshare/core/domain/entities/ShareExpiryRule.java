/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.TimeUnit;

/** Defines a share expiry rule.
 */
public class ShareExpiryRule implements Serializable, Comparable<ShareExpiryRule> {

	private static final long serialVersionUID = -5489131425550163092L;

	private Integer shareExpiryTime;
    private TimeUnit shareExpiryUnit;
    private Integer shareSize;
    private FileSizeUnit shareSizeUnit;

    public ShareExpiryRule() { }

    public Integer getShareExpiryTime() {
        return shareExpiryTime;
    }

    public void setShareExpiryTime(Integer shareExpiryTime) {
        this.shareExpiryTime = shareExpiryTime;
    }

    public TimeUnit getShareExpiryUnit() {
        return shareExpiryUnit;
    }

    public void setShareExpiryUnit(TimeUnit shareExpiryUnit) {
        this.shareExpiryUnit = shareExpiryUnit;
    }

    public Integer getShareSize() {
        return shareSize;
    }

    public void setShareSize(Integer shareSize) {
        this.shareSize = shareSize;
    }

    public FileSizeUnit getShareSizeUnit() {
        return shareSizeUnit;
    }

    public void setShareSizeUnit(FileSizeUnit shareSizeUnit) {
        this.shareSizeUnit = shareSizeUnit;
    }
/*
    public static Comparator<ShareExpiryRule> getSizeComparator() {
        return new Comparator<ShareExpiryRule>() {

            public int compare(ShareExpiryRule o1, ShareExpiryRule o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                }
                long size1  = o1.getShareSizeUnit().getPlainSize(o1.getShareSize());
                long size2  = o2.getShareSizeUnit().getPlainSize(o2.getShareSize());
                if (size1 < size2) {
                    return -1;
                } else if (size1 > size2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }
*/
	public int compareTo(ShareExpiryRule o ) {
		if (null ==o) return 1;
		
		 long size1  = o.getShareSizeUnit().getPlainSize(o.getShareSize());
         long size2  = this.getShareSizeUnit().getPlainSize(this.getShareSize());
         if (size1 < size2) {
             return 1;
         } else if (size1 > size2) {
             return -1;
         } else {
             return 0;
         }
         
	}
}
