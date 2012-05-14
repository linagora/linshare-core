package org.linagora.linShare.integration.selenium;

import org.junit.Ignore;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;

@Ignore
public abstract class AbstractLinshareSeleneseTestCase  extends SeleneseTestCase {
	
	public void setUp() throws Exception {
	     selenium = new DefaultSelenium("localhost", 4445,  "*firefox", "http://localhost:8080/");
	     selenium.start();

	}
	


	
	
	protected void open() throws InterruptedException {
		selenium.open("/linshare");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Please log in.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=français");
		
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Veuillez vous identifier.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
	}
}
