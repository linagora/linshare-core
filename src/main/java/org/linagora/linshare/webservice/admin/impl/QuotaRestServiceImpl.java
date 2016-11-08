/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.webservice.admin.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.QuotaFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContainerQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.QuotaRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/rest/admin/quota", description = "Quota administration service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/quotas")
public class QuotaRestServiceImpl extends WebserviceBase implements QuotaRestService {

	private static final String DOMAIN = "domain";
	private static final String ACCOUNT = "account";
	private static final String ENSEMBLE_TYPE = "ensembleType";

	private QuotaFacade quotaFacade;

	public QuotaRestServiceImpl(QuotaFacade quotaFacade) {
		this.quotaFacade = quotaFacade;
	}

	@Path("/account_quota")
	@PUT
	@ApiOperation(value = "Update an account quota", response = AccountQuotaDto.class)
	@Override
	public AccountQuotaDto update(@ApiParam(value = "Account quota to update.", required = true) AccountQuotaDto entity)
			throws BusinessException {
		return quotaFacade.update(entity);
	}

	@Path("/domain_quota")
	@PUT
	@ApiOperation(value = "Update a domain quota", response = DomainQuotaDto.class)
	@Override
	public DomainQuotaDto update(@ApiParam(value = "Domain quota to update.", required = true) DomainQuotaDto entity)
			throws BusinessException {
		return quotaFacade.update(entity);
	}

	@Path("/ensemble_quota")
	@PUT
	@ApiOperation(value = "Update an ensemble quota", response = ContainerQuotaDto.class)
	@Override
	public ContainerQuotaDto update(
			@ApiParam(value = "Ensemble quota to update.", required = true) ContainerQuotaDto entity)
					throws BusinessException {
		return quotaFacade.update(entity);
	}

	@Path("/account_quota/{account}")
	@GET
	@ApiOperation(value = "find an account quota", response = AccountQuotaDto.class)
	@Override
	public AccountQuotaDto findAccountQuota(
			@ApiParam(value = "Account Uuid", required = true) @PathParam(ACCOUNT) String accountUuid)
					throws BusinessException {
		return quotaFacade.findAccountQuota(accountUuid);
	}

	@Path("/domain_quota/{domain}")
	@GET
	@ApiOperation(value = "Find a domain quota", response = DomainQuotaDto.class)
	@Override
	public DomainQuotaDto findDomainQuota(
			@ApiParam(value = "Domain identifier", required = true) @PathParam(DOMAIN) String domain)
					throws BusinessException {
		return quotaFacade.findDomainQuota(domain);
	}

	@Path("/domain_quota/{domain}/{ensembleType}")
	@GET
	@ApiOperation(value = "Find an ensemble quota", response = ContainerQuotaDto.class)
	@Override
	public ContainerQuotaDto findEnsembleQuota(
			@ApiParam(value = "Domain identifier", required = true) @PathParam(DOMAIN) String domain,
			@ApiParam(value = "Ensemble type", required = true) @PathParam(ENSEMBLE_TYPE) String ensemble)
					throws BusinessException {
		return quotaFacade.findEnsembleQuota(domain, ensemble);
	}

}
