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
package org.linagora.linshare.webservice.uploadrequestv5.impl;

import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.webservice.uploadrequestv4.ChangeUploadRequestPasswordUrlRestService;

public class ChangeUploadRequestUrlPasswordRestServiceImpl
		extends org.linagora.linshare.webservice.uploadrequestv4.impl.ChangeUploadRequestUrlPasswordRestServiceImpl implements ChangeUploadRequestPasswordUrlRestService{

	public ChangeUploadRequestUrlPasswordRestServiceImpl(UploadRequestUrlFacade requestUrlFacade) {
		super(requestUrlFacade);
	}

}
