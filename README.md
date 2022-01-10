## Reactive player API
A simple music player api that allows you to created user accounts,
upload music files and create playlists around those music files.

### Project goals
I've decided to create this project around Reactive programming
with spring boot by using the functional endpoint approach given
by WebFlux, not only that, but also with Kotlin in mind due to its awesome
support for this approach with `corutines` along with coRouters

### Features
- Crud options user accounts
- Crud options for Songs
- Crud options for PlayList
- Reactive security
- Jwt based authentication
- Google Oauth2 login/registration

### Technologies
- Kotlin
- Spring boot (WebFlux)  
- MongoDb