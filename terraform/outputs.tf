output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.beer_catalogue.endpoint
}

output "rds_host" {
  description = "RDS hostname (without port)"
  value       = aws_db_instance.beer_catalogue.address
}
