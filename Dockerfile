FROM openjdk:latest
EXPOSE 8081
ADD /target/tweetapp-docker.jar tweetapp-docker.jar
ENTRYPOINT ["java","-jar","/tweetapp-docker.jar"]