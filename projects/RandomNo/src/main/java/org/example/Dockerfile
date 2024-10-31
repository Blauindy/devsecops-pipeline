# Basis-Image mit OpenJDK
FROM openjdk:17-jdk-alpine

# Arbeitsverzeichnis erstellen
WORKDIR /app

# Java-Datei in den Container kopieren
COPY RandomNumberExample.java /app

# Java-Datei kompilieren
RUN javac RandomNumberExample.java

# Kommando zum Ausführen der Java-Anwendung
CMD ["java", "RandomNumberExample"]