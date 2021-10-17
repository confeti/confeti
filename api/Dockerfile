FROM gradle:jdk16 AS build
WORKDIR /app
COPY . /app
RUN gradle bootJar --no-daemon

FROM openjdk:16-alpine
COPY --from=build /app/api/build/libs/*.jar confeti.jar
COPY --from=build /app/setup.sh /app/api/entrypoint.sh ./
RUN chmod +x setup.sh
RUN chmod +x entrypoint.sh
RUN apk --no-cache add curl jq
ENTRYPOINT ["/entrypoint.sh"]
