package org.linagora.linShare.integration.selenium;

public class AddUsersTest extends AbstractLinshareSeleneseTestCase {
	
	
	public void testAddUser() throws Exception {
		open();
		

		selenium.type("login", "user1@linpki.org");
		selenium.type("password", "password1");
		selenium.click("//input[@value='Connexion']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[3]/a/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Utilisateurs")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='actionBar']/ul/li/a/span");
		selenium.type("mail_0", "mlaborie@linagora.com");
		selenium.type("firstName_0", "Matthieu");
		selenium.type("lastName_0", "Laborie");
		selenium.type("commentArea", "Pas de commentaires? Héhé");
		selenium.click("customMessage");
		selenium.type("customMessageArea", "Salut a toi!!");
		selenium.click("//a[@id='linksubmit_1']/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("L’invité a bien été ajouté")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//img[@alt='Éditer']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Édition d’un compte invité")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[2]/a[1]/span");
		selenium.waitForPageToLoad("300000");
		selenium.click("//img[@alt='Partager']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Partage")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
		selenium.waitForPageToLoad("300000");
	}
}
