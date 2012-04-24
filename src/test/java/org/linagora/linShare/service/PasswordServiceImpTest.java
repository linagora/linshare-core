package org.linagora.linShare.service;

import org.junit.Assert;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class PasswordServiceImpTest extends AbstractTransactionalJUnit4SpringContextTests {

private static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);
	
	@Autowired
	private PasswordService passwordService;
	
	@Test
	public void testGeneratePassword(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assert.assertNotNull(passwordService.generatePassword());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
