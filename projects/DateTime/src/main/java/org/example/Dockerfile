# Basis-Image mit OpenJDK
FROM openjdk:17-jdk-alpine

# Arbeitsverzeichnis erstellen
WORKDIR /app

# Java-Datei in das Arbeitsverzeichnis kopieren
COPY DateTimeExample.java .

# Java-Datei kompilieren
RUN javac DateTimeExample.java

# Kommando zum Ausführen der Java-Anwendung
CMD ["java", "DateTimeExample"]