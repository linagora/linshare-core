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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;

/**
 * Exercises {@link VaultKeyEncryptionService} against a tiny in-process
 * {@code com.sun.net.httpserver.HttpServer} standing in for Vault's Transit
 * API — no real Vault instance needed, and no new test dependency (the
 * server is part of the JDK).
 */
class VaultKeyEncryptionServiceTest {

	private HttpServer server;

	@AfterEach
	void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	private URI startServer(HttpHandler handler) throws IOException {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/", handler);
		server.start();
		return URI.create("http://localhost:" + server.getAddress().getPort() + "/");
	}

	private static void respondJson(HttpExchange exchange, int status, String json) throws IOException {
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, bytes.length);
		try (OutputStream out = exchange.getResponseBody()) {
			out.write(bytes);
		}
	}

	private static String readBody(HttpExchange exchange) throws IOException {
		return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
	}

	@Test
	void wrapPostsToEncryptEndpointAndReturnsVaultsCiphertextToken() throws Exception {
		URI baseUrl = startServer(exchange -> {
			assertEquals("/v1/transit/encrypt/my-key", exchange.getRequestURI().getPath());
			assertEquals("test-token", exchange.getRequestHeaders().getFirst("X-Vault-Token"));
			respondJson(exchange, 200, "{\"data\":{\"ciphertext\":\"vault:v1:abcdEFGH\"}}");
		});
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		WrappedKey wrapped = service.wrap(new byte[32]);

		assertEquals("my-key", wrapped.getKeyId());
		assertArrayEquals("vault:v1:abcdEFGH".getBytes(StandardCharsets.UTF_8), wrapped.getWrappedKeyBytes());
		assertEquals("VAULT-TRANSIT", wrapped.getWrappingAlgorithm());
	}

	@Test
	void unwrapPostsToDecryptEndpointAndReturnsPlaintextBytes() throws Exception {
		byte[] originalDek = new byte[32];
		new SecureRandom().nextBytes(originalDek);
		String expectedBase64 = Base64.getEncoder().encodeToString(originalDek);

		URI baseUrl = startServer(exchange -> {
			assertEquals("/v1/transit/decrypt/my-key", exchange.getRequestURI().getPath());
			String body = readBody(exchange);
			assertTrue(body.contains("vault:v1:abcdEFGH"), "request body should carry the ciphertext token");
			respondJson(exchange, 200, "{\"data\":{\"plaintext\":\"" + expectedBase64 + "\"}}");
		});
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		byte[] result = service.unwrap("my-key", "vault:v1:abcdEFGH".getBytes(StandardCharsets.UTF_8));

		assertArrayEquals(originalDek, result);
	}

	@Test
	void wrapThenUnwrapRoundTripsThroughTheClientAgainstAFakeTransitServer() throws Exception {
		// A trivial (non-cryptographic) fake Transit engine: exercises this
		// client's request/response plumbing end-to-end without needing to
		// reimplement Vault's own encryption inside the test double.
		AtomicInteger requestCount = new AtomicInteger();
		URI baseUrl = startServer(exchange -> {
			requestCount.incrementAndGet();
			String path = exchange.getRequestURI().getPath();
			String body = readBody(exchange);
			if (path.endsWith("/encrypt/round-trip-key")) {
				String base64Plaintext = extractJsonStringField(body, "plaintext");
				respondJson(exchange, 200, "{\"data\":{\"ciphertext\":\"vault:v1:" + base64Plaintext + "\"}}");
			} else if (path.endsWith("/decrypt/round-trip-key")) {
				String ciphertext = extractJsonStringField(body, "ciphertext");
				String base64Plaintext = ciphertext.substring("vault:v1:".length());
				respondJson(exchange, 200, "{\"data\":{\"plaintext\":\"" + base64Plaintext + "\"}}");
			} else {
				respondJson(exchange, 404, "{}");
			}
		});
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "round-trip-key",
				HttpClient.newHttpClient());

		byte[] dek = new byte[32];
		new SecureRandom().nextBytes(dek);

		WrappedKey wrapped = service.wrap(dek);
		byte[] unwrapped = service.unwrap(wrapped.getKeyId(), wrapped.getWrappedKeyBytes());

		assertArrayEquals(dek, unwrapped);
		assertEquals(2, requestCount.get());
	}

	@Test
	void unwrapRejectsMismatchedKeyIdWithoutMakingAnyRequest() throws Exception {
		AtomicInteger requestCount = new AtomicInteger();
		URI baseUrl = startServer(exchange -> {
			requestCount.incrementAndGet();
			respondJson(exchange, 200, "{\"data\":{}}");
		});
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		assertThrows(EncryptedBlobKeyException.class, () -> service.unwrap("some-other-key", new byte[] { 1 }));
		assertEquals(0, requestCount.get(), "a key-id mismatch must fail closed before any network call");
	}

	@Test
	void wrapThrowsOnNonOkResponse() throws Exception {
		URI baseUrl = startServer(exchange -> respondJson(exchange, 500, "{\"errors\":[\"internal error\"]}"));
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		assertThrows(EncryptedBlobKeyException.class, () -> service.wrap(new byte[32]));
	}

	@Test
	void wrapThrowsOnMalformedJsonResponse() throws Exception {
		URI baseUrl = startServer(exchange -> respondJson(exchange, 200, "not valid json"));
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		assertThrows(EncryptedBlobKeyException.class, () -> service.wrap(new byte[32]));
	}

	@Test
	void wrapThrowsWhenResponseIsMissingExpectedField() throws Exception {
		URI baseUrl = startServer(exchange -> respondJson(exchange, 200, "{\"data\":{}}"));
		VaultKeyEncryptionService service = new VaultKeyEncryptionService(baseUrl, "test-token", "my-key",
				HttpClient.newHttpClient());

		assertThrows(EncryptedBlobKeyException.class, () -> service.wrap(new byte[32]));
	}

	@Test
	void constructorRejectsMissingCredentials() {
		assertThrows(EncryptedBlobKeyException.class,
				() -> new VaultKeyEncryptionService(URI.create("http://localhost/"), "", "my-key"));
		assertThrows(EncryptedBlobKeyException.class,
				() -> new VaultKeyEncryptionService(URI.create("http://localhost/"), "token", ""));
	}

	/** Minimal helper to pull a string field's value out of a small hand-built JSON request body in tests. */
	private static String extractJsonStringField(String json, String field) {
		String marker = "\"" + field + "\":\"";
		int start = json.indexOf(marker) + marker.length();
		int end = json.indexOf('"', start);
		return json.substring(start, end);
	}
}
