FROM docker.elastic.co/logstash/logstash:7.17.8

RUN rm -f /usr/share/logstash/pipeline/logstash.conf
ADD /pipeline/ /usr/share/logstash/pipeline/
#ADD /config/ /usr/share/logstash/config/