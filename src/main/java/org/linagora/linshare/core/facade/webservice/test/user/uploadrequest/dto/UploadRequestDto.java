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
package org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Sets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequestTest")
public class UploadRequestDto extends org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto {

	private Set<UploadRequestUrlDto> uploadRequestURLs = Sets.newHashSet();

	public UploadRequestDto() {
		super();
	}

	protected UploadRequestDto(UploadRequest entity, boolean full) {
		super(entity, full);
		for (UploadRequestUrl url : entity.getUploadRequestURLs()) {
			this.uploadRequestURLs.add(new UploadRequestUrlDto(url));
		}
	}

	public Set<UploadRequestUrlDto> getUploadRequestURLs() {
		return uploadRequestURLs;
	}

	public void setUploadRequestURLs(Set<UploadRequestUrlDto> uploadRequestURLs) {
		this.uploadRequestURLs = uploadRequestURLs;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadRequest, UploadRequestDto> transform(boolean full) {
		return new Function<UploadRequest, UploadRequestDto>() {
			@Override
			public UploadRequestDto apply(UploadRequest request) {
				return new UploadRequestDto(request, full);
			}
		};
	}
}