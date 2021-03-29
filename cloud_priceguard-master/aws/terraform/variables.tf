variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "budget_name" {
  description = "The name of a budget. Unique within accounts."
  type        = string
  default     = "EPAMCloudBudget"
}

variable "budget_budget_type" {
  description = "Whether this budget tracks monetary cost or usage."
  type        = string
  default     = "COST"
}

variable "budget_limit_amount" {
  description = "The amount of cost or usage being measured for a budget."
  type        = number
  default     = 250
}

variable "budget_limit_unit" {
  description = "The unit of measurement used for the budget forecast, actual spend, or budget threshold, such as dollars or GB."
  type        = string
  default     = "USD"
}

variable "budget_time_period_end" {
  description = "The end of the time period covered by the budget. There are no restrictions on the end date."
  type        = string
  default     = "2050-03-11_00:00"
}

variable "budget_time_period_start" {
  description = "The start of the time period covered by the budget. The start date must come before the end date."
  type        = string
  default     = "2040-03-11_00:00"
}

variable "budget_time_unit" {
  description = "The length of time until a budget resets the actual and forecasted spend. Valid values: MONTHLY, QUARTERLY, ANNUALLY."
  type        = string
  default     = "ANNUALLY"
}

variable "budget_comparison_operator" {
  description = "Comparison operator to use to evaluate the condition. Can be LESS_THAN, EQUAL_TO or GREATER_THAN."
  type        = string
  default     = "GREATER_THAN"
}
variable "budget_treshold" {
   description = "Threshold when the notification should be sent."
   type        = list(number)
   default     = [
     50,
     60,
     70,
     80,
     90,
     95,
     96,
     97,
     98,
     99
  ]
}

variable "budget_threshold_type" {
  description = "What kind of threshold is defined. Can be PERCENTAGE OR ABSOLUTE_VALUE."
  type        = string
  default     = "PERCENTAGE"
}

variable "budget_notification_type" {
  description = "What kind of budget value to notify on. Can be ACTUAL or FORECASTED"
  type        = string
  default     = "ACTUAL"
}

variable "budget_subscriber_email_addresses" {
  description = "E-Mail addresses to notify."
  type        = list(string)
  default     = ["default@epam.com"]
}
