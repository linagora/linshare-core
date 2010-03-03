package org.linagora.linShare.integration.selenium;

import java.io.File;



public class PartageTest extends AbstractLinshareSeleneseTestCase {
	
	
	public void testPartage() throws Exception {
		open();
		
		selenium.type("login", "user1@linpki.org");
		selenium.type("password", "password1");
		selenium.click("//input[@value='Connexion']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		selenium.click("//div[@id='actionBar']/ul/li/a/span");

		File sample1 = File.createTempFile("small", "txt");
		File sample2 = File.createTempFile("test", "");
		
		selenium.type("file", sample1.getAbsolutePath());
		Thread.sleep(1000);
		selenium.click("link=+");
		selenium.type("file_0", sample2.getAbsolutePath());
		Thread.sleep(1000);
		selenium.type("recipientsPatternQuickSharePopup", "mlaborie@linagora.com,");
		selenium.click("secureSharing");
		Thread.sleep(1000);
		selenium.click("//form[@id='quickShareForm']/div[11]/a[1]/span");
		selenium.click("submitQuickShare");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Partage rapide réussi.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("name", sample2.getName());
		selenium.click("//input[@name='submit' and @value='Rechercher']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Aucun fichier partagé n’a été trouvé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[2]/a/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Fichiers")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("//form[@id='search']/div[2]/table/tbody/tr[1]/td[1]/div")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//img[@title='Fichier partagé.  Action : Partager le fichier à nouveau']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Voulez-vous partager ces fichiers ?")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("recipientsPatternSharePopup", "user2@linpki.org");
		selenium.click("secureSharing");
		selenium.click("//a[@id='linksubmit_0']/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le partage de fichiers s’est bien déroulé")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
		selenium.waitForPageToLoad("300000");
		selenium.type("login", "user2@linpki.org");
		selenium.type("password", "password2");
		selenium.click("//input[@value='Connection']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent(sample2.getName())) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//form[@id='search']/div[2]/table/tbody/tr/td[3]/a[2]/img");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le fichier a été copié dans votre espace.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[2]/a/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("//form[@id='search']/div[2]/table/tbody/tr/td[1]/div")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
		selenium.waitForPageToLoad("300000");
	}
}
