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
package org.linagora.linshare.repository.hibernate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.linagora.linshare.core.domain.constants.ModeratorRole.ADMIN;
import static org.linagora.linshare.core.domain.constants.ModeratorRole.SIMPLE;

@ExtendWith(SpringExtension.class)
@Sql(value = {"/import-tests-guests.sql" })
@ContextConfiguration(locations={
        "classpath:springContext-test.xml",
        "classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
@Transactional
public class UserRepositoryGuestsTest {

    @Autowired
    @Qualifier("userRepository")
    private UserRepository<User> userRepository;

    @Test
    public void findGuestWithOrWithoutModeratorsTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.empty(), Optional.of(5), null);
        assertNotNull(guests);
        assertEquals(21, guests.size());
    }

    @Test
    public void findGuestWithModeratorsTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.of(0), Optional.empty(), null);
        assertNotNull(guests);
        assertEquals(2, guests.size());
    }

    @Test
    public void findGuestWithSimpleModeratorRoleTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.of(0), Optional.empty(), SIMPLE);
        assertNotNull(guests);
        assertEquals(1, guests.size());
    }

    @Test
    public void findGuestWithoutModeratorsTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.empty(), Optional.of(1), null);
        assertNotNull(guests);
        assertEquals(19, guests.size());
    }

    @Test
    public void findGuestWithMoreThanOneModeratorTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.of(1), Optional.empty(), null);
        assertNotNull(guests);
        assertEquals(1, guests.size());
    }

    @Test
    public void findGuestWithMoreThanOneAdminModeratorTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.of(1), Optional.empty(), ADMIN);
        assertNotNull(guests);
        assertEquals(1, guests.size());
    }

    @Test
    public void findGuestWithMoreThanTwoAdminModeratorTest() {
        Set<Long> guests = userRepository.findGuestWithModerators(Optional.of(2), Optional.empty(), ADMIN);
        assertNotNull(guests);
        assertEquals(0, guests.size());
    }
}
