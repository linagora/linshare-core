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
package org.linagora.linshare.core.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.DomainType;

import java.util.UUID;

public class AbstractDomainTest {

	private static class MyDomain extends AbstractDomain {

		public MyDomain(AbstractDomain parent) {
			super("Name", parent);
			this.uuid = UUID.randomUUID().toString();
		}

		@Override
		public DomainType getDomainType() {
			return null;
		}
	}

	@Test
	public void isAncestryShouldReturnTrueWhenSameDomain() {
		MyDomain domain = new MyDomain(null);

		assertThat(domain.isAncestry(domain.getUuid()))
				.isTrue();
	}

	@Test
	public void isAncestryShouldReturnTrueWhenSameDomainWithParent() {
		MyDomain parent = new MyDomain(null);
		MyDomain domain = new MyDomain(parent);

		assertThat(domain.isAncestry(domain.getUuid()))
				.isTrue();
	}

	@Test
	public void isAncestryShouldReturnTrueWhenParentDomain() {
		MyDomain parent = new MyDomain(null);
		MyDomain domain = new MyDomain(parent);

		assertThat(domain.isAncestry(parent.getUuid()))
				.isTrue();
	}

	@Test
	public void isAncestryShouldReturnTrueWhenGrandParentDomain() {
		MyDomain grandParent = new MyDomain(null);
		MyDomain parent = new MyDomain(grandParent);
		MyDomain domain = new MyDomain(parent);

		assertThat(domain.isAncestry(grandParent.getUuid()))
				.isTrue();
	}

	@Test
	public void isAncestryShouldReturnFalseWhenDomainIsNotAnAncestral() {
		MyDomain otherDomain = new MyDomain(null);
		MyDomain domain = new MyDomain(null);

		assertThat(domain.isAncestry(otherDomain.getUuid()))
				.isFalse();
	}
}
