#!/bin/bash
# Author: Gyumin HwangBo
# Date: 2024-02-25
# Version: 1.0
#
# This script deploys Kubernetes resources based on priority.
# Files should be named in the format p<order>-<resource-name>-deployment.yaml.
# For example, p1-namespace-deployment.yaml indicates the highest priority.
#
# Resources in each priority group are deployed sequentially, waiting 10 seconds
# between each deployment for stabilization.
# Then it moves to the next priority group.

default_wait_time=20
current_dir=$(pwd)
deployment_dir=$(dirname "$current_dir")
declare -a priority_files

# Add the files to the priority groups
for file in "$deployment_dir"/p*-deployment.yaml; do
    filename=$(basename "$file")
    priority=$(echo "$filename" | cut -d'-' -f1 | tr -d 'p')
    index=$((priority - 1))
    priority_files[$index]+=" $file"
done

# Sort the files in each priority group
sorted_priority_files=()
for files in "${priority_files[@]}"; do
    sorted_files=$(echo "$files" | tr ' ' '\n' | sort)
    sorted_priority_files+=("$sorted_files")
done

# Deploy to Kubenetes
for files in "${priority_files[@]}"; do
    for file in $files; do
        echo "Applying $file..."
        kubectl apply -f $file
    done

    sleep "$default_wait_time"
    echo "Waiting for $default_wait_time seconds to stabilize the deployment"
done

echo "All deployments completed"
