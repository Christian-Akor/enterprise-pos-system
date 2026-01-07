# Enterprise POS System

A comprehensive, enterprise-level Point of Sale (POS) system built with Java Spring Boot, React TypeScript, and AWS infrastructure. Designed as a multi-tenant SaaS solution with offline-first PWA capabilities.

## 🚀 Features

### Core Functionality
- ✅ Sales transactions & checkout
- ✅ Inventory management
- ✅ Product catalog & barcode scanning
- ✅ Customer management (CRM)
- ✅ Employee management & permissions (RBAC)
- ✅ Payment processing (cash, card, mobile payments)
- ✅ Receipt printing & email receipts
- ✅ Reports & analytics (sales, inventory, revenue)
- ✅ Multi-store/branch support
- ✅ Offline mode capability (PWA)
- ✅ Loyalty programs & discounts
- ✅ Tax calculations
- ✅ Returns & refunds management

### Technical Features
- Multi-tenant SaaS architecture
- Offline-first Progressive Web App (PWA)
- Desktop application (Electron wrapper)
- Real-time synchronization
- Role-based access control (RBAC)
- Comprehensive audit logging
- RESTful API architecture
- Responsive design for all devices

## 🏗️ Architecture

### Monorepo Structure
```
enterprise-pos-system/
├── backend/              # Java Spring Boot backend
├── frontend/             # React TypeScript frontend
├── desktop/              # Electron desktop wrapper
├── docs/                 # Documentation
├── infrastructure/       # AWS & Docker configs
└── scripts/              # Build and deployment scripts
```

### Technology Stack

#### Backend
- Java 17+
- Spring Boot 3.x
- Spring Security (JWT authentication)
- Spring Data JPA
- PostgreSQL (main database)
- Redis (caching & sessions)
- Flyway (database migrations)
- Maven

#### Frontend
- React 18+
- TypeScript
- TailwindCSS
- Redux Toolkit (state management)
- React Query (data fetching)
- Service Workers (offline mode)
- IndexedDB (local storage)
- Vite (build tool)

#### Desktop
- Electron
- Electron Builder

#### Cloud Infrastructure (AWS)
- RDS (PostgreSQL)
- ElastiCache (Redis)
- S3 (file storage)
- CloudFront (CDN)
- EC2/ECS (compute)
- Route 53 (DNS)
- Certificate Manager (SSL)

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18+ and npm/yarn
- PostgreSQL 14+
- Redis 7+
- Docker & Docker Compose
- AWS CLI (for deployment)

## 🚀 Quick Start

### Using Docker Compose (Recommended)
```bash
# Clone the repository
git clone https://github.com/Christian-Akor/enterprise-pos-system.git
cd enterprise-pos-system

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# API Docs: http://localhost:8080/swagger-ui.html
```

### Manual Setup

#### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

#### Desktop App
```bash
cd desktop
npm install
npm run electron:dev
```

## 📚 Documentation

- [Architecture Documentation](./docs/ARCHITECTURE.md)
- [API Documentation](./docs/API.md)
- [Database Schema](./docs/DATABASE.md)
- [Deployment Guide](./docs/DEPLOYMENT.md)
- [Multi-tenancy Guide](./docs/MULTI_TENANCY.md)
- [Offline Mode Guide](./docs/OFFLINE_MODE.md)
- [Contributing Guide](./docs/CONTRIBUTING.md)

## 🔐 Security

- JWT-based authentication
- Role-based access control (RBAC)
- API rate limiting
- SQL injection prevention
- XSS protection
- CSRF protection
- Data encryption at rest and in transit
- Multi-tenant data isolation

## 🧪 Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm run test

# E2E tests
npm run test:e2e
```

## 📦 Building for Production

```bash
# Build all components
./scripts/build-all.sh

# Build individually
cd backend && ./mvnw clean package
cd frontend && npm run build
cd desktop && npm run electron:build
```

## 🚢 Deployment

See [Deployment Guide](./docs/DEPLOYMENT.md) for detailed instructions.

## 📄 License

MIT License - see LICENSE file for details

## 👥 Contributing

See [Contributing Guide](./docs/CONTRIBUTING.md)

## 📞 Support

For support, email support@yourcompany.com or open an issue.