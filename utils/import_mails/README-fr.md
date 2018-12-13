
# Module import_mails

Ce module est un script python visant à améliorer le processus d'import et d'export des mails sur LinShare.

### Sommaire
  *   [Prérequis](#prerequis)
  *   [Configuration du module](#config_module)
  *   [Fichiers d’exports générés](#generated_files)

## <a name="prerequis"></a> Prérequis

Afin d'exécuter ce script, vous aurez besoin de :
*  Un accès à la base de données PostgreSQL de LinShare
*  Python 3
*  pip3

	```
      sudo apt-get install python3-pip
	```
  *  la libraire psycopg2-binary afin de se connecter à la base PostgreSQL
  *  la librairie pypika pour la construction de requêtes SQL.

-Pour installer ces deux librairies il suffit de les ajouter dans le fichier requirements.txt avec la version adéquate.
  * pour les installer

	```
	pip3 install -r requirements.txt
	```
##   <a name="config_module"></a> Configuration du module

Maintenant que notre environnement Python est configuré, on va pouvoir configurer le module.
Se rendre dans le dossier LINSHARE_WORKSPACE/utils/import_mails.
Lancer la commande suivante :


	$ python3 app.py

Si vous obtenez au moins une erreur sur l'écran qui suit, vous devez suivre le reste de ce guide, et notamment l'édition de votre fichier de configuration :

	        Name             State     
	------------------------------
	Config Fields            OK
	Get Mail Content Types   ERROR
	Linshare Database        ERROR
	------------------------------


### Fichier de configuration
Il s'agit maintenant de configurer le module avec les paramètres de votre environnement LinShare:
*  Créer et éditer le fichier config.json en surchargeant les paramètres nécessaires afin de convenir à votre configuration LinShare :

	```json
	{
		"dbname": "linshare",
		"user": "DATABASE_USER",
		"host": "LINSHARE_HOST",
		"port": "DATABASE_PORT",
		"password": "DATABASE_PASSWORD",
		"path_mail_content_types": "../../src/main/java/org/linagora/linshare/core/domain/constants/MailContentType.java"
	}
	```

En exécutant cette commande,

	 $ python3 app.py

vous devrez obtenir le résultat suivant :

	-----Check configuration------
	        Name             State     
	------------------------------
	Config Fields            OK
	Get Mail Content Types   OK
	Linshare Database        OK
	------------------------------

##  <a name="generated_files"></a> Fichiers d'exports générés

Maintenant que le script s'est exécuté avec succès, vous devriez avoir généré dans le dossier `src/main/resources/sql/common` :
*  `import_mail_structure.sql` : fichier insérant la structure des différents emails dans la base de données.
*  le dossier `mail_updates` comprenant les fichiers d'UPDATE du contenu des mails
*  `import_mail_update.sql` : fichier contenant l'agrégat de tous les fichiers d'UPDATE du dossier précédent
*  `import-mail.sql` : fichier résultant de l’agrégat des deux fichiers précédents  

L'arborescence des fichiers créés est la suivante :

	...
	|-- import_mail_structure.sql
	|-- import-mail.sql
	|-- mail_updates
		|-- mail_content
			|-- ...
		|-- mail_footer
			|-- ...
		|-- mail_layout
			|-- ...
		|-- import_mail_update.sql
