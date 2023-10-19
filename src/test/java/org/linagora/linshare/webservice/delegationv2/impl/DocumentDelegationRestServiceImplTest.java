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
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-technical-users.sql"})
@Sql({ "/import-tests-document-entry-setup.sql"})
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

    public static final String TECHNICAL_USER_CREATE_DOCUMENT = "technical.create.document@linshare.org";
    public static final String TECHNICAL_USER_LIST_DOCUMENT = "technical.list.document@linshare.org";
    public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";
    public static final String USER1_UUID = "aebe1b64-39c0-11e5-9fa8-080027b8274b";

    @Autowired
    private DocumentRestServiceImpl testee;


    // Mocks
    private Message message;
    private Exchange exchange;
    private MockedStatic<PhaseInterceptorChain> phaseInterceptorChainMockedStatic;
    private AbstractDomain rootDomain;

    @BeforeEach
    public void setUp() {
        message = mock(Message.class);
        exchange = mock(Exchange.class);
        phaseInterceptorChainMockedStatic = Mockito.mockStatic(PhaseInterceptorChain.class);
        when(message.getExchange()).thenReturn(exchange);
        when(exchange.containsKey("org.linagora.linshare.webservice.interceptor.start_time")).thenReturn(true);
        when(exchange.get("org.linagora.linshare.webservice.interceptor.start_time")).thenReturn(new Date().getTime() - 1000L);
        when(PhaseInterceptorChain.getCurrentMessage()).thenReturn(message);

    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(message, exchange);
        phaseInterceptorChainMockedStatic.close();
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
    @WithMockUser(TECHNICAL_USER_CREATE_DOCUMENT)
	public void technicalUserCanCreateDocumentWithPermissions() throws IOException {
        DocumentDto document =  testee.create("aebe1b64-39c0-11e5-9fa8-080027b8254j",
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null);
        assertThat(document).isNotNull();
    }

	@Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCannotCreateDocumentWithWrongPermissions() {
        assertThatThrownBy(() ->  testee.create(TECHNICAL_USER_CREATE_NODE,
                getFileInputStream(), "test", "test",
                null,  null, null, null, false, 0L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
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

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotGetAllDocument() {
        assertThatThrownBy(() -> testee.findAll(LinShareConstants.defaultRootMailAddress))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotGetAllDocument() {
        assertThatThrownBy(() -> testee.findAll("d896140a-39c0-11e5-b7f9-080027b8274b"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotGetAllDocument() {
        assertThatThrownBy(() ->  testee.findAll("aebe1b64-39c0-11e5-9fa8-080027b8254j"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_LIST_DOCUMENT)
	public void technicalUserCanGetAllDocumentWithPermissions() throws IOException {
        List<DocumentDto> documents =  testee.findAll(USER1_UUID);
        assertThat(documents).isNotNull();
        assertThat(documents).isNotEmpty();
    }

	@Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCannotGetAllDocumentWithWrongPermissions() {
        assertThatThrownBy(() ->  testee.findAll(TECHNICAL_USER_CREATE_NODE))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to list all entries.");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotGetAllDocumentWithoutPermissions() {
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