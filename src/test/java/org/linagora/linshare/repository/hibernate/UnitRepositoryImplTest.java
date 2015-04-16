/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.repository.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.Unit;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class UnitRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	
	@Autowired
	private UnitRepository unitRepository;
	
	@Test
	public void testCreateFileSizeUnit() throws BusinessException{
		
		FileSizeUnitClass unit = new FileSizeUnitClass(FileSizeUnit.GIGA);

		unitRepository.create(unit);
		Assert.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assert.assertTrue(unitRepository.findAll() != null);
		Assert.assertTrue(unitRepository.findById(unit.getPersistenceId()) != null);
		
		@SuppressWarnings("rawtypes")
		Unit newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assert.assertTrue(newUnit instanceof FileSizeUnitClass);
		Assert.assertTrue(((FileSizeUnitClass)newUnit).getUnitValue().toInt() == FileSizeUnit.GIGA.toInt());

		unitRepository.delete(unit);
	}
	
	@Test
	public void testCreateTimeUnit() throws BusinessException{
		
		TimeUnitClass unit = new TimeUnitClass(TimeUnit.WEEK);
		
		unitRepository.create(unit);
		Assert.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assert.assertTrue(unitRepository.findAll() != null);
		Assert.assertTrue(unitRepository.findById(unit.getPersistenceId()) != null);
		
		@SuppressWarnings("rawtypes")
		Unit newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assert.assertTrue(newUnit instanceof TimeUnitClass);
		Assert.assertTrue(((TimeUnitClass)newUnit).getUnitValue().toInt() == TimeUnit.WEEK.toInt());
		
		unitRepository.delete(unit);
	}
	
	
	@Test
	public void testBothUnitEntry() throws BusinessException{
		
		@SuppressWarnings("rawtypes")
		Unit unit = new FileSizeUnitClass(FileSizeUnit.GIGA);

		unitRepository.create(unit);
		Assert.assertNotNull(unit.getPersistenceId());
		logger.debug("Current object: " + unit.toString());
		
		Assert.assertTrue(unitRepository.findAll() != null);
		
		@SuppressWarnings("rawtypes")
		Unit newUnit = unitRepository.findById(unit.getPersistenceId());
		
		Assert.assertTrue(newUnit != null);
		
		
		Assert.assertTrue(newUnit instanceof FileSizeUnitClass);
		Assert.assertTrue(((FileSizeUnitClass)newUnit).getUnitValue().toInt() == FileSizeUnit.GIGA.toInt());

		
		@SuppressWarnings("rawtypes")
		Unit unit2 = new TimeUnitClass(TimeUnit.MONTH);

		unitRepository.create(unit2);
		Assert.assertNotNull(unit2.getPersistenceId());
		logger.debug("Current object: " + unit2.toString());
		
		Assert.assertTrue(unitRepository.findAll() != null);
		
		@SuppressWarnings("rawtypes")
		Unit newUnit2 = unitRepository.findById(unit2.getPersistenceId());
		
		Assert.assertTrue(newUnit2 != null);
		
		Assert.assertTrue(newUnit2 instanceof TimeUnitClass);
		Assert.assertTrue(((TimeUnitClass)newUnit2).getUnitValue().toInt() == TimeUnit.MONTH.toInt());
		Assert.assertFalse(((TimeUnitClass)newUnit2).getUnitValue().toInt() == TimeUnit.WEEK.toInt());

		unitRepository.delete(unit);
		unitRepository.delete(unit2);
	}

}
