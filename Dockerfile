FROM openjdk:8-jdk-alpine
MAINTAINER shenshuaihu <shuaihu.shen@hand-china.com>
ADD /usr/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]