FROM gradle:jdk13 AS BUILD
WORKDIR /compile
COPY /zulassung2 .
RUN gradle bootJar

FROM openjdk:13
WORKDIR /code
COPY --from=BUILD /compile/build/libs/*.jar app.jar
CMD ["java","-jar","app.jar"]
