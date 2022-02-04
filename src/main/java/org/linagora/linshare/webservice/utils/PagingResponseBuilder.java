/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.webservice.utils;

import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.data.domain.Page;

public class PagingResponseBuilder<T> {

	public PagingResponseBuilder() {
		super();
	}

	public Response build(GenericEntity<List<T>> entity) {
		ResponseBuilder response = Response.ok(entity);
		return response.build();
	}

	public Response build(PageContainer<T> container) {
		ResponseBuilder response = Response.ok(container.getPageResponse().getContent());
		setPagingHeaderToResponse(response, container);
		return response.build();
	}

	public Response build(GenericEntity<List<T>> entity, PageContainer<T> container) {
		ResponseBuilder response = Response.ok(entity);
		setPagingHeaderToResponse(response, container);
		return response.build();
	}

	private void setPagingHeaderToResponse(ResponseBuilder response, PageContainer<T> container) {
		response.header("Total-Elements", container.getPageResponse().getTotalElements());
		response.header("Total-Pages", container.getPageResponse().getTotalPages());
		response.header("Current-Page", container.getPageNumber());
		response.header("Current-Page-Size", container.getPageSize());
		response.header("First", container.getPageResponse().isFirst());
		response.header("Last", container.getPageResponse().isLast());
		response.header("Default-Page-Size", container.getDefaultPageRequest().getPageSize());
	}

	public Response build(GenericEntity<List<T>> entity, Page<T> container) {
		ResponseBuilder response = Response.ok(entity);
		setPagingHeaderPageToResponse(response, container);
		return response.build();
	}

	private void setPagingHeaderPageToResponse(ResponseBuilder response, Page<T> container) {
		response.header("Total-Elements", container.getTotalElements());
		response.header("Total-Pages", container.getTotalPages());
		response.header("First", container.isFirst());
		response.header("Last", container.isLast());
	}

}
