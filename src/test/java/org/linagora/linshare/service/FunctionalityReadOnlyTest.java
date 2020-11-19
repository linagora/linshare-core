/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

package org.linagora.linshare.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class FunctionalityReadOnlyTest {

	private static Logger logger = LoggerFactory.getLogger(FunctionalityReadOnlyTest.class);

	@Autowired
	FunctionalityReadOnlyService functionalityService;
	
	@Autowired
	FunctionalityBusinessService functionalityBusinessService;

	@Autowired
	private AbstractDomainRepository domainRepository;

	AbstractDomain domain;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		domain = domainRepository.findById(LinShareTestConstants.TOP_DOMAIN);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Sql("/import-tests-reset-fake-functionality.sql")
	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testUploadRequestMaxFileCountFunctionality_GetIntegerValue() {
		IntegerValueFunctionality func = functionalityService
				.getUploadRequestMaxFileCountFunctionality(domain);
		// the user value is over the maximum value, we need to throws business exception
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			functionalityService.getIntegerValue(func, func.getMaxValue() + 1,
					BusinessErrorCode.UPLOAD_REQUEST_INTEGER_VALUE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_INTEGER_VALUE_INVALID, exception.getErrorCode());
		// the user value is > 0 and < maximum value, so we need to keep the user value.
		Integer integerValue = functionalityService.getIntegerValue(func, func.getMaxValue() - 1,
				BusinessErrorCode.UPLOAD_REQUEST_INTEGER_VALUE_INVALID);
		Assertions.assertEquals(integerValue, func.getMaxValue() - 1);
	}

	@Test
	public void testUploadRequestMaxFileSizeFunctionality_GetSizeValue() {
		SizeUnitValueFunctionality func = functionalityService.getUploadRequestMaxFileSizeFunctionality(domain);
		// the user value is over the maximum value, we need to throws business exception
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			long currentValue = ((FileSizeUnitClass) func.getUnit()).getPlainSize(func.getMaxValue() + 1L);
			functionalityService.getSizeValue(func, currentValue, BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID, exception.getErrorCode());
		// the user value is > 0 and < maximum value, so we need to keep the user value.
		long currentValue = ((FileSizeUnitClass) func.getMaxUnit()).getPlainSize(func.getMaxValue() - 1L);
		long sizeValue = functionalityService.getSizeValue(func, currentValue,
				BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		Assertions.assertEquals(sizeValue, currentValue);
		// delegation is not activated:
		Policy policy = new Policy(Policies.ALLOWED);
		policy.setStatus(false);
		func.setDelegationPolicy(policy);
		functionalityBusinessService.update(domain.getUuid(), func);
		long defaultSize = ((FileSizeUnitClass) func.getUnit()).getPlainSize(func.getValue());
		long size = functionalityService.getSizeValue(func, 1L,
				BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		Assertions.assertEquals(size, defaultSize);
	}

	@Sql("/import-tests-fake-functionality.sql")
	@Test
	public void testUploadRequestMaxDepositSizeFunctionality_GetSizeValue() {
		SizeUnitValueFunctionality func = functionalityService
				.getUploadRequestMaxDepositSizeFunctionality(domain);
		// the user value is over the maximum value, we need to throws business exception
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			long currentValue = ((FileSizeUnitClass) func.getUnit()).getPlainSize(func.getMaxValue() + 1L);
			functionalityService.getSizeValue(func, currentValue, BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID, exception.getErrorCode());
		// the user value is > 0 and < maximum value, so we need to keep the user value.
		long currentValue = ((FileSizeUnitClass) func.getUnit()).getPlainSize(func.getMaxValue() - 1L);
		long sizeValue = functionalityService.getSizeValue(func, currentValue, BusinessErrorCode.UPLOAD_REQUEST_SIZE_VALUE_INVALID);
		Assertions.assertEquals(sizeValue, currentValue);
	}

	@Sql("/import-tests-fake-functionality.sql")
	@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
	@Test
	public void testUploadRequestActivationTimeFunctionality_GetDateValue() {
		TimeUnitValueFunctionality func = functionalityService.getUploadRequestActivationTimeFunctionality(domain);
		// Case 1:
		// the user activation date is out of range | expected throws business exception
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		Date userActivationDate = c.getTime();
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			functionalityService.getDateValue(func, userActivationDate,
					BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		// Case 2:
		// the user activation date is > 0 and < maximum value, so we need to keep the
		// user value.
		c.add(Calendar.DATE, +2);
		Date userActivationDate1 = c.getTime();
		Date activationDate = functionalityService.getDateValue(func, userActivationDate1,
				BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		Assertions.assertEquals(userActivationDate1, activationDate);
		// Case 3:
		// There is no limit we need to return the current activation date
		func.setMaxValue(-1);
		functionalityBusinessService.update(domain.getUuid(), func);
		Assertions.assertEquals(userActivationDate1, activationDate);
		// Case 4:
		// The activation date is after maximum delay before activation | expected throw
		// a business exception
		func.setMaxValue(1); // maximum delay before activation is 1 month
		functionalityBusinessService.update(domain.getUuid(), func);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.MONTH, 2);
		Date userActivationDate2 = c1.getTime();
		BusinessException exception2 = Assertions.assertThrows(BusinessException.class, () -> {
			functionalityService.getDateValue((TimeUnitValueFunctionality) func, userActivationDate2,
					BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception2.getErrorCode());
		// Case 5: Delegation is not activated | expected return the default date
		Policy policy = new Policy(Policies.ALLOWED);
		policy.setStatus(false);
		func.setDelegationPolicy(policy);
		functionalityBusinessService.update(domain.getUuid(), func);
		Calendar calendar = getCalendarWithoutTime(new Date());
		calendar.add(func.toCalendarValue(), func.getValue());
		Date defaultDate = calendar.getTime();
		Date activationDate1 = functionalityService.getDateValue(func, new Date(),
				BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		Assertions.assertEquals(defaultDate, activationDate1);
	}

	@Sql("/import-tests-fake-functionality.sql")
	@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
	@Test
	public void testUploadRequestExpiryTimeFunctionality_GetDateValue() {
		TimeUnitValueFunctionality funcExpiry = functionalityService
				.getUploadRequestExpiryTimeFunctionality(domain);
		// Case 1:
		// the user expiration date is out of range, we need to throws business exception
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, + 11); // Maximum value of expiration date is 10
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			Date userExpirationDate = c.getTime();
			functionalityService.getDateValue(funcExpiry, userExpirationDate, BusinessErrorCode.UPLOAD_REQUEST_EXPIRY_DATE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_EXPIRY_DATE_INVALID, exception.getErrorCode());

		// Case 2: 
		// the user expiration date is > 0 and < maximum value, so we need to keep the user value.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, +8);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date userExpirationDate = calendar.getTime();
		Date expirationDate = functionalityService.getDateValue(funcExpiry, userExpirationDate, BusinessErrorCode.UPLOAD_REQUEST_EXPIRY_DATE_INVALID);
		Assertions.assertEquals(userExpirationDate, expirationDate);
	}

	@Sql("/import-tests-fake-functionality.sql")
	@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
	@Test
	public void testUploadRequestNotificationTimeFunctionality_GetNotificationDate() {
		TimeUnitValueFunctionality funcNotify = functionalityService
				.getUploadRequestNotificationTimeFunctionality(domain);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.MONTH, + 3);
		Date userExpirationDate = c1.getTime();
		// Case 1:
		// the user notification date is out of range, we need to throws business exception
		c1.add(Calendar.DATE, - 10); // Maximum value allowed is 7 (delay before notification is 7 days )
		Date userNotificationDate = c1.getTime();
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			functionalityService.getNotificationDateValue(funcNotify, userNotificationDate, userExpirationDate,
					BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID, exception.getErrorCode());
		// Case 2: 
		// the user notification date is > 0 and < maximum value (7 days), so we need to keep the user value.
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userExpirationDate);
		calendar.add(Calendar.DATE, - 5);
		Date userNotificationDate1 = calendar.getTime();
		Date notificationDate = functionalityService.getNotificationDateValue(funcNotify, userNotificationDate1,
				userExpirationDate, BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID);
		Assertions.assertEquals(userNotificationDate1, notificationDate);
		// Case 3: Delegation is not activated | expected return the default date
		Policy policy = new Policy(Policies.ALLOWED);
		policy.setStatus(false);
		funcNotify.setDelegationPolicy(policy);
		functionalityBusinessService.update(domain.getUuid(), funcNotify);
		Calendar calendar1 = getCalendarWithoutTime(new Date());
		calendar1.add(funcNotify.toCalendarValue(), funcNotify.getValue());
		Date defaultDate = calendar1.getTime();
		Date activationDate1 = functionalityService.getDateValue(funcNotify, new Date(),
				BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID);
		Assertions.assertEquals(defaultDate, activationDate1);
	}

	private Calendar getCalendarWithoutTime(Date date) {
		Calendar calendar = new GregorianCalendar();
		if (date != null) {
			calendar.setTime(date);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
}
