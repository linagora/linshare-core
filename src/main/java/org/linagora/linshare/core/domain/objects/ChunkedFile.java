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
package org.linagora.linshare.core.domain.objects;

import java.util.Set;

import com.google.common.collect.Sets;

public class ChunkedFile {

	private long startTime;

	private final Set<Long> chunks = Sets.newConcurrentHashSet();

	private final java.nio.file.Path path;

	public ChunkedFile(java.nio.file.Path path) {
		this.path = path;
		this.startTime = System.currentTimeMillis();
	}

	public boolean hasChunk(long chunkNumber) {
		return chunks.contains(chunkNumber);
	}

	public void addChunk(long chunkNumber) {
		chunks.add(chunkNumber);
	}

	public Set<Long> getChunks() {
		return chunks;
	}

	public java.nio.file.Path getPath() {
		return path;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "ChunkedFile [startTime=" + startTime + ", chunks=" + chunks + ", path=" + path + "]";
	}

}
