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
package org.linagora.linshare.webservice.delegationv2.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.LogEntryServiceImpl;
import org.linagora.linshare.core.service.impl.SharedSpaceNodeServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        "classpath:springContext-facade-ws-admin.xml",
        "classpath:springContext-facade-ws-user.xml",
        "classpath:springContext-facade-ws-delegation.xml",
        "classpath:springContext-task-executor.xml",
        "classpath:springContext-batches.xml",
        "classpath:springContext-webservice-delegationv2.xml",
        "classpath:springContext-test.xml" })
public class DocumentDelegationRestServiceImplTest {

    public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";

    @Autowired
    private DocumentRestServiceImpl testee;
    @Autowired
    private SharedSpaceNodeServiceImpl nodeService;
    @Autowired
    private DomainServiceImpl domainService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    LogEntryServiceImpl logEntryService;

    private User root;
    private User john;

    private PhaseInterceptorChain chain;

    private Message message;
    private Exchange exchange;

    @Before
    public void setUpp() {
        message = mock(Message.class);
        exchange = mock(Exchange.class);
        when(message.getExchange()).thenReturn(exchange);
        exchange.put("org.linagora.linshare.webservice.interceptor.start_time",  new Date().getTime() - 1000L);

        Phase phase1 = new Phase("phase1", 1);
        SortedSet<Phase> phases = new TreeSet<>();
        phases.add(phase1);

        chain = new PhaseInterceptorChain(phases);
        PhaseInterceptorChain.setCurrentMessage(chain, message);
    }

    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        AbstractDomain topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
        john = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotCreateDocument() {
        assertThatThrownBy(() -> testee.create(LinShareConstants.defaultRootMailAddress,
                getFileInputStream(), "test", "test",
                        null,  null, null, null, false, 0L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }


    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotCreateDocument() {
        assertThatThrownBy(() -> testee.create("d896140a-39c0-11e5-b7f9-080027b8274b",
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotCreateDocument() {
        assertThatThrownBy(() ->  testee.create("aebe1b64-39c0-11e5-9fa8-080027b8254j",
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCanCreateDocumentWithPermissions() throws IOException {
        DocumentDto sharedSpaceNode =  testee.create(TECHNICAL_USER_CREATE_NODE,
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null);
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotCreateDocumentWithoutPermissions() {
        assertThatThrownBy(() ->  testee.create(TECHNICAL_USER_NONE,
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
    }


    private static FileInputStream getFileInputStream() throws IOException {
        return new FileInputStream(File.createTempFile("my-text-file.1", "txt"));
    }

}