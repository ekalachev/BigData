# Task 1

## Fallow the [instructions](https://www.cloudera.com/tutorials/sandbox-deployment-and-install-guide/3.html)
- Install ans setup **docker** and **kubernetes** and install resources:
![docker_install](./img/docker_install.png)
![docker_install2](./img/docker_install2.png)
![docker_install3](./img/docker_install3.png)
- Run installation script:
  - HDP_3.0.1_docker-deploy-scripts/docker-deploy-hdp30.sh
  - You can have some issues with ports. Try this:
      - net stop winnat
      - run the installation script
      - net start winnat

![sandbox_hdp_install](./img/sandbox_hdp_install.png)
- Check the link: http://127.0.0.1:8080/
- Reset admin password:
![sandbox_hdp_install2](./img/sandbox_hdp_install2.png)
![sandbox_hdp_install3](./img/sandbox_hdp_install3.png)

## Result:
![ambari_result](./img/ambari_result.png)
