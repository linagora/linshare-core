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
package org.linagora.linshare.webservice.adminv5.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.LogEntryServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.DocumentEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked") //For lists casting
@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-technical-users.sql"})
@ContextConfiguration(locations = {
        "classpath:springContext-datasource.xml",
        "classpath:springContext-dao.xml",
        "classpath:springContext-ldap.xml",
        "classpath:springContext-repository.xml",
        "classpath:springContext-mongo.xml",
        "classpath:springContext-service.xml",
        "classpath:springContext-service-miscellaneous.xml",
        "classpath:springContext-rac.xml",
        "classpath:springContext-mongo-init.xml",
        "classpath:springContext-storage-jcloud.xml",
        "classpath:springContext-business-service.xml",
        "classpath:springContext-webservice.xml",
        "classpath:springContext-upgrade-v2-0.xml",
        "classpath:springContext-facade-ws-async.xml",
        "classpath:springContext-facade-ws-adminv5.xml",
        "classpath:springContext-facade-ws-admin.xml",
        "classpath:springContext-facade-ws-user.xml",
        "classpath:springContext-task-executor.xml",
        "classpath:springContext-batches.xml",
        "classpath:springContext-webservice-adminv5.xml",
        "classpath:springContext-webservice-admin.xml",
        "classpath:springContext-test.xml" })
public class AuditLogEntryRestServiceImplTest {

    public static final String TECHNICAL_USER_AUDIT = "technical.audit@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final double DAY_MILLISECONDS = 8.64E7;

    @Autowired
    private AuditLogEntryRestServiceImpl testee;
    @Autowired
    private DomainServiceImpl domainService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    LogEntryServiceImpl logEntryService;
    @Autowired
    @Qualifier("basicStatisticMongoRepository")
    private BasicStatisticMongoRepository basicStatisticMongoRepository;
    @Autowired
    @Qualifier("auditUserMongoRepository")
    private AuditUserMongoRepository auditUserMongoRepository;

    private User root;
    private User john;
    private User jane;


    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        AbstractDomain topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
        john = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
        jane = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JANE_ACCOUNT);

        //TODO: Mongo DB should be cleaned between each test instead of having to do it manually
        // and better still, we should insert test data into mongo directly
        basicStatisticMongoRepository.deleteAll();
        auditUserMongoRepository.deleteAll();

        logEntryService.insert(new UserAuditLogEntry(root, john, LogAction.CREATE, AuditLogEntryType.USER, john));
        logEntryService.insert(new UserAuditLogEntry(john, john, LogAction.UPDATE, AuditLogEntryType.USER, john));
        logEntryService.insert(new UserAuditLogEntry(john, jane, LogAction.UPDATE, AuditLogEntryType.USER, john));

        DocumentEntry documentEntry = generateFakeDocument();
        logEntryService.insert(new DocumentEntryAuditLogEntry(john, john, documentEntry, LogAction.CREATE));
        logEntryService.insert(new DocumentEntryAuditLogEntry(john, john, documentEntry, LogAction.DOWNLOAD));
        logEntryService.insert(new DocumentEntryAuditLogEntry(john, john, documentEntry, LogAction.DOWNLOAD));

        ShareEntry shareEntry = generateFakeShareEntry(documentEntry);
        logEntryService.insert(new ShareEntryAuditLogEntry(john, john, LogAction.CREATE, shareEntry, AuditLogEntryType.SHARE_ENTRY));
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCanGetAudit() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotGetRootAudits() {
        assertThatThrownBy(() -> testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not allowed to query this domain");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCanGetTopAudits() {
        List<AuditLogEntry> audits = testee.findAll(john.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotGetAudits() {
        assertThatThrownBy(() -> testee.findAll(john.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_AUDIT)
	public void technicalUserCannotGetAuditsEvenWithPermissions() {
        assertThatThrownBy(() -> testee.findAll(john.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotGetAuditsWithoutPermissions() {
        assertThatThrownBy(() -> testee.findAll(john.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterWithDomainUUID() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), false,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterWithDomains() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), false,
                Set.of(root.getDomainId(), john.getDomainId()), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditIncludeNestedDomainsOverridesDomains() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(root.getDomainId()), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByAction() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of("DOWNLOAD"),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByType() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of("USER"), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(3);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByResourceGroup() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of("ACCOUNTS"), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(3);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByExcludedTypes() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of("USER"), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(4);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByAuthUser() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), john.getLsUuid(),
                null, null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(6);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByActor() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                jane.getLsUuid(), null, null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByActorEmail() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, jane.getMail(), null, null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByRecipientEmail() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, jane.getMail(), null, null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterByRelatedAccount() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null,null,  john.getLsUuid(), null,
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterResource() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, "fakeZip",
                null, null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(3);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterRelatedResource() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                "fakeZip", null, null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(4);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterResourceName() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, "test", null, null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(4);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterDateRange() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, getDate(-1), getDate(1),
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(7);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterDateBegin() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, getDate(1), null,
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(0);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterDateEnd() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, getDate(-1),
                null, 100).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(0);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterPageSize() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                null, 5).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(5);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterPageNumber() {
        List<AuditLogEntry> audits = testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, null, null,
                1, 5).readEntity(List.class);

        assertThat(audits).isNotNull();
        assertThat(audits.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getAuditFilterWrongDate() {
        assertThatThrownBy(() ->  testee.findAll(root.getDomainId(), true,
                Set.of(), "ASC", "creationDate", List.of(),
                List.of(), List.of(), List.of(), null,
                null, null, null, null, null,
                null, null, getDate(1), getDate(-1),
                null, 100))
                .isInstanceOf(BusinessException.class)
                .hasMessage("begin date (" + getDate(1) + ") must be before end date (" + getDate(-1) + ")");
    }

    @NotNull
    private DocumentEntry generateFakeDocument() {
        Document aDocument = new Document("fakeZipDocument", "fake.zip", "application/zip", Calendar.getInstance(), null, john, false, false, 10000L);
        DocumentEntry documentEntry = new DocumentEntry(john, "test", "test", aDocument);
        documentEntry.setCreationDate(Calendar.getInstance());
        documentEntry.setModificationDate(Calendar.getInstance());
        documentEntry.setUuid("fakeZip");
        return documentEntry;
    }

    @NotNull
    private ShareEntry generateFakeShareEntry(DocumentEntry documentEntry) {
        ShareEntryGroup shareEntryGroup = new ShareEntryGroup(john, "test group");
        Calendar date = Calendar.getInstance();
        date.setTime(Calendar.getInstance().getTime());
        ShareEntry shareEntry = new ShareEntry(john, "test share", "test comment", jane, documentEntry, date, shareEntryGroup);
        shareEntry.setCreationDate(date);
        shareEntry.setModificationDate(date);
        return shareEntry;
    }

    private String getDate() {
        return getDate(0);
    }
    private String getDate(long dayShift) {
        return DATE_FORMAT.format(System.currentTimeMillis() + dayShift * DAY_MILLISECONDS);
    }
}