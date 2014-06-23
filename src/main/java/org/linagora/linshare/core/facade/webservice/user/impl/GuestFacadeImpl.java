package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.webservice.dto.UserDto;

import com.google.common.collect.Lists;

public class GuestFacadeImpl extends GenericFacadeImpl implements GuestFacade {
	
	private final GuestService guestService;

	public GuestFacadeImpl(final AccountService accountService,
			final GuestService guestService) {
		super(accountService);
		this.guestService = guestService;
	}
	
	@Override
	public UserDto find(String lsUuid) throws BusinessException {
		User actor = checkAuthentication();
		return UserDto.getFull(guestService.findByLsUuid(actor, lsUuid));
	}

	@Override
	public List<UserDto> getRestrictedContacts(String lsUuid)
			throws BusinessException {
		User actor = checkAuthentication();
		Guest guest = guestService.findByLsUuid(actor, lsUuid);
		Set<AllowedContact> contacts = guest.getContacts();
		List<UserDto> dtos = Lists.newArrayList();
		for (AllowedContact contact : contacts) {
			dtos.add(UserDto.getSimple(contact.getContact()));
		}
		return dtos;
	}

	@Override
	public UserDto create(UserDto guest, String ownerLsUuid) throws BusinessException {
		User actor = checkAuthentication();
		return UserDto.getFull(guestService.create(actor, new Guest(guest), ownerLsUuid));
	}

	@Override
	public UserDto create(UserDto guest) throws BusinessException {
		User actor = checkAuthentication();
		return UserDto.getFull(guestService.create(actor, new Guest(guest), actor.getLsUuid()));
	}

	@Override
	public UserDto update(UserDto guest) throws BusinessException {
		User actor = checkAuthentication();
		return UserDto.getFull(guestService.update(actor, new Guest(guest), guest.getOwner().getUuid()));
	}

	@Override
	public void delete(UserDto guest) throws BusinessException {
		User actor = checkAuthentication();
		guestService.delete(actor, guest.getUuid());
	}

	@Override
	public void delete(String lsUuid) throws BusinessException {
		User actor = checkAuthentication();
		guestService.delete(actor, lsUuid);
	}
}
