#!/bin/bash

# default command argument allows easily changing default behavior of container
if [ "$1" = 'family-tree' ]; then

    exec java -jar /root/spring-boot-graph-api.jar
fi

exec "$@"
