/*
 * Copyright (C) 2024 - LINAGORA This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

/**
 * The logging sub-system of LinShare Core is Log4J-2. So, when another logging
 * system exists it must be redirected to Log4J-2.
 */
public class LoggingTest {

	/**
	 * JBoss Logger providers are prohibited
	 */
	@Test
	public void noJBossLogginProvider() throws ClassNotFoundException {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Class<?> loggerProviderClass = cl.loadClass("org.jboss.logging.LoggerProvider");
		final ServiceLoader<?> loader = ServiceLoader.load(loggerProviderClass,
				Thread.currentThread().getContextClassLoader());
		assertTrue(loader.findFirst().isEmpty(), "A JBoss Logging provider is present in the classloader");
	}

}
