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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ShareRecipientStatisticDto;
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
@Sql({ "/import-tests-share-entry-group-setup.sql"})
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
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
        "classpath:springContext-webservice-adminv5.xml",
        "classpath:springContext-facade-ws-adminv5.xml",
        "classpath:springContext-facade-ws-user.xml",
        "classpath:springContext-webservice-admin.xml",
        "classpath:springContext-facade-ws-admin.xml",
        "classpath:springContext-webservice.xml",
        "classpath:springContext-upgrade-v2-0.xml",
        "classpath:springContext-facade-ws-async.xml",
        "classpath:springContext-task-executor.xml",
        "classpath:springContext-batches.xml",
        "classpath:springContext-test.xml" })
public class SharesRestServiceImplTest {
    @Autowired
    private ShareRestServiceImpl testee;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final double DAY_MILLISECONDS = 8.64E7;


	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeValuesCheck() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isNotEmpty();
        assertThat(topSharesByFileSize.size()).isEqualTo(3);

        ShareRecipientStatisticDto external = topSharesByFileSize.get(0);
        assertThat(external.getRecipientType()).isEqualTo("external");
        assertThat(external.getRecipientUuid()).isEqualTo("");
        assertThat(external.getRecipientMail()).isEqualTo("yoda@linshare.org");
        assertThat(external.getDomainUuid()).isEqualTo("");
        assertThat(external.getDomainLabel()).isEqualTo("");
        assertThat(external.getShareCount()).isEqualTo(3L);
        assertThat(external.getShareTotalSize()).isEqualTo(12288L);

        ShareRecipientStatisticDto internal = topSharesByFileSize.get(1);
        assertThat(internal.getRecipientType()).isEqualTo("internal");
        assertThat(internal.getRecipientUuid()).isEqualTo("d896140a-39c0-11e5-b7f9-080027b8274b");
        assertThat(internal.getRecipientMail()).isEqualTo("user2@linshare.org");
        assertThat(internal.getDomainUuid()).isEqualTo("MyDomain");
        assertThat(internal.getDomainLabel()).isEqualTo("MyDomain");
        assertThat(internal.getShareCount()).isEqualTo(4L);
        assertThat(internal.getShareTotalSize()).isEqualTo(4096L);
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeIsOrdered() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isNotEmpty();
		assertThat(topSharesByFileSize.size()).isEqualTo(3);
        assertThat(topSharesByFileSize.stream()
                .map(ShareRecipientStatisticDto::getShareTotalSize)
                .collect(Collectors.toList()))
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeSuperAdmin() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(List.of("MyDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isNotEmpty();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getTopSharesByFileSizeAdmin() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(List.of("MyDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isNotEmpty();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getTopSharesByFileSizeAdminForbiddenDomain() {
        assertThatThrownBy(() -> testee.getTopSharesByFileSize(List.of("MyDomain", LinShareConstants.rootDomainIdentifier), getDate(-6), getDate(-4), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not allowed to manage this domain : LinShareRootDomain");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getTopSharesByFileSizeAdminMultipleDomains() {
        List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(List.of("MyDomain", "MySubDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

        assertThat(topSharesByFileSize).isNotEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeDomain() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(List.of("MySubDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeNoDomainAllowed() {
		List<ShareRecipientStatisticDto> topSharesByFileSize =
                testee.getTopSharesByFileSize(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileSize).isNotEmpty();
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getTopSharesByFileSizeWrongDomain() {
        assertThatThrownBy(() -> testee.getTopSharesByFileSize(List.of("not a domain"), null, null, 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("The current domain does not exist : not a domain");
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeOnlyBegin() {
        assertThat(testee.getTopSharesByFileSize(null, getDate(-5), null, 0, 50)
                .getPageResponse().getContent()).isNotEmpty();
        assertThat(testee.getTopSharesByFileSize(null, getDate(-4), null, 0, 50)
                .getPageResponse().getContent()).isEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeImpossibleDate() {
        assertThatThrownBy(() -> testee.getTopSharesByFileSize(null, getDate(-6), getDate(-7), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End date cannot be before begin date.");
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileSizeNotDate() {
        assertThatThrownBy(() -> testee.getTopSharesByFileSize(null, "not a date", getDate(-7), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot parse the dates.");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountValuesCheck() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isNotEmpty();
        assertThat(topSharesByFileCount.size()).isEqualTo(3);

        ShareRecipientStatisticDto internal = topSharesByFileCount.get(0);
        assertThat(internal.getRecipientType()).isEqualTo("internal");
        assertThat(internal.getRecipientUuid()).isEqualTo("d896140a-39c0-11e5-b7f9-080027b8274b");
        assertThat(internal.getRecipientMail()).isEqualTo("user2@linshare.org");
        assertThat(internal.getDomainUuid()).isEqualTo("MyDomain");
        assertThat(internal.getDomainLabel()).isEqualTo("MyDomain");
        assertThat(internal.getShareCount()).isEqualTo(4L);
        assertThat(internal.getShareTotalSize()).isEqualTo(4096L);

        ShareRecipientStatisticDto external = "external".equals(topSharesByFileCount.get(1).getRecipientType()) ? topSharesByFileCount.get(1) : topSharesByFileCount.get(2);
        assertThat(external.getRecipientType()).isEqualTo("external");
        assertThat(external.getRecipientUuid()).isEqualTo("");
        assertThat(external.getRecipientMail()).isEqualTo("yoda@linshare.org");
        assertThat(external.getDomainUuid()).isEqualTo("");
        assertThat(external.getDomainLabel()).isEqualTo("");
        assertThat(external.getShareCount()).isEqualTo(3L);
        assertThat(external.getShareTotalSize()).isEqualTo(12288L);
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountIsOrdered() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isNotEmpty();
		assertThat(topSharesByFileCount.size()).isEqualTo(3);
        assertThat(topSharesByFileCount.stream()
                .map(ShareRecipientStatisticDto::getShareCount)
                .collect(Collectors.toList()))
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountSuperAdmin() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(List.of("MyDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isNotEmpty();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getTopSharesByFileCountAdmin() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(List.of("MyDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isNotEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountDomain() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(List.of("MySubDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isEmpty();
    }


    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void getTopSharesByFileCountAdminForbiddenDomain() {
        assertThatThrownBy(() -> testee.getTopSharesByFileCount(List.of("MyDomain", LinShareConstants.rootDomainIdentifier), getDate(-6), getDate(-4), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not allowed to manage this domain : LinShareRootDomain");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void getTopSharesByFileCountAdminMultipleDomains() {
        List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(List.of("MyDomain", "MySubDomain"), getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

        assertThat(topSharesByFileCount).isNotEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountNoDomainAllowed() {
		List<ShareRecipientStatisticDto> topSharesByFileCount =
                testee.getTopSharesByFileCount(null, getDate(-6), getDate(-4), 0, 50)
                        .getPageResponse().getContent();

		assertThat(topSharesByFileCount).isNotEmpty();
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void getTopSharesByFileCountWrongDomain() {
        assertThatThrownBy(() -> testee.getTopSharesByFileCount(List.of("not a domain"), null, null, 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("The current domain does not exist : not a domain");
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountOnlyBegin() {
        assertThat(testee.getTopSharesByFileCount(null, getDate(-5), null, 0, 50)
                .getPageResponse().getContent()).isNotEmpty();
        assertThat(testee.getTopSharesByFileCount(null, getDate(-4), null, 0, 50)
                .getPageResponse().getContent()).isEmpty();
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountImpossibleDate() {
        assertThatThrownBy(() -> testee.getTopSharesByFileCount(null, getDate(-6), getDate(-7), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End date cannot be before begin date.");
    }

	@Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getTopSharesByFileCountNotDate() {
        assertThatThrownBy(() -> testee.getTopSharesByFileCount(null, "not a date", getDate(-7), 0, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot parse the dates.");
    }

    private String getDate() {
        return getDate(0);
    }
    private String getDate(long dayShift) {
        return DATE_FORMAT.format(System.currentTimeMillis() + dayShift * DAY_MILLISECONDS);
    }

}