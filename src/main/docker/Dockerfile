FROM openjdk:8-jre-alpine

ARG UBER_JAR

ADD ${UBER_JAR} /root/family-tree.jar
ADD docker-entrypoint.sh /root/docker-entrypoint.sh

RUN chmod u+x /root/docker-entrypoint.sh

ENTRYPOINT [ "/root/docker-entrypoint.sh" ]
CMD [ "family-tree" ]