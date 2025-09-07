
FROM eclipse-temurin:21-jdk-alpine AS build


RUN apk add --no-cache bash curl unzip


WORKDIR /workspace


COPY settings.gradle .
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
RUN chmod +x gradlew


COPY domain/*/build.gradle domain/*/
COPY infrastructure/*/build.gradle infrastructure/*/
COPY applications/*/build.gradle applications/*/


RUN ./gradlew --no-daemon build -x test || true


COPY . .
RUN dos2unix gradlew && chmod +x gradlew
RUN ./gradlew :app-service:bootJar --no-daemon -x test




FROM eclipse-temurin:21-jre-alpine


RUN adduser -D -h /app appuser \
 && mkdir -p /app/cache /tmp \
 && chown -R appuser:appuser /app /tmp


WORKDIR /app
VOLUME ["/tmp"]


COPY --from=build /workspace/applications/app-service/build/libs/*.jar app.jar
RUN chown appuser:appuser /app/app.jar


ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"


EXPOSE 8081


USER appuser


ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
