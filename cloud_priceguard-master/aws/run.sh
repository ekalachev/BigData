#!/bin/bash

LOGFILE="$1"
AWS_PROFILE="$2"
AWS_REGION="$3"
BUDGET_CURRENCY="$4"
BUDGET_LIMIT="$5"
BUDGET_START_PERIOD="$6"
BUDGET_EMAIL_ADDRESS="$7"
BUDGET_END_PERIOD="$8"


(
    export AWS_PROFILE && \
    export TF_LOG=TRACE && \
    cd $(dirname $0)/terraform && \
    terraform init && \
    terraform apply -auto-approve \
        -var="aws_region=${AWS_REGION}" \
        -var="budget_limit_unit=${BUDGET_CURRENCY}" \
        -var="budget_limit_amount=${BUDGET_LIMIT}" \
        -var="budget_time_period_start=${BUDGET_START_PERIOD}" \
        -var="budget_time_period_end=${BUDGET_END_PERIOD}" \
        -var="budget_subscriber_email_addresses=${BUDGET_EMAIL_ADDRESS}" 

    ) &> "$LOGFILE" && echo "Success" || echo "Failed. Please check the logs at $LOGFILE and/or contact developers"

