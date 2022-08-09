/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.fields.AuditEntryField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.adminv5.AuditLogEntryFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.webservice.adminv5.AuditLogEntryRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/audit")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class AuditLogEntryRestServiceImpl implements AuditLogEntryRestService {

	private final AuditLogEntryFacade auditLogFacade;

	private static PagingResponseBuilder<AuditLogEntry> pageResponseBuilder = new PagingResponseBuilder<>();

	public AuditLogEntryRestServiceImpl(AuditLogEntryFacade facade) {
		super();
		this.auditLogFacade = facade;
	}


	@Path("/{uuid}")
	@GET
	@Operation(summary = "TODO", responses = {
		@ApiResponse(
			content = @Content(schema = @Schema(implementation = AuditLogEntry.class)),
			responseCode = "200"
		)
	})
	@Override
	public AuditLogEntry find(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "audit's uuid")
				@PathParam("uuid") String uuid) {
		return auditLogFacade.find(domainUuid, uuid);
	}


	@Path("/")
	@GET
	@Operation(summary = "TODO", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntry.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response findAll(
			@Parameter(description = "domain's uuid")
				@PathParam("domainUuid") String domainUuid,
			@Parameter(
					description = "Include nested domains, from domainUuid path param.",
					schema = @Schema(implementation = Boolean.class)
				)
				@QueryParam("includeNestedDomains") @DefaultValue("false") boolean includeNestedDomains,
			@Parameter(
					description = "Include a list of domains, it override the value provided by domainUuid path param."
							+ "NB: query param not used if includeNestedDomains is true.",
					schema = @Schema(implementation = Boolean.class)
				)
				@QueryParam("domain") Set<String> domains,
			@Parameter(
					description = "The admin can choose the order of sorting the audit's list to retrieve, if not set the ascending order will be applied by default.",
					required = false,
					schema = @Schema(implementation = SortOrder.class, defaultValue = "ASC")
				)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(
					description = "The admin can choose the field to sort with the audit's list to retrieve, if not set the creationDate field will be choosen by default.",
					required = false,
					schema = @Schema(implementation = AuditEntryField.class, defaultValue = "creationDate")
				)
				@QueryParam("sortField") @DefaultValue("creationDate") String sortField,
			@Parameter(
					description = "The admin can choose the type of LogAction to retrieve.",
					required = false,
					schema = @Schema(implementation = LogAction.class)
				)
				@QueryParam("action") List<String> logActions,
			@Parameter(
					description = "The admin can choose the type of resources to retrieve.",
					required = false,
					schema = @Schema(implementation = AuditLogEntryType.class)
					)
				@QueryParam("type") List<String> types,
			@Parameter(
					description = "The admin can choose the type of resources to retrieve.",
					required = false,
					schema = @Schema(implementation = AuditGroupLogEntryType.class)
				)
				@QueryParam("resourceGroups") List<String> resourceGroups,
			@Parameter(
					description = "The admin can choose the type of resources to exclude."
							+ "NB: query param not used if query param 'type' is provided.",
					required = false,
					schema = @Schema(implementation = AuditLogEntryType.class)
				)
				@QueryParam("excludedType") List<String> excludedTypes,
			@Parameter(
					description = "Filter the audit traces by auth user uuid."
							+ "You just need to provide an account uuid.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("authUser") String authUser,
			@Parameter(
					description = "Filter the audit traces by actor uuid."
							+ "You just need to provide an account uuid.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("actor") String actor,
			@Parameter(
					description = "Return any audit traces related to an account uuid."
							+ "You just need to provide an account uuid.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("relatedAccount") String relatedAccount,
			@Parameter(
					description = "Filter the audit traces by resource uuid."
							+ "You just need to provide a resource uuid.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("resource") String resource,
			@Parameter(
					description = "Return any audit traces related to a resource uuid."
							+ "You just need to provide a resource uuid.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("relatedResource") String relatedResource,
			@Parameter(
					description = "Filter the audit traces by resource name."
							+ "You just need to provide a pattern, ex *myFile*.",
					required = false,
					schema = @Schema(implementation = String.class)
				)
				@QueryParam("resourceName") String resourceName,
			@Parameter(
					description = "begin creation date. format: yyyy-MM-dd",
					schema = @Schema(implementation = Date.class)
				)
				@QueryParam("beginDate") String beginDate,
			@Parameter(
					description = "end creation date. format: yyyy-MM-dd",
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
		PageContainer<AuditLogEntry> container = auditLogFacade.findAll(
				domainUuid, includeNestedDomains,
				domains,
				SortOrder.valueOf(sortOrder),
				AuditEntryField.valueOf(sortField),
				logActions.stream().map(name -> LogAction.valueOf(name)).collect(Collectors.toSet()),
				types.stream().map(name -> AuditLogEntryType.valueOf(name)).collect(Collectors.toSet()),
				resourceGroups.stream().map(name -> AuditGroupLogEntryType.valueOf(name)).collect(Collectors.toSet()),
				excludedTypes.stream().map(name -> AuditLogEntryType.valueOf(name)).collect(Collectors.toSet()),
				Optional.ofNullable(authUser),
				Optional.ofNullable(actor),
				Optional.ofNullable(relatedAccount),
				Optional.ofNullable(resource),
				Optional.ofNullable(relatedResource),
				Optional.ofNullable(resourceName),
				Optional.ofNullable(beginDate),
				Optional.ofNullable(endDate),
				pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}
}
