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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wraps/unwraps DEKs via HashiCorp Vault's Transit secrets engine
 * (ARCH.md 19.2): {@code wrap} calls Transit's {@code encrypt} endpoint,
 * {@code unwrap} calls {@code decrypt}. The KEK never leaves Vault — this
 * class only ever sees the DEK and Vault's opaque ciphertext token.
 *
 * <p>Vault's ciphertext token (e.g. {@code vault:v1:base64...}) already
 * encodes the key version it was wrapped under, so it is stored verbatim as
 * {@link WrappedKey#getWrappedKeyBytes()} (UTF-8) with no extra bookkeeping;
 * {@code keyId} is the Transit key name, used to route both operations to
 * the right named key.
 *
 * <p>Uses only {@code java.net.http} and the Jackson databind already on
 * this project's classpath — no new dependency for a single HTTP client.
 */
public final class VaultKeyEncryptionService implements KeyEncryptionService {

	private static final ObjectMapper JSON = new ObjectMapper();

	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

	private final HttpClient httpClient;

	private final URI vaultBaseUrl;

	private final String token;

	private final String transitKeyName;

	public VaultKeyEncryptionService(URI vaultBaseUrl, String token, String transitKeyName) {
		this(vaultBaseUrl, token, transitKeyName,
				HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build());
	}

	VaultKeyEncryptionService(URI vaultBaseUrl, String token, String transitKeyName, HttpClient httpClient) {
		if (vaultBaseUrl == null) {
			throw new EncryptedBlobKeyException("vaultBaseUrl must not be null");
		}
		if (token == null || token.isEmpty()) {
			throw new EncryptedBlobKeyException("Vault token must not be null or empty");
		}
		if (transitKeyName == null || transitKeyName.isEmpty()) {
			throw new EncryptedBlobKeyException("transitKeyName must not be null or empty");
		}
		this.vaultBaseUrl = vaultBaseUrl;
		this.token = token;
		this.transitKeyName = transitKeyName;
		this.httpClient = httpClient;
	}

	@Override
	public WrappedKey wrap(byte[] dek) {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("plaintext", Base64.getEncoder().encodeToString(dek));
		JsonNode data = post("v1/transit/encrypt/" + transitKeyName, requestBody);
		String ciphertext = requireTextField(data, "ciphertext");
		return new WrappedKey(transitKeyName, ciphertext.getBytes(StandardCharsets.UTF_8), "VAULT-TRANSIT");
	}

	@Override
	public byte[] unwrap(String keyId, byte[] wrappedKeyBytes) {
		if (!transitKeyName.equals(keyId)) {
			throw new EncryptedBlobKeyException("Unknown Vault transit key id: " + keyId);
		}
		if (wrappedKeyBytes == null || wrappedKeyBytes.length == 0) {
			throw new EncryptedBlobKeyException("wrappedKeyBytes must not be null or empty");
		}
		String ciphertext = new String(wrappedKeyBytes, StandardCharsets.UTF_8);
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("ciphertext", ciphertext);
		JsonNode data = post("v1/transit/decrypt/" + transitKeyName, requestBody);
		String base64Plaintext = requireTextField(data, "plaintext");
		try {
			return Base64.getDecoder().decode(base64Plaintext);
		} catch (IllegalArgumentException e) {
			throw new EncryptedBlobKeyException("Vault returned a non-base64 plaintext", e);
		}
	}

	/** @return the response's {@code data} object. */
	private JsonNode post(String path, Object requestBody) {
		String requestJson;
		try {
			requestJson = JSON.writeValueAsString(requestBody);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new EncryptedBlobKeyException("Failed to build Vault request body", e);
		}

		HttpRequest request = HttpRequest.newBuilder(vaultBaseUrl.resolve(path)).timeout(REQUEST_TIMEOUT)
				.header("X-Vault-Token", token).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8)).build();

		HttpResponse<String> response;
		try {
			response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
		} catch (java.io.IOException e) {
			throw new EncryptedBlobKeyException("Vault request failed: " + path, e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EncryptedBlobKeyException("Vault request failed: " + path, e);
		}
		if (response.statusCode() != 200) {
			// Vault error bodies can echo back request context; never include
			// them verbatim in an exception message that might be logged.
			throw new EncryptedBlobKeyException(
					"Vault request to " + path + " failed with HTTP " + response.statusCode());
		}

		JsonNode root;
		try {
			root = JSON.readTree(response.body());
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new EncryptedBlobKeyException("Vault returned a malformed JSON response for " + path, e);
		} catch (java.io.IOException e) {
			throw new EncryptedBlobKeyException("Failed to read Vault response for " + path, e);
		}
		JsonNode data = root.get("data");
		if (data == null || data.isNull()) {
			throw new EncryptedBlobKeyException("Vault response for " + path + " has no \"data\" field");
		}
		return data;
	}

	private static String requireTextField(JsonNode data, String field) {
		JsonNode node = data.get(field);
		if (node == null || !node.isTextual()) {
			throw new EncryptedBlobKeyException("Vault response is missing text field \"" + field + "\"");
		}
		return node.textValue();
	}
}
