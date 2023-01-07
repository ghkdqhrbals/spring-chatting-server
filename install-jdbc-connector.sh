#!/bin/bash

echo "Installing confluent-hub cli"
curl -LO http://client.hub.confluent.io/confluent-hub-client-latest.tar.gz
mkdir confluent-etcs | tar -xvzf confluent-hub-client-latest.tar.gz -C confluent-etcs
#
echo "Insert directories"
echo $(pwd)
export CONFLUENT_HOME=$(pwd)/confluent-etcs
export PATH=$PATH:$CONFLUENT_HOME/bin
#
echo "Installing connector"
$CONFLUENT_HOME/bin/confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.6.0 --component-dir $(pwd)/confluent-etcs
#
echo "Copy connector to kafka/libs"

docker cp $(pwd)/confluent-etcs/confluentinc-kafka-connect-jdbc postgres-kafka-source-connector:/kafka/connect
docker restart postgres-kafka-source-connector