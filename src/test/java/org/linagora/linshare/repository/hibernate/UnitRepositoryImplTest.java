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
