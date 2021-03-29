data "google_billing_account" "account" {
  provider      = google-beta
  display_name  = var.budget_billing_account_name
}

resource "google_billing_budget" "budget" {
  provider = google-beta
  billing_account = data.google_billing_account.account.id
  display_name = var.budget_name

  budget_filter {
    credit_types_treatment = "EXCLUDE_ALL_CREDITS"
  }

  amount {
    specified_amount {
      currency_code = var.budget_currency_code
      units = var.budget_unit
    }
  }

  dynamic "threshold_rules" {
    for_each = var.budget_limits
    content {
      threshold_percent = threshold_rules.value
    }
  }
}
