FROM gradle:jdk13 AS BUILD
WORKDIR .
COPY /zulassung2 .
RUN gradle bootJar

FROM openjdk:13
WORKDIR /code
COPY zulassung2/build/libs/*.jar app.jar
CMD ["java","-jar","app.jar"]
