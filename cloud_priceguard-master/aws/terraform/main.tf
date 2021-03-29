provider "aws" {
    region                         = var.aws_region
}

resource "aws_budgets_budget" "cost" {
  name                             = var.budget_name
  budget_type                      = var.budget_budget_type
  limit_amount                     = var.budget_limit_amount
  limit_unit                       = var.budget_limit_unit
  time_period_end                  = var.budget_time_period_end
  time_period_start                = var.budget_time_period_start
  time_unit                        = var.budget_time_unit

  dynamic "notification" {
    for_each = var.budget_treshold
    content {
        comparison_operator        = var.budget_comparison_operator
        threshold                  = notification.value
        threshold_type             = var.budget_threshold_type
        notification_type          = var.budget_notification_type
        subscriber_email_addresses = var.budget_subscriber_email_addresses
      }
    }
}
