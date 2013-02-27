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
public class AdministrationTest extends AbstractLinshareSeleneseTestCase {
	
	
	public void testAdministrationTest() throws Exception {
		open();

		selenium.type("login", "root@localhost.localdomain");
		selenium.type("password", "adminlinshare");
		selenium.click("//input[@value='Connexion']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[4]/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Activation de la fonction de signature")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("activeSignature");
		selenium.click("activeEncipherment");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Activation de la fonction de signature")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//a[@id='linksubmit_0']/span");
		selenium.waitForPageToLoad("300000");
		selenium.click("activeSignature");
		selenium.click("activeSignature");
		selenium.click("activeEncipherment");
		selenium.click("activeEncipherment");
		selenium.click("//a[@id='linksubmit_0']/span");
		selenium.waitForPageToLoad("300000");
		selenium.click("link=Mes paramètres");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Mes paramètres")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Paramétrage de sécurité")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("password", "12345678");
		selenium.type("confirmPassword", "12345678");
		selenium.click("submit");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Votre clé de chiffrement est prête à être utilisée.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//div[@id='menuBar']/ul/li[2]/a/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Aucun fichier n’a été trouvé.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		

		File sample1 = File.createTempFile("small", "txt");
		

		selenium.click("//div[@id='actionBar']/ul/li/a/span");
		selenium.type("file_9", sample1.getAbsolutePath());
		selenium.click("//form[@id='formUpload_0']/div[5]/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le fichier "+sample1.getName()+" a bien été ajouté.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("filesSelected");
		selenium.click("link=Signer");
		selenium.click("filesSelected");
		selenium.click("encyphermentSubmit");
		selenium.click("//img[@title='Fichier non partagé.  Action : Partager le fichier']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Voulez-vous partager ces fichiers ?")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}


		selenium.type("recipientsPatternSharePopup", "mlaborie@linagora.com");
		selenium.click("secureSharing");
		selenium.click("//a[@id='linksubmit_0']/span");
	}
}
