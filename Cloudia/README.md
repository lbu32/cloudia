# Cloudia - Cloud interface application

##Projektbeschreibung
Cloudia bietet dem Nutzer die Möglichkeit Tickets in einer Webanwendung verwalten und gleichzeitig von einer beliebig
ausgewählten Cloudspeicherlösung Dateien, die mit den jeweiligen Tickets in Kontext stehen, einzusehen und zu ändern.
Egal, wo eine Änderung stattfindet: Die Änderung ist sofort in allen Clients sichtbar.

Features
- Ticketübersicht 
- Ticketbearbeitung (Dateien hinzufügen und löschen)
- Zuordnung an Organisationen
- Vergabe von USER und ADMIN Rolle

##Benötigte Software:
1. Die Software läuft mit Unterstützung vom Spring Framework Version 5.1.2.
2. Installiere Java 8 und setze eine Java-Systemvariable (Anleitung: https://www.java.com/de/download/manual.jsp).
3. Installiere Maven Version 4.6.(Anleitung: http://maven.apache.org/download.cgi)
4. Erstelle eine relationale (MySQL-)Datenbank ohne Tabellen. Diese werden später per ORM erstellt.
5. Passe in der  Datei "application.properties" folgende Eigenschaften an:
    - spring.datasource.url 
      Beispiel: jdbc:mysql://localhost:3360/datenbank
    - spring.datasource.username=user
    - spring.datasource.password=passwort
    - spring.jpa.hibernate.ddl-auto = create (Info: create wird zur Tabellenerstellung verwendet)
    
6. Programmstart auf der Konsole im Verzeichnis in dem die pom.xml liegt mit: "mvn spring-boot:run"
7. Ändere von 'spring.jpa.hibernate.ddl-auto = create' zu 'spring.jpa.hibernate.ddl-auto = update',
   damit Änderungen in den Tabellen gespeichert werden, anstatt das Tabellen neu zu erstellt werden.

##Benötigte Daten in der Datenbank
1. Füge in der Datenbank in die Tabelle "role" mit den Spalten "role_id" und "role" ( 1 , ADMIN ; 2 , USER ) ein.
2. Füge in der Datenbank in die Tabelle "cloud" mit den Spalten "cloud" und "cloud_id" (1 , GoogleDrive ; 2 , Dropbox)
    ein.
3. Registriere einen ADMIN in der Webanwendung. Füge dazu in die Tabelle "user" mit den Spalten "user_id",
    "email", "first_name","last_name", "password", "organization_organization_id" und "role_role_id" folgendes ein:
    update role_role_id in 1 , denn der Wert für 1 ist der Verweis auf die Rolle ADMIN.

##Rollen
   Zu beachten beim Login: Es gibt ADMIN und USER Rollen in der Datenbank.

   ###Admin
 - Ordnet User ihren Organisationen zu
 - Erstellt neue Organisationen
 - Ordnet User einer Rolle zu
 - Zugriff zum Admin-Dashboard

   ###User
 - Erst nach Zuordnung zu einer Organisation durch den ADMIN erhält der USER eine Ansicht mit Tickets

## Anbindung der Webanwendung an die Cloudspeicher
Cloudia ermöglicht die Verwaltung von Dateien über zwei Clouds. Zur Bearbeitung von Dateien [in der WebApp],
verbinde dich mit der entsprechenden Cloud. Entferne dafür im Ticketcontontroller in Funktion getFileService "//" vor
return new GoogleService() oder return new DropboxService().

###Zugang über Dropbox:
1. Registriere dich auf Dropbox.
2. Gehe auf folgenden Link: https://www.dropbox.com/developers/apps/create
3. Bearbeite die Schritte zur Apperstellung auf der Dropbox Plattform.
4. Generiere einen Acces Token.
5. Melde dich mit mit dem Adminuser in der Webanwendung an.
6. Gehe aufs Admindashboard und erstelle eine neue Organisation.
   Füge hier den token von eben ein und trage bei Cloud die 2 ein.
7. Ordne einem User die neue Organisation zu.

###Zugang über Google:
1. Registriere dich auf Google.
2. Melde dich mit dem Adminuser in der Webanwendung an.
3. Gehe aufs Admindashboard und erstelle eine neue Organisation.
   trage bei Cloud die 1 ein. Der Token kann leer bleiben.
4. Ordne diese Organisation einem User zu
5. Melde dich als Admin ab und mit dem User aus 4. wieder an.
6. Erstelle ein neues Ticket.
7. Gehe in den Terminal, wo das Mavenprojekt läuft.
   Hier wirst du aufgefordert einen Link aufzurufen. Tue dies.
   Hier musst du dich mit dem eben erstellten Google Account anmelden
   und dem Projekt die gewünschten Berechtigungen erteilen.
8. Nachdem auf den Link geklickt wurde, werden automatisch in dem Ordner tokens/ die Unterordner benannt nach der jeweiligen Organization_ID, mit der der User angemeldet ist, angelegt und der Token der jeweiligen Organisation darin gespeichert. Für weitere Anmeldungen in Webanwendungen wird der Token aus dem Ordner zur Anmeldung bei Google verwendet und der Link muss somit nicht jedesmal geklickt werden.

   Hinweise:
   wiederhole diese Schritte für jede neue Organisation







