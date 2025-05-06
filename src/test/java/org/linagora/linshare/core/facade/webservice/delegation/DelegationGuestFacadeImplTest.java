package org.linagora.linshare.core.facade.webservice.delegation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.UtilGuestAuthor;
import org.linagora.linshare.core.facade.webservice.common.fragment.GuestFacadeFragment;
import org.linagora.linshare.core.facade.webservice.delegation.impl.GuestFacadeImpl;
import org.linagora.linshare.core.facade.webservice.test.AbstractGuestFacadeTest;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.ModeratorServiceImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Nonnull;

/**
 * Unit tests of {@link GuestFacadeImpl} from the package 'Delegation'.
 */
@ExtendWith(MockitoExtension.class)
class DelegationGuestFacadeImplTest extends AbstractGuestFacadeTest {

	@Override
	protected GuestFacadeFragment createFacadeImplementation(
			@Nonnull final AccountService accountService,
			@Nonnull final GuestService guestService,
			@Nonnull final UserService userService,
			@Nonnull final PasswordService passwordService,
			@Nonnull final ModeratorServiceImpl moderatorService,
			@Nonnull final MongoTemplate mongoTemplate,
			@Nonnull final UtilGuestAuthor utilGuestAuthor
	) {
		return new TestDelegationGuestFacade(
				accountService,
				guestService,
				userService,
				passwordService,
				moderatorService,
				mongoTemplate
		);
	}

	/**
	 * Test implementation of the delegation GuestFacadeImpl that overrides authentication methods
	 * and implements TestGuestFacade interface
	 */
	private static class TestDelegationGuestFacade extends GuestFacadeImpl {

		public TestDelegationGuestFacade(
				final AccountService accountService,
				final GuestService guestService,
				final UserService userService,
				final PasswordService passwordService,
				final ModeratorServiceImpl moderatorService,
				final MongoTemplate mongoTemplate
		) {
			super(
					accountService,
					guestService,
					userService,
					passwordService,
					moderatorService,
					mongoTemplate
			);
		}

		@Override
		@Nonnull
		protected User checkAuthentication() {
			final AbstractGuestFacadeTest.TestUser authUser = new AbstractGuestFacadeTest.TestUser();
			authUser.setLsUuid("auth-user-uuid");
			return authUser;
		}

		@Override
		@Nonnull
		protected User getActor(@Nonnull final Account authUser, @Nonnull final String actorUuid) {
			return (User) authUser;
		}
	}
}