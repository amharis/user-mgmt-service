FROM openjdk:17-jdk-slim


WORKDIR /user-mgmt-service
COPY . /user-mgmt-service


RUN ./mvnw install

EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "song-service-1.0.0.jar"]
CMD ["./mvnw", "spring-boot:run"]