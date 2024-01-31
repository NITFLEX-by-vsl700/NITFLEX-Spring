FROM maven AS build-snapshot

EXPOSE 8080

COPY . /usr/src/nitflex-spring
WORKDIR /usr/src/nitflex-spring
RUN mvn clean verify
ENTRYPOINT ["java", "-jar", "target/nitflex-0.0.1-SNAPSHOT.jar"]