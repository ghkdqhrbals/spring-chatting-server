#!/bin/bash

# Get directory of script
  DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
  
  if [[ $# -ne 1 ]]
  then
echo "ERROR: This script expects the namespace name to be given as an argument"
  echo "e.g. ./ecr-cred-updater.sh my-namespace"
  exit 1
  fi
  
  # Steal the aws creds from the user's configuration for awscli
  AWS_ACCESS_KEY_ID=`cat ~/.aws/credentials | grep aws_access_key_id | head -1 | cut -d'=' -f2 | sed 's/ //g'`
  AWS_SECRET_ACCESS_KEY=`cat ~/.aws/credentials | grep aws_secret_access_key | head -1 | cut -d'=' -f2 | sed 's/ //g'`
  if [ -z "$AWS_ACCESS_KEY_ID" ]
  then
echo "ERROR: Failed to work out the AWS_ACCESS_KEY_ID"
  exit 1
  fi
  
  if [ -z "$AWS_SECRET_ACCESS_KEY" ]
  then
echo "ERROR: Failed to work out the AWS_SECRET_ACCESS_KEY"
  exit 1
  fi
  
  # Fill in the variables in the yaml and run kubectl
  cat $DIR/ecr-cred-updater.yaml | envsubst '$AWS_ACCESS_KEY_ID $AWS_SECRET_ACCESS_KEY' | kubectl apply -n ${1} -f -