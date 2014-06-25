package org.linagora.linshare.core.facade.webservice.user.impl;

import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.webservice.dto.UserDto;

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
	public UserDto create(UserDto guestDto, String ownerLsUuid)
			throws BusinessException {
		User actor = checkAuthentication();
		Guest guest = retreiveGuest(guestDto);
		return UserDto.getFull(guestService.create(actor, guest, ownerLsUuid));
	}

	@Override
	public UserDto create(UserDto guestDto) throws BusinessException {
		User actor = checkAuthentication();
		Guest guest = retreiveGuest(guestDto);
		return UserDto.getFull(guestService.create(actor, guest,
				actor.getLsUuid()));
	}

	@Override
	public UserDto update(UserDto guestDto) throws BusinessException {
		User actor = checkAuthentication();
		return UserDto.getFull(guestService.update(actor, new Guest(guestDto),
				guestDto.getOwner().getUuid()));
	}

	@Override
	public void delete(UserDto guestDto) throws BusinessException {
		User actor = checkAuthentication();
		guestService.delete(actor, guestDto.getUuid());
	}

	@Override
	public void delete(String lsUuid) throws BusinessException {
		User actor = checkAuthentication();
		guestService.delete(actor, lsUuid);
	}

	/**
	 * HELPERS
	 */

	private Guest retreiveGuest(UserDto guestDto) {
		Guest guest = new Guest(guestDto);
		if (guest.isRestricted()) {
			for (UserDto contact : guestDto.getRestrictedContacts()) {
				guest.addContact(new AllowedContact(guest,
						new Internal(contact)));
			}
		}
		return guest;
	}
}
