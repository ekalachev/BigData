# Spark WSL Install

Apache Spark Windows Subsystem for Linux (WSL) Install

## Enable WSL

Go to Start → Control Panel → Turn Windows features on or off. Check Windows Subsystem for Linux.

## Install Ubuntu

Go to Start → Microsoft Store. Search for Ubuntu. Select Ubuntu then Get and Launch to install the Ubuntu terminal on Windows (if the install hangs, you may need to press Enter). Create a username and password.

## Launch Ubuntu

Go to Start → Command Prompt. This launches the Windows command prompt. To launch Ubuntu type bash and press Enter. Now you have the power of a Linux terminal on your Windows machine.

## Download and Install OpenJDK 8

1. Execute the following commands to download and install OpenJDK 8:

    ```cmd
    sudo apt-get update
    sudo apt-get install openjdk-8-jdk
    ```

2. Select needed spark version [spark](https://spark.apache.org/downloads.html) then copy the link and use it below. Make next steps:
    - `mkdir Ubuntu`
    - `cd Ubuntu`
    - `mkdir Downloads`
    - `cd Downloads`
    - `wget http://apache.cs.utah.edu/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz`
    - `cd ..`
    - `mkdir Programs`
    - `tar -xvzf Downloads/spark-3.1.1-bin-hadoop3.2.tgz -C Programs`

3. Once installed, open your .bashrc file in the nano text editor by executing the following command:

    ```cmd
    sudo vim ~/.bashrc
    ```

    - Enter your password.
    - Press i to go into edit mode. Go to the end of the file using arrow key.
    - JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
    - SPARK_HOME="/mnt/d/Ubuntu/Programs/spark-3.1.1-bin-hadoop3.2"
    - Press `esc` key to get out of edit mode.
    - Enter `:wq` and press enter . This will save and close the file.
    - `source ~/.bashrc` will load your recent changes into your current shel