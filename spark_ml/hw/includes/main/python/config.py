import os


@click.command("--username", help="username unique to databricks on this workspace")
@click.command("--databricks_token", help="")
@click.command("--winequality_white_path", help="")
@click.command("--winequality_red_path", help="")
@click.command("--experiment_id", help="")
def config(
        username: str,
        databricks_url: str,
        databricks_token: str,
        winequality_white_path: str,
        winequality_red_path: str,
        experiment_id: int):

    os.environ["USERNAME"] = username
    os.environ["DATABRICKS_URL"] = databricks_url
    os.environ["DATABRICKS_TOKEN"] = databricks_token
    os.environ["WINEQUALITY_WHITE_PATH"] = winequality_white_path
    os.environ["WINEQUALITY_RED_PATH"] = winequality_red_path
    os.environ["EXPERIMENT_ID"] = experiment_id
