## Reactive player API
A simple music player api that allows you to created user accounts,
upload music files and create playlists around those music files.

### Project goals
I've decided to create this project around Reactive programming
with spring boot by using the functional endpoint approach given
by WebFlux, not only that, but with Kotlin in mind, thanks to coroutine support

Reactive programming may be an overkill for a project like this, but it was
something I wanted to try for a long time just to see how different it could
be from a MVC Spring boot application

### Features
- Crud options user accounts
- Crud options for Songs
- Crud options for PlayList
- Jwt based authentication (Asymmetric key signed)
- Google Oauth2 login / registration

### Technologies
- Kotlin
- Spring boot (WebFlux)
- MongoDb

### How to run
Requirements:
 - Java 17
 - Docker
 - A Google Cloud Oauth2 client (add credentials on application-local.yml and application-docker.yml)
 - two folders where to store audio files and images (add their names on application-local.yml and application-docker.yml)

With that done, run:
```
./gradlew build
```
Once the project has been compiled successfully, run:
```
docker-compose up
```
Well you're done, happy hacking <3!
