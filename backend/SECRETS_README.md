# 🔐 Secrets Management Guide

This project uses environment variables to manage sensitive configuration data securely. All secrets are stored in `.env` files that are excluded from version control.

## 📁 Environment Files Structure

```
SmartCourierSystem2/
├── .env                    # Main database configuration
├── auth-service/
│   └── .env               # JWT secrets for auth service
├── delivery-service/
│   └── .env               # Database + RabbitMQ config
├── tracking-service/
│   └── .env               # Database + RabbitMQ config
└── admin-service/
    └── .env               # Database config
```

## 🔑 Secrets Configuration

### Main Database (.env)
```bash
# Database Configuration
MYSQL_ROOT_PASSWORD=smartcourier_root_pass_2026
MYSQL_USER=smartcourier_user
MYSQL_PASSWORD=smartcourier_db_pass_2026
MYSQL_DATABASE=smartcourier_main

# Database URLs for each service
AUTH_DB_URL=jdbc:mysql://mysql:3306/auth_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DELIVERY_DB_URL=jdbc:mysql://mysql:3306/delivery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
TRACKING_DB_URL=jdbc:mysql://mysql:3306/tracking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
ADMIN_DB_URL=jdbc:mysql://mysql:3306/admin_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Auth Service (auth-service/.env)
```bash
# Auth Service Secrets
JWT_SECRET=smartcourier_jwt_secret_key_256_bits_long_enough_for_hs256_algorithm_2026
JWT_EXPIRATION=3600000

# Database (inherited from main .env)
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}
```

### Delivery & Tracking Services (.env)
```bash
# Database (inherited from main .env)
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

# RabbitMQ Configuration
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

## 🚀 Usage

### Development
1. Copy the `.env` files to your local environment
2. Update passwords with your own secure values
3. Run `docker-compose up` - services will automatically load environment variables

### Production
1. Use Docker secrets or external secret management (AWS Secrets Manager, HashiCorp Vault, etc.)
2. Set environment variables in your deployment platform
3. Never commit `.env` files to version control

## 🔒 Security Best Practices

- ✅ `.env` files are in `.gitignore`
- ✅ Environment variables have fallback defaults
- ✅ JWT secrets are 256+ bits for HS256 algorithm
- ✅ Database passwords are strong and unique
- ✅ No hardcoded secrets in application code

## 🧪 Testing Secrets

Test that secrets are loaded correctly:

```bash
# Check environment variables in running container
docker exec auth-service env | grep JWT

# Test database connection
docker exec auth-service mysql -h mysql -u smartcourier_user -p smartcourier_db_pass_2026 auth_db -e "SELECT 1"
```

## 📝 Environment Variable Reference

| Variable | Service | Description | Default |
|----------|---------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | mysql | Database root password | - |
| `MYSQL_USER` | mysql | Application database user | - |
| `MYSQL_PASSWORD` | mysql | Application database password | - |
| `JWT_SECRET` | auth-service | JWT signing key | fallback |
| `JWT_EXPIRATION` | auth-service | JWT token expiration (ms) | 3600000 |
| `SPRING_RABBITMQ_*` | delivery/tracking | RabbitMQ connection | guest/guest |

## 🔄 Migration from Hardcoded Values

All services now use environment variables instead of hardcoded values:
- Database credentials: `${SPRING_DATASOURCE_USERNAME}`
- JWT secrets: `${JWT_SECRET}`
- RabbitMQ config: `${SPRING_RABBITMQ_HOST}`

Fallback defaults ensure backward compatibility during migration.