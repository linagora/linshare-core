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
