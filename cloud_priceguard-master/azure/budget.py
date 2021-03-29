#!/usr/bin/env python

import os
import sys
import json
import datetime
import dateparser
import requests
import subprocess


PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), '')

def authorization(subscription_name):
    try:
        current_token_expiry_date = subprocess.check_output(['az account get-access-token --query "expiresOn" --output tsv'], stderr=subprocess.DEVNULL, shell=True, timeout=10)
        current_token_expiry_date = dateparser.parse(current_token_expiry_date.decode())
    except:
        raise ValueError("ERROR. Cannot verify azure token relevance")
    if isinstance(current_token_expiry_date, datetime.date) and current_token_expiry_date < datetime.datetime.now():
        try:
            subprocess.run(['az login'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, shell=True, timeout=120)
            print("SUCCESS. You have logged in.")
        except:
            raise ValueError('ERROR. Login failed or timeout was reached\nPlease, try again')

    azure_account_info = subprocess.check_output(['az account list'], shell=True)

    azure_subscriptions_list = json.loads(azure_account_info)
    azure_matched_subscription = next(x for x in azure_subscriptions_list if 'name:' f'{subscription_name}')
    azure_subscription_id = azure_matched_subscription['id']

    azure_access_token_response = subprocess.check_output(['az account get-access-token'], shell=True)
    azure_access_token = json.loads(azure_access_token_response)['accessToken']
    return azure_subscription_id, azure_access_token,


def parse_json(contact_emails, budget_amount, start_date, end_date):
    with open(PATH + 'EPAMCloudBudget_1_body.json', 'r') as json_file:
        body = json.load(json_file)
    for key in body['properties']['notifications'].keys():
        body['properties']['notifications'][key]['contactEmails'] = [contact_emails]
    body['properties']['amount'] = budget_amount
    body['properties']['timePeriod']['startDate'] = start_date
    body['properties']['timePeriod']['endDate'] = end_date
    with open(PATH + 'EPAMCloudBudget_1_body.json', 'w+') as json_file:
       json.dump(body, json_file)


    with open(PATH + 'EPAMCloudBudget_2_body.json', 'r') as json_file:
        body = json.load(json_file)
    for key in body['properties']['notifications'].keys():
        body['properties']['notifications'][key]['contactEmails'] = [contact_emails]
    body['properties']['amount'] = budget_amount
    body['properties']['timePeriod']['startDate'] = start_date
    body['properties']['timePeriod']['endDate'] = end_date
    with open(PATH + 'EPAMCloudBudget_2_body.json', 'w+') as json_file:
       json.dump(body, json_file)


def get_budgets(ctx):
    existing_budgets = []
    azure_subscription_id, azure_access_token = authorization(ctx.obj['subscription_name'])
    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + azure_access_token
    }
    response = requests.get('https://management.azure.com/subscriptions/' + azure_subscription_id + '/providers/Microsoft.Consumption/budgets?api-version=2019-10-01',
    headers=headers
    )
    response_json = json.loads(response.content)
    for element in response_json['value']:
        existing_budgets.append(element['name'])
    if response.status_code != 200:
        raise ValueError('ERROR. Cannot retrive budgets.\nPlease check your subscription name and try again.\nIf you faced with this issue again please contact the developers')
    return existing_budgets


def create_budget(ctx):
    azure_subscription_id, azure_access_token = authorization(ctx.obj['subscription_name'])
    existing_budgets = get_budgets(ctx)

    for existing_budget in existing_budgets:
        if existing_budget == 'EPAMCloudBudget_1' or existing_budget == 'EPAMCloudBudget_2':
            raise ValueError('ERROR. Budget already exist')

    parse_json(ctx.obj['contact_email'], ctx.obj['budget_amount'], ctx.obj['start_date'], ctx.obj['end_date'])

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + azure_access_token
    }
    for file_counter in range (1,3):
        with open(PATH + 'EPAMCloudBudget_' + str(file_counter) + '_body.json', 'rb') as json_budget_body:
            response = requests.put('https://management.azure.com/subscriptions/' + azure_subscription_id + '/providers/Microsoft.Consumption/budgets/EPAMCloudBudget_' + str(file_counter) + '?api-version=2019-10-01',
            headers=headers,
            data=json_budget_body
            )
        if response.status_code != 201:
            raise ValueError('ERROR. Please check your subscription name and try again.\nIf you faced with this issue again please contact the developers')
        elif file_counter == 2:
            print('SUCCESS. The budget was created.')
