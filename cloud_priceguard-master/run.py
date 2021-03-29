#!/usr/bin/env python3
import datetime
import os
import subprocess
import yaml


def run_aws(config):
    from_date = str(config['priceguard']['aws']['budget_start'])
    to_date = datetime.datetime.strftime(
        datetime.datetime.strptime(from_date, '%Y-%m-%d_%H:%M') +
        datetime.timedelta(days=365),
        '%Y-%m-%d_%H:%M')
    subprocess.call([
        './aws/run.sh',
        str(config['priceguard']['logfile_path']),
        str(config['priceguard']['aws']['profile']),
        str(config['priceguard']['aws']['region']),
        str(config['priceguard']['aws']['budget_currency']),
        str(config['priceguard']['budget_limit']),
        from_date,
        str(config['priceguard']['aws']['emails']).replace("'", '"'),
        to_date,
    ]
    )


def run_gcp(config):
    subprocess.call([
        './gcp/run.sh',
        str(config['priceguard']['logfile_path']),
        str(config['priceguard']['gcp']['service_account']['credentials_file']),
        str(config['priceguard']['gcp']['budget_currency']),
        str(config['priceguard']['budget_limit']),
        str(config['priceguard']['gcp']['billing_account']['name']),
    ]
    )


def run_azure(config):
    from_date = str(config['priceguard']['azure']['budget_start'])
    to_date = datetime.datetime.strftime(
        datetime.datetime.strptime(from_date, '%Y-%m-%d') +
        datetime.timedelta(days=365),
        '%Y-%m-%d')
    subprocess.call([
        'python3', './azure/run.py',
        '--subscription_name', str(config['priceguard']['azure']['subscription_name']),
        '--contact_email', ','.join(config['priceguard']['azure']['emails']),
        '--budget_amount', str(config['priceguard']['budget_limit']),
        '--start_date', from_date,
        '--end_date', to_date,
        'budget',
    ]
    )


def check_requirements(cloud_name, config):
    with open(os.path.join(
            os.path.dirname(os.path.realpath(__file__)),
            cloud_name,
            "requirements.txt"),
            'r') as f:
        requirements = [s[:-1] for s in f.readlines()]

    for req in requirements:
        with open(os.devnull, "w") as f:
            rc = subprocess.call(['which', req], stdout=f, stderr=f, shell=True)
        if rc != 0:
            raise Exception(f"{req} is not installed on a system. Please install it.")


runners = {
    "aws": run_aws,
    "gcp": run_gcp,
    "azure": run_azure
}


def main():
    with open('config.yaml', 'r') as f:
        config = yaml.load(f)

    for cloud_name in runners.keys():
        if config['priceguard'][cloud_name]['active']:
            check_requirements(cloud_name, config)
            runners[cloud_name](config)


if __name__ == '__main__':
    main()
