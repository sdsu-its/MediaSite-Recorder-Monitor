FROM sdsuits/tomcat-jdk:latest
MAINTAINER Tom Paulus <tpaulus@sdsu.edu>

RUN mkdir build; cd build
COPY . .

RUN ./gradlew war
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war
COPY build/libs/ms-monitor.war /usr/local/tomcat/webapps/ROOT.war

RUN cd ..; rm -rf build
EXPOSE 8080

CMD ["catalina.sh", "run"]
