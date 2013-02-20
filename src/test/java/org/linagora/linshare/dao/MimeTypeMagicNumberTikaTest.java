package org.linagora.linshare.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-dao.xml",
		"classpath:springContext-test.xml"
})
public class MimeTypeMagicNumberTikaTest extends AbstractJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaTest.class);
	
	@Qualifier("mimeTypeMagicNumberDao2")
	@Autowired
	private MimeTypeMagicNumberDao mimeTypeService;
	
	@Before
	public void setUp() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateAllowedMimeType() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AllowedMimeType> allSupportedMimeType = mimeTypeService.getAllSupportedMimeType();
		logger.debug("allSupportedMimeType size : " + allSupportedMimeType.size());
		// old library : 161, new one : 1385
		Assert.assertEquals(1385, allSupportedMimeType.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
