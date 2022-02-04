/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.repository.hibernate;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.Unit;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class UnitRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private UnitRepository unitRepository;
	
	@Test
	public void testCreateFileSizeUnit() throws BusinessException{
		
		FileSizeUnitClass unit = new FileSizeUnitClass(FileSizeUnit.GIGA);

		unitRepository.create(unit);
		Assertions.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assertions.assertTrue(unitRepository.findAll() != null);
		Assertions.assertTrue(unitRepository.findById(unit.getPersistenceId()) != null);
		
		Unit<?> newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assertions.assertTrue(newUnit instanceof FileSizeUnitClass);
		Assertions.assertTrue(((FileSizeUnitClass)newUnit).getUnitValue().toInt() == FileSizeUnit.GIGA.toInt());

		unitRepository.delete(unit);
	}
	
	@Test
	public void testCreateTimeUnit() throws BusinessException{
		
		TimeUnitClass unit = new TimeUnitClass(TimeUnit.WEEK);
		
		unitRepository.create(unit);
		Assertions.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assertions.assertTrue(unitRepository.findAll() != null);
		Assertions.assertTrue(unitRepository.findById(unit.getPersistenceId()) != null);
		
		Unit<?> newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assertions.assertTrue(newUnit instanceof TimeUnitClass);
		Assertions.assertTrue(((TimeUnitClass)newUnit).getUnitValue().toInt() == TimeUnit.WEEK.toInt());
		
		unitRepository.delete(unit);
	}
	
	
	@Test
	public void testBothUnitEntry() throws BusinessException{
		
		Unit<?> unit = new FileSizeUnitClass(FileSizeUnit.GIGA);

		unitRepository.create(unit);
		Assertions.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assertions.assertTrue(unitRepository.findAll() != null);
		
		Unit<?> newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assertions.assertTrue(newUnit != null);
		
		
		Assertions.assertTrue(newUnit instanceof FileSizeUnitClass);
		Assertions.assertTrue(((FileSizeUnitClass)newUnit).getUnitValue().toInt() == FileSizeUnit.GIGA.toInt());

		
		Unit<?> unit2 = new TimeUnitClass(TimeUnit.MONTH);

		unitRepository.create(unit2);
		Assertions.assertNotNull(unit2.getPersistenceId());
		logger.debug("Current object: " + unit2.toString());
		
		Assertions.assertTrue(unitRepository.findAll() != null);
		
		Unit<?> newUnit2 = unitRepository.findById(unit2.getPersistenceId());
		
		Assertions.assertTrue(newUnit2 != null);
		
		Assertions.assertTrue(newUnit2 instanceof TimeUnitClass);
		Assertions.assertTrue(((TimeUnitClass)newUnit2).getUnitValue().toInt() == TimeUnit.MONTH.toInt());
		Assertions.assertFalse(((TimeUnitClass)newUnit2).getUnitValue().toInt() == TimeUnit.WEEK.toInt());

		unitRepository.delete(unit);
		unitRepository.delete(unit2);
	}

}
