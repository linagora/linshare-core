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
/*
 * This file is a part of Linra.
 * This software is a computer program whose purpose 
 * is to manage the whole life-cycle of electronic certificate.
 * It relies on EJBCA, the open-source PKI, and provides 
 * an easy-to-use interface for end users, administration interface 
 * and advanced certificate management features.
 *
 * ==LICENSE NOTICE==
 * Linra is a free software subjected to the  
 * ** GNU Affero Public License ** as  published by the 
 * Free Software Foundation, ** version 3 ** of the license.
 * 
 * By application to section 7 in the GNU  Affero GPLv3, 
 * dynamic and static links do not extend license to other
 * softwares.
 * 
 * You can redistribute  and/or modify since  you respect 
 * the term of the license. 
 * 
 * NOTICE : THIS LICENSE IS FREE OF CHARGE AND THE SOFTWARE
 * IS DISTRIBUTED WITHOUT ANY WARRANTIES OF ANY KIND 
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * ==LICENSE NOTICE==
 * 
 * (c) 2008 Groupe Linagora - http://linagora.com
 * 
 */
package org.linagora.linShare.core.repository.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.linagora.linShare.core.LinShareConstants;


public class LinShareNamingStrategy extends ImprovedNamingStrategy {
	
	private static final long serialVersionUID = -8315941617365356531L;
	
	private final String prefix;
	
	public LinShareNamingStrategy() {
		this.prefix = LinShareConstants.DEFAULT_DBTABLE_PREFIX;
	}
	
	public LinShareNamingStrategy(final String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String classToTableName(String className) {
		StringBuilder sb = new StringBuilder();
		if(!StringUtils.isEmpty(prefix) && !className.startsWith(prefix)) {
			sb.append(prefix);
		}
		return sb.append(super.classToTableName(className)).toString();
	}
	
	
	@Override
	public String tableName(String tableName) {
		StringBuilder sb = new StringBuilder();
		if(!StringUtils.isEmpty(prefix) && !tableName.startsWith(prefix)) {
			sb.append(prefix);
		}
		return sb.append(super.tableName(tableName)).toString();
	}

}
