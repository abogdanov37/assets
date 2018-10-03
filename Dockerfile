FROM openjdk:10.0.2-jdk-oracle
MAINTAINER Andrew Bogdanov

ADD build/libs/Assets-1.0.jar /bin/Assets-1.0.jar
ADD test.mv.db /root/test.mv.db

EXPOSE 8085

CMD ["/usr/bin/java", "-jar",  "/bin/Assets-1.0.jar"]