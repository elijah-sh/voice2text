FROM hub.c.163.com/library/java:openjdk-8-jdk-alpine
MAINTAINER shenshuaihu shen_shuaihu@163.com
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]