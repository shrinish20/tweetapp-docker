FROM openjdk:latest
EXPOSE 5000
ADD /target/tweetapp-docker.jar tweetapp-docker.jar
ENTRYPOINT ["java","-jar","/tweetapp-docker.jar"]