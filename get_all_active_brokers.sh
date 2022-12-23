#!/bin/bash
. functions.sh "$1"

function get_all_active_brokers {
broker_ids=$(get_broker_ids)
for broker_id in $broker_ids
do
    broker_details="$(get_broker_details $broker_id)"
    broker_endpoint=$(parse_endpoint_detail "$broker_details")
    echo "broker_id="$broker_id,"endpoint="$broker_endpoint
done
}

get_all_active_brokers