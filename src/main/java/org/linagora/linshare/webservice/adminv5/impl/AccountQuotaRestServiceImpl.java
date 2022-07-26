/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Date;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AccountQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.AccountQuotaRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
@Path("/domains/{domainUuid}/statistics/account_quotas")
public class AccountQuotaRestServiceImpl extends WebserviceBase implements AccountQuotaRestService {

	private final AccountQuotaFacade accountQuotaFacade;

	private static PagingResponseBuilder<AccountQuotaDto> pageResponseBuilder = new PagingResponseBuilder<>();

	public AccountQuotaRestServiceImpl(AccountQuotaFacade accountQuotaFacade) {
		super();
		this.accountQuotaFacade = accountQuotaFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Get storage consumption statistics. By default will be a 12 rolling months.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response findAll(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(
					description = "Include nested domains.",
					schema = @Schema(implementation = Boolean.class)
				)
				@QueryParam("includeNestedDomains") @DefaultValue("false") boolean includeNestedDomains,
			@Parameter(
					description = "The admin can choose the order of sorting the stat's list to retrieve, if not set the ascending order will be applied by default.",
					required = false,
					schema = @Schema(implementation = SortOrder.class, defaultValue = "ASC")
				)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(
					description = "The admin can choose the field to sort with the stat's list to retrieve, if not set the statisticDate date order will be choosen by default.",
					required = false,
					schema = @Schema(implementation = AccountQuotaDtoField.class, defaultValue = "batchModificationDate")
				)
				@QueryParam("sortField") @DefaultValue("batchModificationDate") String sortField,
			@Parameter(
					description = "begin statistic creation date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("beginDate") String beginDate,
			@Parameter(
					description = "end statistic creation date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("endDate") String endDate,
			@Parameter(
					description = "The admin can choose the page number to get.",
					required = false
				)
				@QueryParam("page") Integer pageNumber,
			@Parameter(
					description = "The admin can choose the number of elements to get.",
					required = false
				)
				@QueryParam("size") @DefaultValue("100") Integer pageSize) {
		PageContainer<AccountQuotaDto> container = accountQuotaFacade.findAll(
				domainUuid, includeNestedDomains,
				SortOrder.valueOf(sortOrder),
				AccountQuotaDtoField.valueOf(sortField),
				Optional.ofNullable(beginDate),
				Optional.ofNullable(endDate), pageNumber,
				pageSize);
		return pageResponseBuilder.build(container);
	}

	@Override
	public AccountQuotaDto find(String domainUuid, String uuid, boolean realTime) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
