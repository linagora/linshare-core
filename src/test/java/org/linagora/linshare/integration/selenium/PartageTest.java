/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.integration.selenium;

import java.io.File;

import org.junit.Ignore;


@Ignore
public class PartageTest extends AbstractLinshareSeleneseTestCase {
	
	@Ignore
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
