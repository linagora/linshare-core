package org.linagora.linshare.integration.selenium;

import org.junit.Ignore;

@Ignore
public class LanguageTest extends AbstractLinshareSeleneseTestCase {
	
	
	public void testLanguage() throws Exception {
		open();
		

		selenium.click("link=anglais");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Please log in.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=French");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Veuillez vous identifier.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("login", "user1@linpki.org");
		selenium.type("password", "password1");
		selenium.click("//input[@value='Connexion']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=My configuration");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Mes paramètres")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("AnglaisFrançais")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.select("currentLocale", "label=Français");
		selenium.select("currentLocale", "label=Anglais");
		selenium.click("//form[@id='configUserform']/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("My configuration")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Logout");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Please log in.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("login", "user1@linpki.org");
		selenium.type("password", "password1");
		selenium.click("//input[@value='Connection']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Welcome to LinShare, the Open Source secure files sharing system.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=My configuration");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("My configuration")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		Thread.sleep(1000);
		selenium.select("currentLocale", "label=French");
		selenium.click("//form[@id='configUserform']/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Mes paramètres")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Please log in.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

	}
}
