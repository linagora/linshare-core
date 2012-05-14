package org.linagora.linShare.integration.selenium;

import org.junit.Ignore;

@Ignore
public class LogTest extends AbstractLinshareSeleneseTestCase {

	public void testLog() throws Exception {
		open();

		selenium.type("login", "user1@linpki.org");
		selenium.type("password", "password1");
		selenium.click("//input[@value='Connexion']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium
						.isTextPresent("Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé."))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[4]/a/span");
		selenium.waitForPageToLoad("300000");
		selenium.addSelection("statusPalette-avail", "label=Ajout de fichier");
		selenium.click("statusPalette-select");
		selenium.click("//form[@id='formReport']/a[1]/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium.isTextPresent("Ajout de fichier"))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}


		selenium.click("//form[@id='formReport']/a[2]/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium
						.isTextPresent("Aucune réponse pour cette recherche"))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		selenium.type("fileName", "test");
		selenium.click("//form[@id='formReport']/a[1]/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium.isTextPresent("Ajout de fichier"))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
		selenium.waitForPageToLoad("300000");
	}
}
