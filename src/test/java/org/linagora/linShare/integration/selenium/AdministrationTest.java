package org.linagora.linShare.integration.selenium;

import java.io.File;


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
