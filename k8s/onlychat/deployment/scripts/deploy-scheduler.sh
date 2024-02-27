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
script_path="${BASH_SOURCE[0]}"
priority_files=()

# Resolve the directory in case the path is a symlink
while [ -h "$script_path" ]; do
  script_dir="$( cd -P "$( dirname "$script_path" )" && pwd )"
  script_path="$(readlink "$script_path")"
  [[ $script_path != /* ]] && script_path="$script_dir/$script_path"
done
script_dir="$( cd -P "$( dirname "$script_path" )" && pwd )"

# Now, get the parent directory of the script directory
deployment_directory="$(dirname "$script_dir")"

echo "Set deployment directory: $deployment_directory"

# Add the files to the priority groups
for file in "$deployment_directory"/p*-deployment.yaml; do
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
