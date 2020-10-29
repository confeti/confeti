FROM gradle:jdk15 AS build
WORKDIR /app
COPY . /app
RUN gradle bootJar --no-daemon

FROM openjdk:15-alpine
COPY --from=build /app/api/build/libs/*.jar confeti.jar
ENTRYPOINT ["java","--enable-preview","-jar","/confeti.jar"]
