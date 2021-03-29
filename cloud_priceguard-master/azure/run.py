#!/usr/bin/env python

import os
import click

from budget import create_budget

class UnSortedGroup(click.Group):
    def __init__(self, *args, **kwargs):
        super(UnSortedGroup, self).__init__(*args, **kwargs)
        self.commands = OrderedDict()

    def list_commands(self, ctx):
        return self.commands


def group(name=None, **attrs):
    attrs.setdefault('cls', UnSortedGroup)
    return click.command(name, **attrs)


@click.group()
@click.pass_context
@click.option('--subscription_name', type=str, default='', help='Azure subscription name')
@click.option('--contact_email', type=str, default='', help='Email for budget alerts')
@click.option('--budget_amount', type=int, default='', help='')
@click.option('--start_date', type=str, default='', help='Start budget date format "yyyy-mm-dd"')
@click.option('--end_date', type=str, default='', help='End budget date format "yyyy-mm-dd"')
def cli(ctx, subscription_name, contact_email, budget_amount, start_date, end_date):
    ctx.obj['subscription_name'] = subscription_name
    ctx.obj['contact_email'] = contact_email
    ctx.obj['budget_amount'] = budget_amount
    ctx.obj['start_date'] = start_date
    ctx.obj['end_date'] = end_date


@cli.command()
@click.pass_context
def budget(ctx):
    create_budget(ctx)


# @cli.group()
# @click.pass_context
# @click.option('--subscription_name', type=str, default='', help='Azure subscription name')
# def schedule():
#     click.echo('Not implemented yet')


if __name__ == '__main__':
    cli(obj={})