FROM openjdk:17-oracle
WORKDIR /app
RUN mkdir -p /app/music
RUN mkdir -p /app/cover
COPY build/libs .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "Reactive player-0.0.1-SNAPSHOT.jar"]