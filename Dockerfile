FROM  adoptopenjdk:11-jre-hotspot
USER root
RUN apt-get update && apt-get install -y git
ADD sonar-scanner-4.6.2.2472-linux sonar-scanner-4.6.2.2472-linux
RUN chmod a+x /sonar-scanner-4.6.2.2472-linux/bin/sonar-scanner
RUN chmod 755 /sonar-scanner-4.6.2.2472-linux/jre/bin/java
RUN chmod -R 777 /sonar-scanner-4.6.2.2472-linux
RUN chmod 777 /sonar-scanner-4.6.2.2472-linux/conf/sonar-scanner.properties
ADD target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--gr.nikos.smartclide.sonarqube.url=${GR_NIKOS_SMARTCLIDE_SONARQUBE_URL}"]