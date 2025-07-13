#!/bin/bash

echo "🚀 Setting environment variables in Railway..."

# Database
railway variables set SPRING_DATASOURCE_URL="jdbc:mysql://your-host:3306/your-db"
railway variables set SPRING_DATASOURCE_USERNAME="your-username"
railway variables set SPRING_DATASOURCE_PASSWORD="your-password"

# Redis
railway variables set SPRING_DATA_REDIS_HOST="your-redis-host"
railway variables set SPRING_DATA_REDIS_PORT="6379"

# JPA
railway variables set SPRING_JPA_HIBERNATE_DDL_AUTO="update"
railway variables set SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver"

# GitHub OAuth2
railway variables set GITHUB_CLIENT_ID="your-client-id"
railway variables set GITHUB_CLIENT_SECRET="your-client-secret"

echo "✅ Done. Don't forget to trigger a redeploy."
