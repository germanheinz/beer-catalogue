variable "aws_region" {
  default = "eu-central-1"
}

variable "db_name" {
  default = "beercatalogue"
}

variable "db_username" {
  default = "beeradmin"
}

variable "db_password" {
  description = "RDS master password"
  default     = "BeerAdmin2026!"
  sensitive   = true
}

variable "db_instance_identifier" {
  default = "beer-catalogue-db"
}
