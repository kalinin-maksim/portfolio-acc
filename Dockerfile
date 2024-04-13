FROM adoptopenjdk/openjdk11
VOLUME /tmp
COPY application/target/edu-kalinin-acc-application-0.0.1-SNAPSHOT-full.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]