terraform {
  backend "azurerm" {
  }
}

provider "azurerm" {
  version = "~> 2.54.0"
  features {
    key_vault {
      purge_soft_delete_on_destroy = true
    }
  }
}

data "azurerm_client_config" "current" {}

resource "azurerm_resource_group" "bdcc" {
  name = "rg-datalake-${var.ENV}-${var.LOCATION}"
  location = var.LOCATION

  lifecycle {
    prevent_destroy = true
  }

  tags = {
    region = var.BDCC_REGION
    env = var.ENV
  }
}

resource "azurerm_storage_account" "bdcc" {
  depends_on = [
    azurerm_resource_group.bdcc]

  name = "stdatalake${var.ENV}${var.LOCATION}"
  resource_group_name = azurerm_resource_group.bdcc.name
  location = azurerm_resource_group.bdcc.location
  account_tier = "Standard"
  account_replication_type = var.STORAGE_ACCOUNT_REPLICATION_TYPE
  is_hns_enabled = "true"

  network_rules {
    default_action = "Allow"
    ip_rules = values(var.IP_RULES)
  }

  lifecycle {
    prevent_destroy = true
  }

  tags = {
    region = var.BDCC_REGION
    env = var.ENV
  }
}

resource "azurerm_storage_data_lake_gen2_filesystem" "gen2_data" {
  depends_on = [
    azurerm_storage_account.bdcc]

  name = "data"
  storage_account_id = azurerm_storage_account.bdcc.id

  lifecycle {
    prevent_destroy = true
  }
}


resource "azurerm_kubernetes_cluster" "bdcc" {
  depends_on = [
    azurerm_resource_group.bdcc]

  name                = "aks-datalake-${var.ENV}-${var.LOCATION}"
  location            = azurerm_resource_group.bdcc.location
  resource_group_name = azurerm_resource_group.bdcc.name
  dns_prefix          = "bdcc${var.ENV}"

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_D2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  tags = {
    region = var.BDCC_REGION
    env = var.ENV
  }
}

output "client_certificate" {
  value = azurerm_kubernetes_cluster.bdcc.kube_config.0.client_certificate
}

output "kube_config" {
  value = azurerm_kubernetes_cluster.bdcc.kube_config_raw
}
