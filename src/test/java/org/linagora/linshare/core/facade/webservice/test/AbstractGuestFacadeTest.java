package org.linagora.linshare.core.facade.webservice.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.UtilGuestAuthor;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.fragment.GuestFacadeFragment;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.ModeratorServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Nonnull;

/**
 * <p>Abstract base class for unit tests of implementations of facade services 'GuestFacade' (ie. extending
 * {@link GuestFacadeFragment}):</p>
 * <ul>
 * <li>{@link org.linagora.linshare.core.facade.webservice.delegation.impl.GuestFacadeImpl},</li>
 * <li>or, {@link org.linagora.linshare.core.facade.webservice.user.impl.GuestFacadeImpl}.</li>
 * </ul>
 */
public abstract class AbstractGuestFacadeTest {

	@Mock
	protected GuestService guestService;

	@Mock
	protected UserService userService;

	@Mock
	protected AccountService accountService;

	@Mock
	protected MongoTemplate mongoTemplate;

	@Mock
	protected ModeratorServiceImpl moderatorService;

	@Mock
	protected PasswordService passwordService;

	protected GuestFacadeFragment guestFacade;

	protected UtilGuestAuthor utilGuestAuthor;

	/**
	 * Implemented by concrete test classes to create the appropriate facade implementation
	 */
	protected abstract GuestFacadeFragment createFacadeImplementation(
			@Nonnull final AccountService accountService,
			@Nonnull final GuestService guestService,
			@Nonnull final UserService userService,
			@Nonnull final PasswordService passwordService,
			@Nonnull final ModeratorServiceImpl moderatorService,
			@Nonnull final MongoTemplate mongoTemplate,
			@Nonnull final UtilGuestAuthor utilGuestAuthor
	);

	@BeforeEach
	void setUp() {
		this.utilGuestAuthor = new UtilGuestAuthor(this.mongoTemplate);
		this.guestFacade = this.createFacadeImplementation(
				this.accountService,
				this.guestService,
				this.userService,
				this.passwordService,
				this.moderatorService,
				this.mongoTemplate,
				this.utilGuestAuthor
		);
	}

	/**
	 * Verifies that updating a guest with a null contact list
	 */
	@Test
	void updateWithNullRestrictedContactList() {
		final Version version = Version.V5;
		final String actorUuid = "actor-uuid";
		final String guestUuid = "guest-uuid";

		final GuestDto guestDto = new GuestDto();
		guestDto.setUuid(guestUuid);
		guestDto.setRestrictedContactList(null);
		guestDto.setFirstName("John");
		guestDto.setLastName("Doe");
		guestDto.setMail("john.doe@example.com");

		final Guest guestEntity = new Guest();
		guestEntity.setLsUuid(guestUuid);
		guestEntity.setFirstName("John");
		guestEntity.setLastName("Doe");
		guestEntity.setMail("john.doe@example.com");
		guestEntity.setDomain(new GuestDomain("guest-domain"));

		final User fakeAuthor = new Internal() {
			@Override
			public String getAccountRepresentation() {
				return "Fake Author";
			}
		};
		fakeAuthor.setFirstName("Fake");
		fakeAuthor.setLastName("Author");
		fakeAuthor.setMail("fake.author@linshare.org");

		when(this.mongoTemplate.findOne(any(Query.class), eq(AuditLogEntryUser.class))).thenReturn(null);
		when(this.moderatorService.findByActorAndGuest(any(), any(), any())).thenReturn(Optional.empty());
		when(this.guestService.update(any(), any(), any(), any(), any(), any())).thenReturn(guestEntity);

		final GuestDto updatedGuest = this.guestFacade.update(version, actorUuid, guestDto, guestUuid);

		assertNotNull(updatedGuest);
		assertEquals("John", updatedGuest.getFirstName());
	}

	/**
	 * Test updating a guest with an empty contact list.
	 */
	@Test
	void updateWithEmptyRestrictedContactList() {
		final Version version = Version.V5;
		final String actorUuid = "actor-uuid";
		final String guestUuid = "guest-uuid";

		final GuestDto guestDto = new GuestDto();
		guestDto.setUuid(guestUuid);
		guestDto.setRestrictedContactList(Collections.emptyList());
		guestDto.setRestrictedContacts(new ArrayList<>());
		guestDto.setFirstName("Jane");
		guestDto.setLastName("Smith");
		guestDto.setMail("jane.smith@example.com");

		final Guest guestEntity = createSampleGuest(guestUuid);
		guestEntity.setFirstName("Jane");
		guestEntity.setLastName("Smith");
		when(this.guestService.update(any(), any(), any(), anyList(), anyList(), any())).thenReturn(guestEntity);

		final GuestDto updatedGuest = this.guestFacade.update(version, actorUuid, guestDto, guestUuid);

		assertNotNull(updatedGuest);
		assertEquals("Jane", updatedGuest.getFirstName());

		final ArgumentCaptor<List<String>> contactUuidCaptor = ArgumentCaptor.forClass(List.class);
		verify(this.guestService).update(any(), any(), any(), anyList(), contactUuidCaptor.capture(), any());
		assertEquals(0, contactUuidCaptor.getValue().size(), "ContactUuid list should be empty");
	}

	/**
	 * Test updating a guest with a non-empty contact list.
	 */
	@Test
	void updateWithNonEmptyRestrictedContactList() {
		final Version version = Version.V5;
		final String actorUuid = "actor-uuid";
		final String guestUuid = "guest-uuid";

		final ContactListDto contact1 = new ContactListDto();
		contact1.setUuid("contactlist-uuid-1");
		contact1.setName("Friends");

		final ContactListDto contact2 = new ContactListDto();
		contact2.setUuid("contactlist-uuid-2");
		contact2.setName("Colleagues");

		final List<ContactListDto> contactList = Arrays.asList(contact1, contact2);

		final GenericUserDto user1 = new GenericUserDto();
		user1.setMail("contact1@example.com");

		final GenericUserDto user2 = new GenericUserDto();
		user2.setMail("contact2@example.com");

		final List<GenericUserDto> restrictedContacts = Arrays.asList(user1, user2);

		final GuestDto guestDto = new GuestDto();
		guestDto.setUuid(guestUuid);
		guestDto.setRestrictedContactList(contactList);
		guestDto.setRestrictedContacts(restrictedContacts);
		guestDto.setFirstName("Robert");
		guestDto.setLastName("Johnson");
		guestDto.setMail("robert.johnson@example.com");
		guestDto.setRestricted(true);

		final Guest guestEntity = createSampleGuest(guestUuid);
		guestEntity.setFirstName("Robert");
		guestEntity.setLastName("Johnson");
		guestEntity.setRestricted(true);
		when(this.guestService.update(any(), any(), any(), anyList(), anyList(), any())).thenReturn(guestEntity);

		final GuestDto updatedGuest = this.guestFacade.update(version, actorUuid, guestDto, guestUuid);

		assertNotNull(updatedGuest);
		assertEquals("Robert", updatedGuest.getFirstName());

		final ArgumentCaptor<List<String>> contactUuidCaptor = ArgumentCaptor.forClass(List.class);
		verify(this.guestService).update(any(), any(), any(), anyList(), contactUuidCaptor.capture(), any());
		final List<String> capturedContactUuids = contactUuidCaptor.getValue();
		assertEquals(2, capturedContactUuids.size(), "ContactUuid list should contain 2 items");
		assertEquals("contactlist-uuid-1", capturedContactUuids.get(0));
		assertEquals("contactlist-uuid-2", capturedContactUuids.get(1));

		final ArgumentCaptor<List<String>> emailCaptor = ArgumentCaptor.forClass(List.class);
		verify(this.guestService).update(any(), any(), any(), emailCaptor.capture(), anyList(), any());
		final List<String> capturedEmails = emailCaptor.getValue();
		assertEquals(2, capturedEmails.size(), "Email list should contain 2 items");
		assertEquals("contact1@example.com", capturedEmails.get(0));
		assertEquals("contact2@example.com", capturedEmails.get(1));
	}

	/**
	 * Helper method to create a sample Guest entity for testing
	 */
	private static @Nonnull Guest createSampleGuest(@Nonnull final String uuid) {
		final Guest guestEntity = new Guest();
		guestEntity.setLsUuid(uuid);
		guestEntity.setFirstName("Sample");
		guestEntity.setLastName("Guest");
		guestEntity.setMail("sample.guest@example.com");
		guestEntity.setDomain(new GuestDomain("guest-domain"));
		return guestEntity;
	}

	/**
	 * Test User implementation for mock authentication
	 */
	public static class TestUser extends Internal {
		@Override
		public String getAccountRepresentation() {
			return "Test User";
		}
	}
}