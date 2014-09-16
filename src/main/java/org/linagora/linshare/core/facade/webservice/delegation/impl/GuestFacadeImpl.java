package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.GuestDto;

import com.google.common.collect.Lists;

public class GuestFacadeImpl extends DelegationGenericFacadeImpl implements
		GuestFacade {

	private GuestService guestService;

	public GuestFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final GuestService guestService) {
		super(accountService, userService);
		this.guestService = guestService;
	}

	@Override
	public GuestDto find(String ownerUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.find(actor, owner, uuid));
	}

	@Override
	public GuestDto find(String ownerUuid, String domain, String mail)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(mail, "Missing required guest mail");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.find(actor, owner, domain, mail));
	}

	@Override
	public List<GuestDto> findAll(String ownerUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		List<GuestDto> res = Lists.newArrayList();
		List<Guest> guests = guestService.findAllMyGuests(actor, owner);
		for (Guest guest : guests) {
			res.add(GuestDto.getFull(guest));
		}
		return res;
	}

	@Override
	public GuestDto create(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Guest guest = new Guest(guestDto);
		return GuestDto.getFull(guestService.create(actor, owner, guest));

	}

	@Override
	public GuestDto update(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Guest guest = new Guest(guestDto);
		return GuestDto.getFull(guestService.update(actor, owner, guest));
	}

	@Override
	public void delete(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		guestService.delete(actor, owner, guestDto.getUuid());
	}

	@Override
	public void delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		guestService.delete(actor, owner, uuid);
	}

}
