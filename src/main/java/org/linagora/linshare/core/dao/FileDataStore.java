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

import org.linagora.linshare.core.domain.objects.FileMetaData;

import com.google.common.io.ByteSource;

public interface FileDataStore {

	void remove(FileMetaData metadata);

	FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException;

	ByteSource get(FileMetaData metadata);

	boolean exists(FileMetaData metadata);

	/**
	 * Returns exactly {@code length} bytes starting at physical byte
	 * {@code offset}. The default falls back to slicing a full {@link #get}
	 * read, which is correct for every existing implementation but not
	 * efficient; a backend able to seek should override this to read only
	 * the requested physical bytes (see {@code AbstractJcloudFileDataStoreImpl}).
	 */
	default ByteSource getRange(FileMetaData metadata, long offset, long length) {
		return get(metadata).slice(offset, length);
	}

}
