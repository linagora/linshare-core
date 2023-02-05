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
package org.linagora.linshare.core.business.service.impl;

public abstract class GenericQuotaBusinessServiceImpl {

	private Util<Long> longUtil = new Util<Long>();

	private Util<Boolean> booleanUtil = new Util<Boolean>();

	class Util<T> {
		protected boolean needCascade(T fromValue, T toValue, Boolean fromOverride, Boolean toOverride) {
			boolean doIt = false;
			if (fromOverride == null) {
				// root
				return true;
			}
			if (toOverride != null) {
				if (!toOverride.equals(fromOverride)) {
					if (toOverride) {
						// from false to true => need to cascade
						doIt = true;
					} else {
						// from true to false => need to cascade
						// Do we need to restore 'fromValue' with parent default
						// value ?
						doIt = true;
					}
				} else {
					if (!toValue.equals(fromValue)) {
						doIt = true;
					}
				}
			}
			return doIt;
		}
	}

	protected boolean needCascade(Long fromValue, Long toValue, Boolean fromOverride, Boolean toOverride) {
		return longUtil.needCascade(fromValue, toValue, fromOverride, toOverride);
	}

	protected boolean needCascade(Boolean fromValue, Boolean toValue, Boolean fromOverride, Boolean toOverride) {
		return booleanUtil.needCascade(fromValue, toValue, fromOverride, toOverride);
	}

	protected boolean needToRestore(Boolean fromOverride, Boolean toOverride) {
		boolean doIt = false;
		if (fromOverride == null) {
			// root
			return doIt;
		}
		if (toOverride != null) {
			if (!toOverride.equals(fromOverride)) {
				if (!toOverride) {
					// from true to false => need to cascade
					// Do we need to restore 'fromValue' with parent default
					// value ?
					doIt = true;
				}
			}
		}
		return doIt;
	}
}
