#!/bin/bash

echo "(step-1) confluent-hub cli 다운로드 및 압축해제"
curl -LO http://client.hub.confluent.io/confluent-hub-client-latest.tar.gz
mkdir confluent-etcs | tar -xvzf confluent-hub-client-latest.tar.gz -C confluent-etcs
#
echo "(step-2) confluent-hub 환경변수 설정"
echo $(pwd)
export CONFLUENT_HOME=$(pwd)/confluent-etcs
export PATH=$PATH:$CONFLUENT_HOME/bin
#
echo "(step-3) cli를 통한 jdbc connector 다운로드"
$CONFLUENT_HOME/bin/confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.6.0 --component-dir $(pwd)/confluent-etcs
#
echo "(step-4) debezium connector 컨테이너의 connector리스트에 삽입"
docker cp $(pwd)/confluent-etcs/confluentinc-kafka-connect-jdbc postgres-kafka-source-connector:/kafka/connect

echo "(step-5) debezium connector 컨테이너 재시작으로 loading new connector"
docker restart postgres-kafka-source-connector