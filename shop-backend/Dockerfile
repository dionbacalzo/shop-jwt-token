FROM ubuntu

# change or remove proxy to current work setup
ENV http_proxy http://10.12.251.2:8080
ENV https_proxy http://10.12.251.2:8080

RUN apt-get update && \
	apt-get install -y software-properties-common && \
	apt-get install -y vim && \
	apt-get install -y dos2unix && \
    add-apt-repository ppa:openjdk-r/ppa && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y wget && \
    apt-get clean
	
ADD . /app
WORKDIR /app

# RUN file="$(ls -1 /app)" && echo $file
# RUN echo $(ls -1 /app)

# in case user is running the workspace from windows
RUN dos2unix gradlew
# make sure you are using the correct proxy setting at gradle.properties for this to work
RUN ./gradlew build war -x test

# download and install tomcat
RUN mkdir /usr/local/tomcat
RUN wget http://www-us.apache.org/dist/tomcat/tomcat-9/v9.0.19/bin/apache-tomcat-9.0.19.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN cp -Rv /tmp/apache-tomcat-9.0.19/* /usr/local/tomcat/

# copy war file to tomcat dir
RUN cd /app
RUN cp /app/build/libs/shop.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD /usr/local/tomcat/bin/catalina.sh run

# POWERSHELL : stop all running containers docker ps -a -q | ForEach { docker stop $_ }
# docker build -t shop .
# docker run -d -p 8080:8080 shop
# docker exec -it <container id> bash //access container bash

# note to self: wget checks if a specific ip from a proxy is moved or is currently working
# use the DockerFiles at the docker folder instead if you want a cotinuous build/update process