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
package org.linagora.linshare.core.notifications.config;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

public class LinShareTemplateResource implements ITemplateResource {

	protected final String resource;

	protected final String baseName;

	protected String messages;

	public LinShareTemplateResource(final String resource, final String baseName) {
		super();
		Validate.notNull(resource, "Resource cannot be null or empty");
		Validate.notEmpty(baseName, "BaseName cannot be null or empty");
		this.resource = resource;
		this.baseName = baseName;
		this.messages = null;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	@Override
	public String getDescription() {
		return resource;
	}

	@Override
	public String getBaseName() {
		if (messages != null) {
			return baseName;
		}
		// Optimization
		// we disable message resolutions when there is nothing, by returning null.
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	public Reader reader() throws IOException {
		return new StringReader(this.resource);
	}

	public ITemplateResource relative(final String relativeLocation) {
		// Optimization.
		// just looking for one resource named like NEW_SHARING.properties,
		// skipping other resources like NEW_SHARING_fr.properties or
		// NEW_SHARING_fr.*.properties
		if (relativeLocation.equals(baseName + ".properties")) {
			return new LinShareTemplateResource(messages, relativeLocation);
		}
		return new LinShareEmptyTemplateResource();
	}

}
