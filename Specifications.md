#les speces de la future WebApp permettant l'extraction de sujets sur les forums Moodle

# Specs de la web application #

Outils de développement :

-Moodle

-Eclipse / NetBeans

-Firefox + FireBug


Langages utilisés :

-partie graphique : JSP +(DOJO**)**

-partie métier : servlet (J2EE)

Fonctionnalités de la servlet :

L'extraction des différents sujets d'un forum se fera à partir d'une URL donnée en paramètre. A partir de cette URL, la servlet ira récupérer en parcourant si nécessaire, les sous catégories du forum si l'URL donnée pointe sur l'index ou une liste de rubriques.

Les pages extraites seront stockées en locale au format XML. Si une nouvelle extraction est demandée par l'utilisateur, et qu'elle porte sur une partie du forum précédemment extraite. Le XML déjà enregistré sera récupéré et comparé et si nécessaire mis à jour (si le topic ou la rubrique a été modifiée).

Pour extraire les informations des pages nous utiliserons le parser HTMLParser.

Afin de minimiser le coût en espace disque des fichiers XML, les référence ou "quote" sur un message précédent se fera par référence afin d'éviter les doublons et pour faciliter la mise à jour des pages (dans le cas d'une édition).

Afin de s'identifier lors de la connexion aux forums de Moodle les identifiants et mots de passe de session seront conserver dans un fichier de properties d'autres properties viendront s'y ajouter plus tard si nécessaire. Les cookies se session renvoyés par Moodle lors d'une connexion seront conservés afin de pouvoir les réutiliser par la suite et renouvelés si expirés

Le résultat retourné à l'utilisateur sera de la même forme que celui des forum moddle. Par contre de nouvelles fonctionnalités viendront s'ajouter à celles d'origine telles qu'un bouton permettant l'export en RDF à partir d'un certain point dans un sujet de discussion.


# Questions : #

Doit-il y avoir un seul XML modélisant l'ensemble des topics extraits ou plusieurs et comment les découper (un par topic, un par rubrique) ?

La servlet doit-elle se loguer lors de la connexion sur le forum ?
