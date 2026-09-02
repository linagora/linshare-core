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
package org.linagora.linshare.core.dao;

import java.io.IOException;

/**
 * Optional capability a {@link FileDataStore} backend may implement when it
 * can atomically replace one physical blob with another already-stored one.
 * Migration (docs/ARCH.md 15, 22) needs this to commit a verified,
 * fully-written replacement blob without ever exposing a partially-written
 * target — a plain read-then-write "copy" cannot give that guarantee.
 */
public interface AtomicBlobReplace {

	/**
	 * Atomically makes {@code targetKey} contain exactly the bytes currently
	 * stored at {@code sourceKey} (in the same {@code container}), replacing
	 * whatever was previously at {@code targetKey}. On successful return,
	 * {@code sourceKey} no longer exists. A reader of {@code targetKey} must
	 * never observe a partially-written result, whether this call succeeds,
	 * fails, or the process crashes during it.
	 */
	void atomicReplace(String container, String sourceKey, String targetKey) throws IOException;
}
