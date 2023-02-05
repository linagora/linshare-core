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
package org.linagora.linshare.utils;

import java.net.InetSocketAddress;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientTestHelper {

	protected java.net.InetSocketAddress address;

	public MongoClientTestHelper(InetSocketAddress address) {
		super();
		this.address = address;
	}

	public MongoClient getClient() {
		String connectionString = "mongodb://" + address.getHostString() + ":" + address.getPort();
		MongoClient client = MongoClients.create(connectionString);
		return client;
	}
}
