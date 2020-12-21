FROM openjdk:15
ADD ./target/survival-pool-1.0-SNAPSHOT.jar /usr/src/survival-pool-1.0-SNAPSHOT.jar
WORKDIR usr/src
ENTRYPOINT ["java", "-jar", "survival-pool-1.0-SNAPSHOT.jar"]
