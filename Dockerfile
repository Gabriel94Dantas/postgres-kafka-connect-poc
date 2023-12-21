FROM confluentinc/cp-kafka-connect:7.1.0-1-ubi8
ENV CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components"
RUN confluent-hub install --no-prompt debezium/debezium-connector-postgresql:2.2.1
RUN confluent-hub install --no-prompt confluentinc/connect-transforms:latest
RUN confluent-hub install --no-prompt jcustenborder/kafka-connect-transform-common:latest
RUN mkdir /usr/share/confluent-hub-components/insertuuid
COPY /target/*.jar /usr/share/java/