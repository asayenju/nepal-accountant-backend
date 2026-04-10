# Arthik — Digital Lending for Nepali SMEs

> Smart financial tools for small businesses. Track revenue, manage invoices, and access working capital — all in one place.

---

## The Problem

Nepal has over 500,000 small and medium businesses. Most of them:

- Record revenue and expenses in physical notebooks
- Get rejected by banks because they have no formal financial history
- Have no visibility into their own profit and cash flow
- Cannot access working capital when they need it most

Banks require land collateral. SMEs do not have it. The gap between what SMEs need and what traditional finance offers them is enormous.

## The Solution

Arthik gives SMEs a simple tool to run their finances digitally. As they use it, they build a verifiable financial history. That history becomes the basis for credit — no collateral required.

```
Business uses Arthik daily
         ↓
Invoices, expenses, and payments are recorded
         ↓
Revenue patterns and profit margins become visible
         ↓
Arthik generates a lending eligibility score
         ↓
SME accesses working capital in days, not months
```

---

## Core Features

### Revenue and Profit Tracking
- Real-time dashboard showing monthly revenue, expenses, and net profit
- Unpaid invoice tracking — know exactly who owes you money
- Cash flow forecasting based on historical patterns
- Client payment history to identify reliable and slow payers

### Invoice Management
- Create and send professional digital invoices directly to clients
- AI-powered invoice extraction — photograph a paper invoice and let the app read it
- Automatic VAT calculation at 13% per Nepal IRD requirements
- Track invoice status: draft, sent, paid, overdue

### Expense Recording
- Log business expenses by category
- Photograph receipts for AI extraction
- Full profit and loss view at any time

### Lending Eligibility
- Continuous credit score built from real business data
- Loan offers based on actual revenue, not collateral
- One-tap application flow for pre-qualified businesses
- Partner microfinance institutions handle disbursement

### Tax Compliance (lightweight)
- Auto-generated VAT summary from invoices each month
- IRD-compliant invoice formats with PAN and VAT numbers
- CBMS integration for businesses that cross the NPR 10 crore threshold

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot |
| Database | Supabase Postgres |
| Auth | Supabase Auth + JWT |
| File Storage | Supabase Storage |
| AI Extraction | Multimodal LLM via provider abstraction |
| Build | Maven |

---

## Architecture

```
Mobile / Web Client
        ↓
Spring Boot API (auth, business logic, lending score)
        ↓
Supabase (Postgres + Auth + Storage)
        ↓
AI Pipeline (invoice photo → structured data)
        ↓
IRD CBMS (real-time sync for eligible businesses)
```

---

## API Overview

### Auth
```
POST   /api/v1/auth/signup
POST   /api/v1/auth/login
```

### Businesses
```
POST   /api/v1/businesses
GET    /api/v1/businesses
GET    /api/v1/businesses/{businessId}
PUT    /api/v1/businesses/{businessId}
```

### Clients
```
POST   /api/v1/businesses/{businessId}/clients
GET    /api/v1/businesses/{businessId}/clients
GET    /api/v1/businesses/{businessId}/clients/{clientId}
```

### Invoices
```
POST   /api/v1/businesses/{businessId}/invoices
GET    /api/v1/businesses/{businessId}/invoices
GET    /api/v1/businesses/{businessId}/invoices/{invoiceId}
PUT    /api/v1/businesses/{businessId}/invoices/{invoiceId}
POST   /api/v1/invoices/analyze          ← AI extraction from photo
```

### Expenses
```
POST   /api/v1/businesses/{businessId}/expenses
GET    /api/v1/businesses/{businessId}/expenses
```

### Dashboard
```
GET    /api/v1/businesses/{businessId}/dashboard
GET    /api/v1/businesses/{businessId}/cashflow
```

### Lending
```
GET    /api/v1/businesses/{businessId}/lending/score
POST   /api/v1/businesses/{businessId}/lending/apply
```

### Tax
```
GET    /api/v1/businesses/{businessId}/tax/vat-summary
POST   /api/v1/businesses/{businessId}/tax/returns
```

### System
```
GET    /api/v1/system/health
```

---

## Data Model

```
users
  └── businesses
        ├── clients
        ├── invoices
        │     └── invoice_line_items
        ├── expenses
        ├── payments (linked to invoices)
        └── tax_returns
```

All tables use Row Level Security (RLS). Users can only access data belonging to their own businesses.

---

## Environment Variables

```env
PORT=8080
SUPABASE_URL=
SUPABASE_ANON_KEY=
SUPABASE_SERVICE_ROLE_KEY=
SUPABASE_INVOICE_BUCKET=
AI_PROVIDER=
AI_MODEL=
AI_API_KEY=
AI_BASE_URL=
```

---

## Local Setup

```bash
# Clone the repository
git clone https://github.com/your-org/arthik-backend

# Copy environment config
cp .env.example .env

# Fill in your Supabase and AI provider credentials in .env

# Run the application
./mvnw spring-boot:run

# Health check
curl http://localhost:8080/api/v1/system/health
```

---

## Build Roadmap

### Phase 1 — Foundation ✅
- Project structure, package layout, environment config
- Centralized error handling and validation
- Core REST endpoint scaffolding

### Phase 2 — Auth and Database
- Supabase Auth integration with JWT protected routes
- Full schema: users, businesses, clients, invoices, expenses, payments
- Row Level Security policies on all tables

### Phase 3 — Core Business Features
- Business and client management
- Invoice creation, sending, and payment tracking
- Expense recording
- Revenue and profit dashboard

### Phase 4 — AI Extraction
- Invoice photo upload to Supabase Storage
- Multimodal AI pipeline for field extraction
- Confidence scoring and manual review flags for uncertain extractions

### Phase 5 — Lending
- Credit score engine based on revenue, payment history, and profit margins
- Loan eligibility API
- Partner microfinance institution integration

### Phase 6 — Tax and Compliance
- VAT summary generation from invoice data
- IRD-compliant invoice formats
- CBMS integration for businesses above NPR 10 crore threshold

### Phase 7 — Production
- Integration and contract tests
- CI/CD pipeline
- Observability and alerting
- Mobile-first frontend

---

## Market Context

- Nepal SME market: 500,000+ businesses
- Annual remittance inflow: NPR 1.2 trillion (financial behaviour data opportunity)
- IRD e-billing mandate expanding annually — compliance pressure grows every year
- Traditional bank SME loan rejection rate: 80%+
- Comparable companies in India: Khatabook ($600M valuation), OkCredit ($80M raised)

---

## License

Private and confidential. All rights reserved.