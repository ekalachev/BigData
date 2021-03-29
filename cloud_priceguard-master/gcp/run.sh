#!/bin/bash

LOGFILE="$1"
CREDS_FILE="$2"
BUDGET_CURRENCY="$3"
BUDGET_LIMIT="$4"
BILLING_ACCOUNT="$5"

(
    export GOOGLE_APPLICATION_CREDENTIALS="${CREDS_FILE}" && \
    export TF_LOG=TRACE && \
    cd $(dirname $0)/terraform && \
    terraform init && \
    terraform apply -auto-approve \
        -var="secretfile_path=${CREDS_FILE}" \
        -var="budget_currency_code=${BUDGET_CURRENCY}" \
        -var="budget_unit=${BUDGET_LIMIT}" \
        -var="budget_billing_account_name=${BILLING_ACCOUNT}"

    ) &> "$LOGFILE" && echo "Success" || echo "Failed. Please check the logs at $LOGFILE and/or contact developers"
