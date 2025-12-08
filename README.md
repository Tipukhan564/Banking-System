# Enterprise Banking System

A comprehensive full-stack banking application built with **Java Spring Boot** (backend) and **React** (frontend), featuring advanced banking operations, real-time transaction processing, and enterprise-grade security.

## Features

### Core Banking Operations
- **Customer Management**: Registration, authentication, and profile management
- **Account Management**: Multiple account types (Savings, Current, Fixed Deposit)
- **Transaction Processing**: Deposits, withdrawals, and internal transfers
- **Card Management**: Debit/Credit card issuance and management

### Advanced Banking Features
- **ISO8583 Message Processing**: Full implementation of ISO8583 standard for ATM/POS transactions
- **Socket Programming**:
  - Apache MINA server (Port 8583)
  - Netty server (Port 8584)
  - Real-time ATM/POS communication
- **IBFT (Inter-Bank Fund Transfer)**: Inter-bank transfers with fee management
- **Raast Integration**: Pakistan's instant payment system implementation
- **Message Queue (Kafka)**: Asynchronous transaction processing
- **Bulk Operations**: CSV-based bulk account opening
- **Reporting**: PDF and Excel report generation

### Security & Encryption
- **JWT Authentication**: Secure token-based authentication
- **KMS Encryption**: Data encryption for sensitive information (CNIC, Card Numbers, CVV)
- **Role-based Access Control**: Fine-grained permission management
- **Security Headers**: CORS, CSRF protection

### Technology Stack

#### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Apache Kafka
- Apache MINA / Netty
- PostgreSQL / H2
- Maven

#### Frontend
- React 18
- Vite
- Tailwind CSS
- Framer Motion (animations)
- Recharts (data visualization)
- Axios

## Architecture

```
Banking-System/
├── src/main/java/com/banking/
│   ├── model/entity/          # JPA entities
│   ├── repository/            # Data access layer
│   ├── service/              # Business logic
│   ├── controller/           # REST APIs
│   ├── security/             # JWT, authentication
│   ├── iso8583/              # ISO8583 implementation
│   ├── socket/               # MINA/Netty servers
│   └── kafka/                # Kafka producers/consumers
├── frontend/
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── pages/           # Page components
│   │   ├── context/         # React context
│   │   └── services/        # API services
│   └── public/
└── docker-compose.yml
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 15
- Apache Kafka
- Maven 3.9+
- Docker (optional)

### Installation

#### Option 1: Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/Banking-System.git
cd Banking-System

# Start all services with Docker Compose
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

#### Option 2: Manual Installation

**Backend Setup:**

```bash
# Navigate to project root
cd Banking-System

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/banking-system-1.0.0.jar
```

**Frontend Setup:**

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

### Configuration

#### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

#### Kafka Configuration
```properties
spring.kafka.bootstrap-servers=localhost:9092
```

## API Documentation

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "cnic": "12345-1234567-1",
  "phoneNumber": "+92300123456",
  "dateOfBirth": "1990-01-01",
  "password": "SecurePass123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

### Account Endpoints

#### Create Account
```http
POST /api/accounts/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "customerId": 1,
  "accountType": "SAVINGS",
  "currency": "PKR"
}
```

#### Get Customer Accounts
```http
GET /api/accounts/customer/{customerId}
Authorization: Bearer {token}
```

### Transaction Endpoints

#### Transfer Funds
```http
POST /api/transactions/transfer
Authorization: Bearer {token}
Content-Type: application/json

{
  "fromAccountNumber": "1234567890123456",
  "toAccountNumber": "9876543210987654",
  "amount": 5000.00,
  "description": "Payment"
}
```

#### IBFT Transfer
```http
POST /api/ibft/transfer
Authorization: Bearer {token}
Content-Type: application/json

{
  "fromAccountNumber": "1234567890123456",
  "toIban": "PK12ABCD1234567890123456",
  "toBankCode": "ABCD",
  "amount": 10000.00,
  "description": "Inter-bank transfer"
}
```

### CSV Bulk Upload

```http
POST /api/csv/bulk-account-opening
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: customers.csv
```

CSV Format:
```csv
firstName,lastName,email,cnic,phoneNumber,dateOfBirth,accountType
John,Doe,john@example.com,12345-1234567-1,+923001234567,1990-01-01,SAVINGS
```

### Reports

#### Generate PDF Report
```http
GET /api/reports/transactions/pdf?accountId=1&startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
Authorization: Bearer {token}
```

#### Generate Excel Report
```http
GET /api/reports/transactions/excel?accountId=1&startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
Authorization: Bearer {token}
```

## ISO8583 Implementation

The system implements ISO8583 standard for ATM/POS transaction processing.

### Message Types
- **0100**: Authorization Request
- **0110**: Authorization Response
- **0200**: Financial Transaction Request
- **0210**: Financial Transaction Response
- **0400**: Reversal Request
- **0410**: Reversal Response
- **0800**: Network Management Request
- **0810**: Network Management Response

### Socket Servers
- **MINA Server**: Port 8583
- **Netty Server**: Port 8584

Both servers handle ISO8583 messages with proper framing and parsing.

## Transaction Flows

### IBFT (Inter-Bank Fund Transfer)
1. User initiates IBFT request
2. System validates account and balance
3. Deducts amount + fee from source account
4. Sends request to 1Link/IBFT network
5. Processes response and updates transaction status
6. Sends notification via Kafka
7. Updates account balance

### Raast (Instant Payment)
1. User initiates Raast payment
2. System validates IBAN and balance
3. Sends request to SBP Raast network
4. Real-time processing (< 5 seconds)
5. Instant credit to beneficiary
6. No fees for individual transactions
7. Kafka event for analytics

### ATM Transaction
1. ATM sends ISO8583 message to socket server
2. Server parses message and extracts fields
3. Validates card, PIN, and balance
4. Processes withdrawal/deposit
5. Updates account balance
6. Generates authorization code
7. Sends ISO8583 response

## Kafka Topics

- `banking.transaction` - General transactions
- `banking.ibft` - IBFT transactions
- `banking.raast` - Raast payments
- `banking.atm` - ATM transactions

## Security

### Encryption
- **Algorithm**: AES-256
- **Encrypted Fields**: CNIC, Card Numbers, CVV, PIN
- **KMS**: Simulated Key Management Service

### Password Policy
- Minimum 8 characters
- BCrypt hashing
- Salt rounds: 12

### JWT Configuration
- Expiration: 24 hours
- Algorithm: HS256
- Refresh token support

## Testing

```bash
# Run backend tests
mvn test

# Run frontend tests
cd frontend
npm test

# Run integration tests
mvn verify
```

## Monitoring

Access application metrics:
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## Production Deployment

### Environment Variables
```bash
export DATABASE_URL=jdbc:postgresql://prod-db:5432/banking_db
export KAFKA_SERVERS=kafka-prod:9092
export JWT_SECRET=your-production-secret
export ENCRYPTION_KEY=your-kms-key
```

### Build Production Artifacts
```bash
# Backend
mvn clean package -Pprod

# Frontend
cd frontend
npm run build
```

## Performance

- **Transaction Processing**: < 200ms average
- **IBFT Transfer**: < 2 seconds
- **Raast Transfer**: < 5 seconds
- **ISO8583 Processing**: < 100ms
- **Report Generation**: < 3 seconds

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## License

This project is licensed under the MIT License.

## Support

For support and queries:
- Email: support@bankingsystem.com
- Documentation: https://docs.bankingsystem.com
- Issues: https://github.com/yourusername/Banking-System/issues

## Acknowledgments

- State Bank of Pakistan (SBP) for Raast specifications
- 1Link for IBFT integration guidelines
- ISO 8583 standards documentation
- Spring Boot community
- React community

---

Built with ❤️ using Java Spring Boot & React
