# Deployment Guide - Enterprise POS System

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [AWS Production Deployment](#aws-production-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Database Setup](#database-setup)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software
- **Java 17+** (for backend development)
- **Node.js 18+** and npm (for frontend development)
- **PostgreSQL 14+** (local development)
- **Redis 7+** (caching)
- **Docker & Docker Compose** (containerization)
- **AWS CLI** (for AWS deployment)
- **Git** (version control)

### AWS Account Setup
- Active AWS account
- IAM user with appropriate permissions
- AWS CLI configured with credentials

## Local Development Setup

### 1. Clone Repository
```bash
git clone https://github.com/Christian-Akor/enterprise-pos-system.git
cd enterprise-pos-system
```

### 2. Backend Setup
```bash
cd backend

# Create application-local.yml for local config
cat > src/main/resources/application-local.yml << EOF
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pos_db
    username: postgres
    password: your_password
  redis:
    host: localhost
    port: 6379
jwt:
  secret: your-local-secret-key-min-256-bits
EOF

# Build and run
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Backend will be available at: `http://localhost:8080`

### 3. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Create .env.local file
cat > .env.local << EOF
VITE_API_URL=http://localhost:8080/api/v1
VITE_APP_NAME=Enterprise POS
EOF

# Run development server
npm run dev
```

Frontend will be available at: `http://localhost:3000`

### 4. Database Setup
```bash
# Create database
psql -U postgres
CREATE DATABASE pos_db;
\q

# Flyway will automatically run migrations on startup
```

## Docker Deployment

### Using Docker Compose (Recommended for Development)
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

Services will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432
- Redis: localhost:6379

### Build Individual Images
```bash
# Backend
cd backend
docker build -t pos-backend:latest .

# Frontend
cd frontend
docker build -t pos-frontend:latest .
```

## AWS Production Deployment

### Architecture Overview
```
Route 53 → CloudFront → ALB → ECS (Backend + Frontend)
                                ↓
                         RDS + ElastiCache + S3
```

### 1. Set Up AWS Infrastructure

#### Create RDS PostgreSQL Instance
```bash
aws rds create-db-instance \
    --db-instance-identifier pos-production-db \
    --db-instance-class db.t3.medium \
    --engine postgres \
    --master-username posadmin \
    --master-user-password <secure-password> \
    --allocated-storage 100 \
    --vpc-security-group-ids sg-xxxxx \
    --db-subnet-group-name pos-db-subnet-group \
    --backup-retention-period 7 \
    --multi-az
```

#### Create ElastiCache Redis Cluster
```bash
aws elasticache create-cache-cluster \
    --cache-cluster-id pos-redis-cluster \
    --cache-node-type cache.t3.micro \
    --engine redis \
    --num-cache-nodes 1 \
    --cache-subnet-group-name pos-cache-subnet-group \
    --security-group-ids sg-xxxxx
```

#### Create S3 Bucket
```bash
aws s3 mb s3://enterprise-pos-files-production
aws s3api put-bucket-versioning \
    --bucket enterprise-pos-files-production \
    --versioning-configuration Status=Enabled
```

### 2. Set Up ECS (Elastic Container Service)

#### Create ECR Repositories
```bash
# Backend repository
aws ecr create-repository --repository-name pos-backend

# Frontend repository
aws ecr create-repository --repository-name pos-frontend
```

#### Build and Push Images
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | \
    docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and push backend
cd backend
docker build -t pos-backend:latest .
docker tag pos-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/pos-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/pos-backend:latest

# Build and push frontend
cd frontend
docker build -t pos-frontend:latest .
docker tag pos-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/pos-frontend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/pos-frontend:latest
```

#### Create ECS Cluster
```bash
aws ecs create-cluster --cluster-name pos-production-cluster
```

#### Create Task Definitions
See `infrastructure/aws/ecs-task-definition.json`

```bash
aws ecs register-task-definition \
    --cli-input-json file://infrastructure/aws/ecs-task-definition.json
```

#### Create ECS Service
```bash
aws ecs create-service \
    --cluster pos-production-cluster \
    --service-name pos-backend-service \
    --task-definition pos-backend-task \
    --desired-count 2 \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx,subnet-yyy],securityGroups=[sg-xxx]}" \
    --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:region:account-id:targetgroup/pos-backend-tg
```

### 3. Set Up Application Load Balancer

```bash
# Create ALB
aws elbv2 create-load-balancer \
    --name pos-production-alb \
    --subnets subnet-xxx subnet-yyy \
    --security-groups sg-xxx \
    --scheme internet-facing

# Create target groups and listeners
# See infrastructure/aws/alb-config.json
```

### 4. Set Up CloudFront CDN

```bash
aws cloudfront create-distribution \
    --distribution-config file://infrastructure/aws/cloudfront-config.json
```

### 5. Configure Route 53 DNS

```bash
# Create hosted zone
aws route53 create-hosted-zone --name yourposapp.com

# Create A record pointing to CloudFront
aws route53 change-resource-record-sets \
    --hosted-zone-id Z1234567890ABC \
    --change-batch file://infrastructure/aws/route53-records.json
```

## Environment Configuration

### Backend Environment Variables (Production)
```bash
SPRING_PROFILES_ACTIVE=production
DB_HOST=pos-production-db.xxxxx.us-east-1.rds.amazonaws.com
DB_PORT=5432
DB_NAME=pos_production
DB_USERNAME=posadmin
DB_PASSWORD=<secure-password>
REDIS_HOST=pos-redis-cluster.xxxxx.cache.amazonaws.com
REDIS_PORT=6379
JWT_SECRET=<secure-256-bit-secret>
AWS_REGION=us-east-1
AWS_S3_BUCKET=enterprise-pos-files-production
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=<sendgrid-api-key>
```

### Frontend Environment Variables (Production)
```bash
VITE_API_URL=https://api.yourposapp.com/api/v1
VITE_APP_NAME=Enterprise POS
VITE_ENVIRONMENT=production
```

## Database Setup

### Initial Schema Creation
```bash
# Run Flyway migrations
./mvnw flyway:migrate

# Or let Spring Boot auto-run on startup
```

### Create Initial Tenant
```sql
-- Create public schema tenant
INSERT INTO tenants (id, tenant_id, business_name, business_email, subscription_plan, status, active)
VALUES (gen_random_uuid(), 'default', 'Demo Business', 'demo@example.com', 'ENTERPRISE', 'ACTIVE', true);

-- Create tenant schema
CREATE SCHEMA tenant_default;
```

### Seed Data
```bash
# Run seed scripts
psql -h localhost -U postgres -d pos_db -f infrastructure/db/seed.sql
```

## CI/CD Pipeline

### GitHub Actions Workflow

See `.github/workflows/deploy.yml`

```yaml
# Automated deployment on push to main branch
- Build and test
- Build Docker images
- Push to ECR
- Update ECS service
```

### Manual Deployment
```bash
# Deploy backend
./scripts/deploy-backend.sh production

# Deploy frontend
./scripts/deploy-frontend.sh production
```

## SSL/TLS Configuration

### AWS Certificate Manager
```bash
# Request certificate
aws acm request-certificate \
    --domain-name yourposapp.com \
    --subject-alternative-names *.yourposapp.com \
    --validation-method DNS

# Configure CloudFront and ALB to use certificate
```

## Monitoring & Logging

### CloudWatch Setup
```bash
# Create log groups
aws logs create-log-group --log-group-name /ecs/pos-backend
aws logs create-log-group --log-group-name /ecs/pos-frontend

# Set retention
aws logs put-retention-policy \
    --log-group-name /ecs/pos-backend \
    --retention-in-days 30
```

### Application Monitoring
- Enable Spring Boot Actuator endpoints
- Configure CloudWatch metrics
- Set up alarms for critical metrics

## Backup Strategy

### Database Backups
```bash
# Automated RDS backups (daily)
aws rds modify-db-instance \
    --db-instance-identifier pos-production-db \
    --backup-retention-period 30

# Manual snapshot
aws rds create-db-snapshot \
    --db-instance-identifier pos-production-db \
    --db-snapshot-identifier pos-manual-snapshot-$(date +%Y%m%d)
```

### S3 Versioning
```bash
# Enable versioning for file recovery
aws s3api put-bucket-versioning \
    --bucket enterprise-pos-files-production \
    --versioning-configuration Status=Enabled
```

## Troubleshooting

### Common Issues

#### Backend won't start
```bash
# Check logs
docker-compose logs backend

# Common causes:
- Database connection failure → Check DB credentials
- Redis connection failure → Check Redis host
- Port already in use → Change port or kill process
```

#### Frontend can't connect to backend
```bash
# Check CORS configuration in backend
# Verify API URL in frontend .env file
# Check network connectivity
```

#### Database migration fails
```bash
# Reset Flyway
./mvnw flyway:clean
./mvnw flyway:migrate

# Or manually fix in database
```

#### ECS Task fails to start
```bash
# Check CloudWatch logs
aws logs tail /ecs/pos-backend --follow

# Common causes:
- Environment variables missing
- ECR image pull authentication
- Resource limits (CPU/Memory)
```

## Security Checklist

- [ ] Change all default passwords
- [ ] Use environment variables for secrets
- [ ] Enable HTTPS/TLS everywhere
- [ ] Configure security groups properly
- [ ] Enable database encryption at rest
- [ ] Set up WAF rules
- [ ] Enable CloudTrail logging
- [ ] Configure backup retention
- [ ] Set up alerting and monitoring
- [ ] Review IAM policies

## Performance Optimization

### Database
- Create appropriate indexes
- Set up read replicas for reporting
- Configure connection pooling
- Monitor slow queries

### Caching
- Implement Redis caching strategy
- Configure cache TTL appropriately
- Monitor cache hit rates

### CDN
- Cache static assets on CloudFront
- Set appropriate cache headers
- Use compression

## Support

For deployment issues, check:
- CloudWatch logs
- Application logs
- Database logs
- ECS service events

Contact: devops@yourcompany.com
