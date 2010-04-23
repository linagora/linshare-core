package org.linagora.linShare.core.domain.constants;

import junit.framework.TestCase;


public class FileSizeUnitTest extends TestCase{

	public void testGetPlainSize(){

		
     assertEquals(FileSizeUnit.KILO.getPlainSize(1), 1024L);
     assertEquals(FileSizeUnit.MEGA.getPlainSize(1), 1048576L);
     assertEquals(FileSizeUnit.GIGA.getPlainSize(1), 1073741824L);
		
	}
	
}
