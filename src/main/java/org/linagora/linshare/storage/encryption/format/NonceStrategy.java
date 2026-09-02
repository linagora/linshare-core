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
package org.linagora.linshare.storage.encryption.format;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobUnsupportedVersionException;

/**
 * Derives a unique 96-bit AES-GCM nonce for each chunk of a blob. Uniqueness
 * must hold mechanically for every chunk index under one blob's DEK; no
 * timestamps, no counter wraparound.
 */
public interface NonceStrategy {

	int schemeId();

	byte[] deriveNonce(byte[] noncePrefix, long chunkIndex);

	/** Highest 0-based chunk index this construction can represent. */
	long maxChunkIndex();

	static NonceStrategy forSchemeId(int nonceSchemeId) {
		if (nonceSchemeId == RandomPrefixCounterNonceStrategy.SCHEME_ID) {
			return new RandomPrefixCounterNonceStrategy();
		}
		throw new EncryptedBlobUnsupportedVersionException("Unsupported nonce scheme id: " + nonceSchemeId);
	}
}
