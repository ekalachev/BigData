variable "secretfile_path" {
  description = "Absolute path to secret.json file"
  type        = string
  default     = "/tmp/__secret.json"
}

variable "budget_currency_code" {
  description = "Budget currency. NB: should be same as account's budget"
  type        = string
  default     = "RUB"
}

variable "budget_unit" {
  description = "Budget unit, in currency above"
  type        = number
  default     = 50
}

variable "budget_limits" {
  description = "Set notifications on budget limits below (1=100%)"
  type        = list(number)
  default     = [
    0.5,
    0.6,
    0.7,
    0.8,
    0.9,
    0.95,
    0.96,
    0.97,
    0.98,
    0.99,
    1
  ]
}

variable "budget_billing_account_name" {
  description = "Name of billing account created manually. Used for discovery"
  type        = string
  default     = "my_billing_account"
}

variable "budget_name" {
  description = "Budget display name"
  type        = string
  default     = "EPAMCloudBudget"
}
