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
package org.linagora.linshare.storage.encryption.key;

/**
 * Immutable result of wrapping a DEK: which key it was wrapped under, the
 * wrapped bytes, and the wrapping algorithm identifier. Never holds raw DEK
 * or KEK bytes.
 */
public final class WrappedKey {

	private final String keyId;

	private final byte[] wrappedKeyBytes;

	private final String wrappingAlgorithm;

	public WrappedKey(String keyId, byte[] wrappedKeyBytes, String wrappingAlgorithm) {
		if (keyId == null || keyId.isEmpty()) {
			throw new IllegalArgumentException("keyId must not be null or empty");
		}
		if (wrappedKeyBytes == null || wrappedKeyBytes.length == 0) {
			throw new IllegalArgumentException("wrappedKeyBytes must not be null or empty");
		}
		if (wrappingAlgorithm == null || wrappingAlgorithm.isEmpty()) {
			throw new IllegalArgumentException("wrappingAlgorithm must not be null or empty");
		}
		this.keyId = keyId;
		this.wrappedKeyBytes = wrappedKeyBytes.clone();
		this.wrappingAlgorithm = wrappingAlgorithm;
	}

	public String getKeyId() {
		return keyId;
	}

	public byte[] getWrappedKeyBytes() {
		return wrappedKeyBytes.clone();
	}

	public String getWrappingAlgorithm() {
		return wrappingAlgorithm;
	}
}
