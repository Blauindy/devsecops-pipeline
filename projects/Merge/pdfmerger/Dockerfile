FROM gradle:latest
WORKDIR /app
COPY . /app
#RUN chmod +x gradlew
#RUN ./gradlew build
RUN gradle build --no-daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/pdfmerger-1.0-SNAPSHOT.jar"]