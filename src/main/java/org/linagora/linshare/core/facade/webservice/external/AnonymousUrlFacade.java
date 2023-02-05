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
package org.linagora.linshare.core.facade.webservice.external;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.core.facade.webservice.external.dto.ShareEntryDto;

import com.google.common.io.ByteSource;

public interface AnonymousUrlFacade {

	AnonymousUrlDto find(String uuid, String password);

	ShareEntryDto getShareEntry(String anonymousUrlUuid, String anonymousShareEntryUuid, String password);

	ByteSource download(String anonymousUrlUuid, String anonymousShareEntryUuid, String password);

	ByteSource getThumbnail(String anonymousUrlUuid, String anonymousShareEntryUuid, String password, ThumbnailType kind);
}
