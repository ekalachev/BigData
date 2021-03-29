# PriceGuard project
Simple tool for local workstation setup, which will help you safely manage and monitor your cloud budget.

## Requirements:
* [Terraform v0.12.23](https://releases.hashicorp.com/terraform/0.12.23/)
* Python3
* Bash (at least git-bash)


# How-to
## Google cloud
* Create a service account for your project using [IAM/Service accounts](https://console.cloud.google.com/iam-admin/serviceaccounts) page with Project Owner role. Create a key (JSON) and download it.
* Grant billing privilege to this account
  * in [Billing](https://console.cloud.google.com/billing) section choose your billing account, click on 'Account management' section in left menu,
  * in the right menu click on 'add member'
  * paste your service account's email address
  * select role Billing->Billing account administrator
  * click 'save'
* Edit config file (copy config.sample.yaml in this repo to config.yaml
```yaml
priceguard:
  logfile_path: "/tmp/priceguard.log"               # path to log file. just debug info
  budget_limit: "100"                               # budget limit
  gcp:
    active: true                                    # activate PriceGuard for GCP
    budget_currency: "RUB"                          # it should be the same as your account's origin. RUB for Russia, USD for USA, ...
    service_account:
      credentials_file: "/path/to/secret.json"      # path to your service account's key json
    billing_account:
      name: "my_billing_account"                    # name of your billing account (see "Billing" section"
```

* Execute run.py

Notifications will be sent via email to all users who has any billing role.

## Amazon WebServices
* Create access keys for an IAM user
  * In [Your Security Credentials](https://console.aws.amazon.com/iam/home#/security_credentials) choose 'Access keys section' and tap 'Create access key'.
  * To view the new access key pair, choose 'Show'. You will not have access to the secret access key again after this dialog box closes. Your credentials will look something like this:
      * Access key ID: AKIAIOSFODNN7EXAMPLE
      * Secret access key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
  * To download the key pair, choose Download .csv file.
  * After you download the .csv file, choose 'Close'. When you create an access key, the key pair is active by default, and you can use the pair right away.

* Install the current AWS CLI:
```
 $ pip3 install awscli --upgrade --user
```
[Configuring the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html)
Example:
```
$ aws configure
  AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
  AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
  Default region name [None]: us-west-2                                   # The Default region name identifies the AWS Region whose servers you want to send your requests to by default. This is typically the Region closest to you, but it can be any Region.
  Default output format [None]: json                                      # json, yaml, text, table
```
* Linux or macOS:
```
$ ls  ~/.aws/credentials
  [default]
  aws_access_key_id=AKIAIOSFODNN7EXAMPLE
  aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY

$ ls ~/.aws/config
  [default]
  region=us-west-2
  output=json
```
* Windows:
```
C:\> dir "%UserProfile%\.aws"
```
* Edit config file (copy config.sample.yaml in this repo to config.yaml
```yaml
priceguard:
  logfile_path: "/tmp/priceguard.log"               # path to log file. just debug info
  budget_limit: "100"                               # budget limit
  aws:
    active: true                                    # activate PriceGuard for AWS
    profile: "default"                              # must stay default
    region: "us-west-2"                             # the default region name, it should be the same as your account's configuration.
    budget_currency: "USD"                          # it should be the same as your account's origin. RUB for Russia, USD for USA, ...
    budget_start: "2020-03-11_00:00"                # the start of the time period covered by the budget.
    emails:                                         # e-mail addresses to notify.
      - "john_doe@epam.com"
```
* Execute run.py

## MS Azure
* Installing the Azure CLI

  * [Windows](https://docs.microsoft.com/ru-ru/cli/azure/install-azure-cli-windows?view=azure-cli-latest)
  * [macOS](https://docs.microsoft.com/ru-ru/cli/azure/install-azure-cli-macos?view=azure-cli-latest)
  * [Linux](https://docs.microsoft.com/ru-ru/cli/azure/install-azure-cli-apt?view=azure-cli-latest)

* Edit config file (copy config.sample.yaml in this repo to config.yaml
```yaml
priceguard:
  logfile_path: "/tmp/priceguard.log"               # path to log file. just debug info
  budget_limit: "100"                               # budget limit (in the same currency as your account)
  azure:
    active: true
    budget_start: "2020-03-01"                      # it should be first day of the month
    emails:
      - "john_doe@epam.com"                         # mailbox to which notifications are sent
    subscription_name: "Free\\ Trial"

```
* Execute run.py
