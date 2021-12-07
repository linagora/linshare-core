/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractTwakeUserProvider;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.twake.client.TwakeUser;
import org.linagora.linshare.core.service.impl.twake.client.TwakeUsersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractTwakeUserProviderServiceImpl implements TwakeUserProviderService {

	public static final String API_COMPANIES_ENDPOINT = "/api/companies/";
	public static final String USERS_ENDPOINT = "/users";
	public static final String GUEST_ROLE = "guest";
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private final OkHttpClient client;
	private final ObjectMapper objectMapper;

	public AbstractTwakeUserProviderServiceImpl() {
		client = new OkHttpClient();
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected OkHttpClient client() {
		return client;
	}

	@Override
	public User findUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail) throws BusinessException {
		if (!isValid(domain)) {
			return null;
		}
		try (Response response = client().newCall(request(userProvider, Optional.of(USERS_ENDPOINT))).execute()) {
			if (!validateResponse(response, userProvider)) {
				return null;
			}

			TwakeUsersResponse twakeUsersResponse = objectMapper.readValue(response.body().bytes(), TwakeUsersResponse.class);
			return filterValidUser(twakeUsersResponse)
				.filter(filterBy(mail, TwakeUser::getEmail))
				.map(toInternalUser(domain))
				.findFirst()
				.orElse(null);
		} catch (IOException e) {
			LOGGER.error("Fails to connect to Twake Console with user provider %s", userProvider);
			return null;
		}
	}

	private Function<TwakeUser, Internal> toInternalUser(AbstractDomain domain) {
		return user -> {
			Internal internal = new Internal(user.getName(), user.getSurname(), user.getEmail(), user.getId());
			internal.setDomain(domain);
			internal.setRole(domain.getDefaultRole());
			return internal;
		};
	}

	protected abstract boolean isValid(AbstractDomain domain);

	private Predicate<TwakeUser> filterBy(String pattern, Function<TwakeUser, String> name) {
		return user -> {
			if (Strings.isNullOrEmpty(pattern)) {
				return true;
			}
			String value = name.apply(user);
			return !Strings.isNullOrEmpty(value) && value.contains(pattern);
		};
	}

	@Override
	public List<User> searchUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException {
		if (!isValid(domain)) {
			return ImmutableList.of();
		}
		try (Response response = client().newCall(request(userProvider, Optional.of(USERS_ENDPOINT))).execute()) {
			if (!validateResponse(response, userProvider)) {
				return ImmutableList.of();
			}

			TwakeUsersResponse twakeUsersResponse = objectMapper.readValue(response.body().bytes(), TwakeUsersResponse.class);
			return filterValidUser(twakeUsersResponse)
				.filter(filterBy(mail, TwakeUser::getEmail))
				.filter(filterBy(firstName, TwakeUser::getName))
				.filter(filterBy(lastName, TwakeUser::getSurname))
				.map(toInternalUser(domain))
				.collect(Collectors.toUnmodifiableList());
		} catch (IOException e) {
			LOGGER.error("Fails to connect to Twake Console with user provider %s", userProvider);
			return ImmutableList.of();
		}
	}

	private boolean validateResponse(Response response, AbstractTwakeUserProvider userProvider) {
		if (!response.isSuccessful()) {
			LOGGER.error("Twake didn't answer successfully: " + response.message());
			LOGGER.error("" + response);
			return false;
		}
		return true;
	}

	protected abstract Stream<TwakeUser> filterValidUser(TwakeUsersResponse twakeUsersResponse) throws IOException ;

	@Override
	public List<User> autoCompleteUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String pattern) throws BusinessException {
		if (!isValid(domain)) {
			return ImmutableList.of();
		}
		try (Response response = client().newCall(request(userProvider, Optional.of(USERS_ENDPOINT))).execute()) {
			if (!validateResponse(response, userProvider)) {
				return ImmutableList.of();
			}
			TwakeUsersResponse twakeUsersResponse = objectMapper.readValue(response.body().bytes(), TwakeUsersResponse.class);
			return filterValidUser(twakeUsersResponse)
				.filter(filterBy(pattern, TwakeUser::getEmail))
				.map(toInternalUser(domain))
				.collect(Collectors.toUnmodifiableList());
		} catch (Exception e) {
			LOGGER.error("An exception occurred with autocomplete on: " + userProvider, e);
			return ImmutableList.of();
		}
	}

	@Override
	public List<User> autoCompleteUser(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String firstName, String lastName) throws BusinessException {
		if (!isValid(domain)) {
			return ImmutableList.of();
		}
		try (Response response = client().newCall(request(userProvider, Optional.of(USERS_ENDPOINT))).execute()) {
			if (!validateResponse(response, userProvider)) {
				return ImmutableList.of();
			}

			TwakeUsersResponse twakeUsersResponse = objectMapper.readValue(response.body().bytes(), TwakeUsersResponse.class);
			return filterValidUser(twakeUsersResponse)
				.filter(filterBy(firstName, TwakeUser::getName))
				.filter(filterBy(lastName, TwakeUser::getSurname))
				.map(toInternalUser(domain))
				.collect(Collectors.toUnmodifiableList());
		} catch (Exception e) {
			LOGGER.error("An exception occurred with autocomplete on: " + userProvider, e);
			return ImmutableList.of();
		}
	}

	@Override
	public Boolean isUserExist(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String mail) throws BusinessException {
		return findUser(domain, userProvider, mail) != null;
	}

	@Override
	public User auth(AbstractTwakeUserProvider userProvider, String login, String userPasswd) throws BusinessException {
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	@Override
	public User searchForAuth(AbstractDomain domain, AbstractTwakeUserProvider userProvider, String login) throws BusinessException {
		if (!isValid(domain)) {
			return null;
		}
		try (Response response = client().newCall(request(userProvider, Optional.of(USERS_ENDPOINT))).execute()) {
			if (!validateResponse(response, userProvider)) {
				return null;
			}

			TwakeUsersResponse twakeUsersResponse = objectMapper.readValue(response.body().bytes(), TwakeUsersResponse.class);
			return filterValidUser(twakeUsersResponse)
				.filter(user -> user.getEmail().equals(login))
				.map(toInternalUser(domain))
				.findFirst()
				.orElse(null);
		} catch (IOException e) {
			LOGGER.error("Fails to connect to Twake Console with user provider %s", userProvider);
			return null;
		}
	}

	private Request request(AbstractTwakeUserProvider userProvider, Optional<String> extraPath) {
		String basic = Credentials.basic(userProvider.getTwakeConnection().getClientId(), userProvider.getTwakeConnection().getClientSecret());
		return new Request.Builder()
			.url(httpUrlFrom(userProvider, extraPath))
			.header("Authorization", basic)
			.header("Accept", "application/json")
			.build();
	}

	protected HttpUrl httpUrlFrom(AbstractTwakeUserProvider userProvider, Optional<String> extraPath) {
		return HttpUrl.parse(userProvider.getTwakeConnection().getProviderUrl()
			+ API_COMPANIES_ENDPOINT
			+ userProvider.getTwakeCompanyId()
			+ extraPath.orElse(""));
	}
}
