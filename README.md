# Les Amis de l'escalade : déployer l'application

Après avoir installé Java, vous pouvez déployer l'applicaiton à l'aide d'un serveur. Nous prendrons ici celui qu'intègre Spring Boot.

Téléchargez tout d'abord le fichier .zip contenant l'application disponible ici : 
    
    https://github.com/RemiMoustey/Library. 
    
Puis, décompressez-le où vous le souhaitez sur votre disque local.

Ensuite, importez chaque dossier présent à la racine en tant que module à l'aide de votre environnement de développement, dans un projet vierge.

**1. Connexion à la base de données.**

Créez un fichier nommé _application.properties_ dans les dossiers _microservice-books/src/main/resources_, _microservice-loans/src/main/resources_ et _microservice-users/src/main/resources_.

Créez trois bases de données appelées book-service, loan-service et user-service.

Ouvrez ces trois fichiers à l'aide d'un éditeur de texte et remplissez-les comme suit :

    spring.application.name=microservice-[books ou loans ou users]
    
    #MySQL configuration
    spring.jpa.hibernate.ddl-auto=update
    spring.datasource.url=[url de connexion vers la base de données]
    spring.datasource.username=[username de connexion vers la base de données]
    spring.datasource.passoword=[mot de passe de connexion vers la base de données]
    
    spring.datasource.sql-script-encoding=UTF-8
    
    #Eureka
    eureka.client.serviceUrl.defaultZone: http://localhost:9102/eureka/
    
    management.endpoints.web.exposure.include=health,info,metrics,beans,env
    
Remplacez les données entre crochets par les informations de connexion à votre système de gestion de base de données.

Après avoir créé une base de données, entrez toutes les requêtes présentes dans les fichiers _create_tables.sql_ et _data.sql_ **dans cet ordre**. Ces requêtes rempliront votre base de données avec des tables et un jeu de données de démonstration.

**2. Lancement du site web en local.**

**Pour déployer l'application, réalisez les étapes suivantes dans l'ordre à l'aide de votre environnement de développement :
    
1. Lancer les fonctions main des classes EurekaServerApplication et ZuulServerApplication respectivement dans les packages eureka-server et zuul-server.

2. Lancer les fonctions main des classes MicroserviceBooksApplication, MicroserviceLoansApplication et MicroserviceUsersApplication respectivement dans les packages microservice-books, microservice-loans et microservice-users.

3. Lancer la fonction main de la classe ClientUiApplication dans le package clientui.

Rendez-vous ensuite sur votre navigateur, puis tapez dans la barre d'adresse : _localhost:8080/livres_.

Vous devriez arriver sur la page d'accueil qui liste les livres de la bibliothèque !

Il vous est également possible d'exécuter le batch, en lançant la fonction main de la classe BatchApplication.