/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.admin.MailContentFacade;
import org.linagora.linshare.core.facade.webservice.admin.MailFooterFacade;
import org.linagora.linshare.core.facade.webservice.admin.MailLayoutFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainRestService;
import org.linagora.linshare.webservice.dto.DomainDto;
import org.linagora.linshare.webservice.dto.FunctionalityDto;
import org.linagora.linshare.webservice.dto.MailContentDto;
import org.linagora.linshare.webservice.dto.MailFooterDto;
import org.linagora.linshare.webservice.dto.MailLayoutDto;

@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainRestServiceImpl extends WebserviceBase implements DomainRestService {

	private final DomainFacade domainFacade;

	private final FunctionalityFacade functionalityFacade;

	private final MailContentFacade mailContentFacade;

	private final MailFooterFacade mailFooterFacade;

	private final MailLayoutFacade mailLayoutFacade;

	public DomainRestServiceImpl(final DomainFacade webServiceDomainFacade,
			final FunctionalityFacade webServiceFunctionalityFacade,
			final MailContentFacade mailContentFacade,
			final MailLayoutFacade mailLayoutFacade,
			final MailFooterFacade mailFooterFacade) {
		this.domainFacade = webServiceDomainFacade;
		this.functionalityFacade = webServiceFunctionalityFacade;
		this.mailContentFacade = mailContentFacade;
		this.mailLayoutFacade = mailLayoutFacade;
		this.mailFooterFacade = mailFooterFacade;
	}

	@Path("/")
	@GET
	@Override
	public List<DomainDto> getDomains() throws BusinessException {
		return domainFacade.getDomains();
	}

	@Path("/{domain}")
	@GET
	@Override
	public DomainDto getDomain(@PathParam(value = "domain") String domain, @QueryParam("tree") @DefaultValue("false") boolean tree) throws BusinessException {
		return domainFacade.getDomain(domain, tree);
	}

	@Path("/")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void createDomain(DomainDto domain) throws BusinessException {
		domainFacade.createDomain(domain);
	}

	@Path("/")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void updateDomain(DomainDto domain) throws BusinessException {
		domainFacade.updateDomain(domain);
	}

	@Path("/")
	@DELETE
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void deleteDomain(DomainDto domain) throws BusinessException {
		domainFacade.deleteDomain(domain);
	}

	/*
	 * Functionalities
	 */

	@Path("/{domain}/functionalities")
	@GET
	@Override
	public List<FunctionalityDto> getDomainFunctionalities(@PathParam(value = "domain") String domain) throws BusinessException {
		return functionalityFacade.getAll(domain);
	}

	@Path("/{domain}/functionalities/{identifier}")
	@GET
	@Override
	public FunctionalityDto getDomainFunctionality(
			@PathParam(value = "domain") String domain,
			@PathParam(value = "identifier") String identifier)
					throws BusinessException {
		return functionalityFacade.get(domain, identifier);
	}

	@Path("/{domain}/functionalities")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void updateDomainFunctionality(@PathParam(value = "domain") String domain, FunctionalityDto func) throws BusinessException {
		functionalityFacade.update(domain, func);
	}

	@Path("/{domain}/functionalities")
	@DELETE
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public void deleteDomainFunctionality(@PathParam(value = "domain") String domain, FunctionalityDto func) throws BusinessException {
		functionalityFacade.delete(domain, func);
	}

	@Path("/{domain}/mail_contents/")
	@GET
	@Override
	public List<MailContentDto> getMailContents(
			@PathParam(value = "domain") String domain)
			throws BusinessException {
		return mailContentFacade.getMailContents(domain);
	}

	@Path("/{domain}/mail_footers/")
	@GET
	@Override
	public List<MailFooterDto> getMailFooters(
			@PathParam(value = "domain") String domain)
			throws BusinessException {
		return mailFooterFacade.getMailFooters(domain);
	}

	@Path("/{domain}/mail_layouts/")
	@GET
	@Override
	public List<MailLayoutDto> getMailLayouts(
			@PathParam(value = "domain") String domain)
			throws BusinessException {
		return mailLayoutFacade.getMailLayouts(domain);
	}
}
