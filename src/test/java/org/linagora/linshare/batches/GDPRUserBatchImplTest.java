/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.batches;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.utils.TestingTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


@ExtendWith({ SpringExtension.class})
@Transactional
@ContextConfiguration(locations = {
	"classpath:springContext-datasource.xml",
	"classpath:springContext-dao.xml",
	"classpath:springContext-ldap.xml",
	"classpath:springContext-mongo.xml",
	"classpath:springContext-storage-jcloud.xml",
	"classpath:springContext-repository.xml",
	"classpath:springContext-business-service.xml",
	"classpath:springContext-rac.xml",
	"classpath:springContext-service-miscellaneous.xml",
	"classpath:springContext-service.xml",
	"classpath:springContext-mongo-init.xml",
	"classpath:springContext-batches.xml",
	"classpath:springContext-test.xml",
	"classpath:springContext-overriding.xml" })
public class GDPRUserBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("GDPRUserBatch")
	private GenericBatch testee;

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("timeService")
	private TimeService timeService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("contactListService")
	private ContactListService contactListService;

	@Autowired
	@Qualifier("guestService")
	private GuestService guestService;

	@Autowired
	@Qualifier("functionalityService")
	private FunctionalityService functionalityService;

	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService sharedSpaceNodeService;

	@Autowired
	@Qualifier("sharedSpaceNodeBusinessService")
	private SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService;

	@Autowired
	@Qualifier("workGroupNodeService")
	private WorkGroupNodeService workGroupNodeService;

	@Autowired
	@Qualifier("sharedSpaceRoleBusinessService")
	private SharedSpaceRoleBusinessService sharedSpaceRoleBusinessService;

	@Autowired
	@Qualifier("threadService")
	private ThreadService threadService;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService sharedSpaceMemberService;

	private User john;
	private User root;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		root = userRepository.findByMail(LinShareTestConstants.ROOT_ACCOUNT);
		((TestingTimeService) timeService).setReference(new Date());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void gdprBatchShouldNotFailWhenNoData() {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(testee);
		batchRunner.execute(ImmutableList.of(testee));
	}

	@Test
	public void gdprBatchShouldAnonymizeUsers() {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(john);
		userRepository.purge(john);

		batchRunner.execute(ImmutableList.of(testee));

		User result = userRepository.findActivateAndDestroyedByLsUuid(john.getLsUuid());
		assertThat(result.getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
		assertThat(result.getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
		assertThat(result.getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
	}

	@Test
	public void gdprBatchShouldAnonymizeContactList() {
		createContactList();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(john);
		userRepository.purge(john);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("resource.owner.uuid").is(john.getLsUuid()));
		List<MailingListAuditLogEntry> mailingListAuditLogEntries = mongoTemplate.find(query, MailingListAuditLogEntry.class);
		assertThat(mailingListAuditLogEntries).isNotEmpty();
		mailingListAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getOwner().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	private void createContactList() {
		ContactList contactList = new ContactList();
		contactList.setOwner(john);
		contactList.setIdentifier("myContactList");
		contactListService.create(john, john, contactList);
	}

	@Test
	public void gdprBatchShouldAnonymizeGuest() {
		Guest guest = createModerator();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(guest);
		userRepository.purge(guest);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("resource.uuid").is(guest.getLsUuid()));
		List<GuestAuditLogEntry> guestAuditLogEntries = mongoTemplate.find(query, GuestAuditLogEntry.class);
		assertThat(guestAuditLogEntries).isNotEmpty();
		guestAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	@Test
	public void gdprBatchShouldAnonymizeModerator() {
		List<AuditLogEntry> all = mongoTemplate.findAll(AuditLogEntry.class);
		assertThat(all).isNotEmpty();
		all.stream()
			.forEach(System.out::println);

		createModerator();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(john);
		userRepository.purge(john);

		batchRunner.execute(ImmutableList.of(testee));

		all = mongoTemplate.findAll(AuditLogEntry.class);
		assertThat(all).isNotEmpty();
		all.stream()
			.forEach(System.out::println);

		Query query = Query.query(Criteria.where("resource.account.uuid").is(john.getLsUuid()));
		List<ModeratorAuditLogEntry> moderatorAuditLogEntries = mongoTemplate.find(query, ModeratorAuditLogEntry.class);
		assertThat(moderatorAuditLogEntries).isNotEmpty();
		moderatorAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getAccount().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getAccount().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getAccount().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	private Guest createModerator() {
		Functionality functionality = functionalityService.find(root, FunctionalityNames.GUESTS.toString());
		Policy activationPolicy = functionality.getActivationPolicy();
		activationPolicy.setStatus(true);
		functionalityService.update(root, root.getDomainId(), functionality);

		Guest guest = new Guest();
		guest.setMail("mail");
		guest.setFirstName("firstName");
		guest.setLastName("lastName");
		List<String> restrictedMails = new ArrayList<>();
		return guestService.create(john, john, guest, restrictedMails);
	}

	@Test
	public void gdprBatchShouldAnonymizeSharedSpace() {
		createSharedSpace();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(john);
		userRepository.purge(john);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("account.uuid").is(john.getLsUuid()));
		List<SharedSpaceMember> sharedSpaceMembers = mongoTemplate.find(query, SharedSpaceMember.class);
		assertThat(sharedSpaceMembers).isNotEmpty();
		sharedSpaceMembers.stream()
			.forEach(entry -> {
				assertThat(entry.getAccount().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getAccount().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getAccount().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});

		Query nodeQuery = Query.query(Criteria.where("author.uuid").is(john.getLsUuid()));
		List<SharedSpaceNode> sharedSpaceNodes = mongoTemplate.find(nodeQuery, SharedSpaceNode.class);
		assertThat(sharedSpaceNodes).isNotEmpty();
		sharedSpaceNodes.stream()
			.forEach(entry -> {
				assertThat(entry.getAuthor().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getAuthor().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getAuthor().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});

		Query memberLogQuery = Query.query(Criteria.where("resource.account.uuid").is(john.getLsUuid()));
		List<SharedSpaceMemberAuditLogEntry> sharedSpaceMemberAuditLogEntries = mongoTemplate.find(memberLogQuery, SharedSpaceMemberAuditLogEntry.class);
		assertThat(sharedSpaceMemberAuditLogEntries).isNotEmpty();
		sharedSpaceMemberAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getAccount().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getAccount().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getAccount().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	private void createSharedSpace() {
		SharedSpaceNode node = new SharedSpaceNode();
		node.setNodeType(NodeType.WORK_SPACE);
		sharedSpaceNodeService.create(john, john, node);
	}

	@Test
	public void gdprBatchShouldAnonymizeWorkGroup() {
		createWorkGroup();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		userRepository.delete(john);
		userRepository.purge(john);

		batchRunner.execute(ImmutableList.of(testee));

		List<WorkGroupNode> all = mongoTemplate.findAll(WorkGroupNode.class);
		assertThat(all).isNotEmpty();
		Query query = Query.query(Criteria.where("lastAuthor.uuid").is(john.getLsUuid()));
		List<WorkGroupNode> workGroupNodes = mongoTemplate.find(query, WorkGroupNode.class);
		assertThat(workGroupNodes).isNotEmpty();
		workGroupNodes.stream()
			.forEach(entry -> {
				assertThat(entry.getLastAuthor().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getLastAuthor().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getLastAuthor().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	private void createWorkGroup() {
		WorkGroup workGroup = threadService.create(john, john, "MyWorkGroup");

		SharedSpaceNode sharedSpaceNode = new SharedSpaceNode(workGroup.getName(), null, NodeType.WORK_GROUP);
		sharedSpaceNode.setUuid(workGroup.getLsUuid());
		sharedSpaceNode.setDomainUuid(john.getDomainId());
		sharedSpaceNode = sharedSpaceNodeBusinessService.create(sharedSpaceNode);

		WorkGroupNode workGroupNode = new WorkGroupNode(new AccountMto(john), "MyNode", null, workGroup.getName());
		workGroupNode.setNodeType(WorkGroupNodeType.FOLDER);

		workGroupNodeService.create(root, john, workGroup, workGroupNode, false, false);

		SharedSpaceRole adminRole = sharedSpaceRoleBusinessService.findByName("ADMIN");
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminRole);
		sharedSpaceMemberService.create(john, john, sharedSpaceNode, context, new SharedSpaceAccount(john));
	}
}
