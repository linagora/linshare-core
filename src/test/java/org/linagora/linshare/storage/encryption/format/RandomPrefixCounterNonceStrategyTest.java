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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;

class RandomPrefixCounterNonceStrategyTest {

	private final RandomPrefixCounterNonceStrategy strategy = new RandomPrefixCounterNonceStrategy();

	@Test
	void nonceIsAlwaysTwelveBytes() {
		byte[] prefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
		assertEquals(12, strategy.deriveNonce(prefix, 0).length);
	}

	@Test
	void differentChunkIndicesProduceDifferentNonces() {
		byte[] prefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
		byte[] nonce0 = strategy.deriveNonce(prefix, 0);
		byte[] nonce1 = strategy.deriveNonce(prefix, 1);
		assertFalse(Arrays.equals(nonce0, nonce1));
	}

	@Test
	void manyChunkIndicesUnderOnePrefixNeverCollide() {
		byte[] prefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
		new SecureRandom().nextBytes(prefix);

		Set<String> seen = new HashSet<>();
		int chunkCount = 200_000;
		for (long i = 0; i < chunkCount; i++) {
			byte[] nonce = strategy.deriveNonce(prefix, i);
			assertTrue(seen.add(new String(nonce, StandardCharsets.ISO_8859_1)), "nonce collision at chunk index " + i);
		}
	}

	@Test
	void maxChunkIndexIsThirtyTwoBitUnsignedRange() {
		assertEquals(0xFFFFFFFFL, strategy.maxChunkIndex());
	}

	@Test
	void chunkIndexBeyondMaxIsRejectedNotSilentlyWrapped() {
		byte[] prefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
		assertThrows(EncryptedBlobFormatException.class,
				() -> strategy.deriveNonce(prefix, RandomPrefixCounterNonceStrategy.MAX_CHUNK_INDEX + 1));
	}

	@Test
	void wrongPrefixLengthIsRejected() {
		assertThrows(EncryptedBlobFormatException.class, () -> strategy.deriveNonce(new byte[4], 0));
	}
}
