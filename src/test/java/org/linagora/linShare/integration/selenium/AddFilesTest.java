package org.linagora.linShare.integration.selenium;

import java.io.File;


public class AddFilesTest extends AbstractLinshareSeleneseTestCase {
	
	public void testAddFiles() throws Exception {
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

		selenium.click("//div[@id='menuBar']/ul/li[2]/a/span");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Fichiers")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		File sample1 = File.createTempFile("small", "txt");
		File sample2 = File.createTempFile("test", "");
		
		
		selenium.click("//div[@id='actionBar']/ul/li/a/span");
		selenium.type("file_9", sample1.getAbsolutePath());
		selenium.click("link=+");
		selenium.type("file_10", sample2.getAbsolutePath());
		selenium.click("//form[@id='formUpload_0']/div[5]/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le fichier "+sample1.getName()+" a bien été ajouté.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le fichier "+sample2.getName()+" a bien été ajouté.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.select("selectSorter", "label=Date de dépôt");
		selenium.waitForPageToLoad("300000");
		selenium.select("selectSorter", "label=Nom");
		selenium.select("selectSorter", "label=Taille");
		selenium.click("filesSelected");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Partager")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		

		selenium.type("name", sample1.getName());
		selenium.click("//input[@name='submit' and @value='Rechercher']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("//form[@id='search']/div[2]/table/tbody/tr/td[1]/div/a/span")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("filesSelected");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Supprimer")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		selenium.click("link=Supprimer");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Voulez-vous vraiment supprimer ce(s) fichier(s) ?")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//a[@id='confirmsubmitPopupYes']/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Suppression de 1 fichier(s) réalisée avec succès.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("filesSelected");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Partager")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//a[contains(text(),'Partager')]");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Liste des fichiers à partager")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("share");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Voulez-vous partager ces fichiers ?")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("recipientsPatternSharePopup", "mlaborie@linagora.com");
		selenium.click("secureSharing");
		selenium.click("datePicked");
		selenium.click("link=1");
		selenium.click("panelSubject_toggler");
		selenium.type("textarea", "Test d'objet avec dés accents é\"_çè&éè_\"çà_&é'çà&é' \"'-_\"_\"ç_'");
		selenium.click("panelMessage_subject");
		selenium.type("textarea_0", "é\"è(&éè\"çà_ '\"à)&çé'ç é&')çé&' '=ç&é')ç");
		selenium.click("//a[@id='linksubmit_0']/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le partage de fichiers s’est bien déroulé")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//img[@title='Mettre à jour le fichier']");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Dépôt de fichiers")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("file", sample2.getAbsolutePath());
		selenium.click("//form[@id='formUpload']/div[5]/a/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Le fichier "+sample2.getName()+" a bien été mis à jour")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.type("name", sample2.getName());
		selenium.click("//input[@name='submit' and @value='Rechercher']");
		selenium.waitForPageToLoad("300000");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("//form[@id='search']/div[2]/table/tbody/tr/td[1]/div/a/span")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("selectAll");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Supprimer")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Supprimer");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Voulez-vous vraiment supprimer ce(s) fichier(s) ?")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("//a[@id='confirmsubmitPopupYes']/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Suppression de 1 fichier(s) réalisée avec succès.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.click("link=Déconnexion");
	}

	

}
