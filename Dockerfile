FROM openjdk:17
ADD target/Infosys.jar Infosys.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Infosys.jar"]