FROM ubuntu:14.04
MAINTAINER Ikuo Matsumura <makiczar@gmail.com>

RUN apt-get install -y software-properties-common
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer

RUN wget http://central.maven.org/maven2/org/aspectj/aspectjweaver/1.8.6/aspectjweaver-1.8.6.jar

ADD counter-cluster-assembly-0.1-SNAPSHOT.jar .
RUN mkdir target/

CMD java \
  -Dconfig.resource=$CONFIG \
  -javaagent:aspectjweaver-1.8.6.jar \
  -jar counter-cluster-assembly-0.1-SNAPSHOT.jar
