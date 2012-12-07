/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;

/** Defines a share expiry rule.
 */
public class ShareExpiryRuleVo implements Serializable {

    private final Integer shareExpiryTime;
    private final TimeUnit shareExpiryUnit;
    private final Integer shareSize;
    private final FileSizeUnit shareSizeUnit;

    public ShareExpiryRuleVo(Integer shareExpiryTime, TimeUnit shareExpiryUnit,
        Integer shareSize, FileSizeUnit shareSizeUnit) {
        this.shareExpiryTime = shareExpiryTime;
        this.shareExpiryUnit = shareExpiryUnit;
        this.shareSize = shareSize;
        this.shareSizeUnit = shareSizeUnit;
    }

    public ShareExpiryRuleVo(ShareExpiryRule shareExpiryRule) {
        this.shareExpiryTime = shareExpiryRule.getShareExpiryTime();
        this.shareExpiryUnit = shareExpiryRule.getShareExpiryUnit();
        this.shareSize = shareExpiryRule.getShareSize();
        this.shareSizeUnit = shareExpiryRule.getShareSizeUnit();
    }

    public Integer getShareExpiryTime() {
        return shareExpiryTime;
    }

    public TimeUnit getShareExpiryUnit() {
        return shareExpiryUnit;
    }

    public Integer getShareSize() {
        return shareSize;
    }

    public FileSizeUnit getShareSizeUnit() {
        return shareSizeUnit;
    }

    public ShareExpiryRule getShareExpiryRule() {
        ShareExpiryRule shareExpiryRule = new ShareExpiryRule();
        shareExpiryRule.setShareExpiryTime(shareExpiryTime);
        shareExpiryRule.setShareExpiryUnit(shareExpiryUnit);
        shareExpiryRule.setShareSize(shareSize);
        shareExpiryRule.setShareSizeUnit(shareSizeUnit);
        return shareExpiryRule;
    }

}
