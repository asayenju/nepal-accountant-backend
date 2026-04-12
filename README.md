# Arthik — SME Financial Access & Lender-Readiness Platform

> Digital bookkeeping and credit scoring for Nepali small businesses. Build verifiable financial history to access working capital without collateral.

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

### Digital Bookkeeping
- Real-time dashboard showing monthly revenue, expenses, and net profit
- Client management with payment history tracking
- Invoice creation, sending, and payment status monitoring
- Expense recording by category with receipt management
- Cash flow forecasting based on historical patterns

### Lender-Readiness Scoring
- Transparent credit score built from real business data
- 8 core lender metrics: revenue stability, profit margins, payment collection speed, customer concentration, expense ratios, cash flow volatility, data completeness, and business verification status
- Loan offers based on actual revenue, not collateral
- One-tap application flow for pre-qualified businesses

### Cash Flow Visibility
- Unpaid invoice tracking — know exactly who owes you money
- Monthly cash flow projections
- Payment overdue alerts and aging reports
- Receivables management dashboard

### Tax Compliance (lightweight)
- Auto-generated VAT summary from invoices each month
- IRD-compliant invoice formats with PAN and VAT numbers
- Exportable transaction records for tax filing
- Optional compliance reports for CBMS integration

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
Spring Boot API (auth, business logic, lender scoring)
        ↓
Supabase (Postgres + Auth + Storage)
        ↓
AI Pipeline (invoice photo → structured data)
        ↓
Partner Lenders (score-based loan offers)
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
DELETE /api/v1/businesses/{businessId}
```

### Clients
```
POST   /api/v1/businesses/{businessId}/clients
GET    /api/v1/businesses/{businessId}/clients
GET    /api/v1/businesses/{businessId}/clients/{clientId}
PUT    /api/v1/businesses/{businessId}/clients/{clientId}
DELETE /api/v1/businesses/{businessId}/clients/{clientId}
```

### Invoices
```
POST   /api/v1/businesses/{businessId}/invoices
GET    /api/v1/businesses/{businessId}/invoices
GET    /api/v1/businesses/{businessId}/invoices/{invoiceId}
PUT    /api/v1/businesses/{businessId}/invoices/{invoiceId}
DELETE /api/v1/businesses/{businessId}/invoices/{invoiceId}
POST   /api/v1/invoices/analyze          ← AI extraction from photo
```

### Expenses
```
POST   /api/v1/businesses/{businessId}/expenses
GET    /api/v1/businesses/{businessId}/expenses
GET    /api/v1/businesses/{businessId}/expenses/{expenseId}
PUT    /api/v1/businesses/{businessId}/expenses/{expenseId}
DELETE /api/v1/businesses/{businessId}/expenses/{expenseId}
```

### Payments
```
POST   /api/v1/businesses/{businessId}/payments
GET    /api/v1/businesses/{businessId}/payments
GET    /api/v1/businesses/{businessId}/payments/{paymentId}
PUT    /api/v1/businesses/{businessId}/payments/{paymentId}
DELETE /api/v1/businesses/{businessId}/payments/{paymentId}
```

### Dashboard & Analytics
```
GET    /api/v1/businesses/{businessId}/dashboard
GET    /api/v1/businesses/{businessId}/cashflow
GET    /api/v1/businesses/{businessId}/analytics/revenue
GET    /api/v1/businesses/{businessId}/analytics/expenses
GET    /api/v1/businesses/{businessId}/analytics/profit
GET    /api/v1/businesses/{businessId}/analytics/receivables
GET    /api/v1/businesses/{businessId}/analytics/overdue
```

### Lender-Readiness
```
GET    /api/v1/businesses/{businessId}/financial-summary    ← Bank-friendly summary
GET    /api/v1/businesses/{businessId}/lending/score        ← Credit score + reasons
GET    /api/v1/businesses/{businessId}/lending/metrics      ← 8 core lender metrics
POST   /api/v1/businesses/{businessId}/lending/apply        ← Loan application
```

### Tax (Lightweight)
```
GET    /api/v1/businesses/{businessId}/tax/vat-summary
GET    /api/v1/businesses/{businessId}/tax/transactions     ← Exportable records
POST   /api/v1/businesses/{businessId}/tax/returns          ← Optional compliance reports
```

### System
```
GET    /api/v1/system/health
```

---

## Core Lender Metrics

Before building more endpoints, we define these 8 metrics that banks actually care about:

1. **Revenue Stability** - Month-over-month revenue volatility (lower is better)
2. **Profit Margin** - Average profit as percentage of revenue
3. **Payment Collection Speed** - Average days to collect invoice payments
4. **Customer Concentration** - Percentage of revenue from top 3 clients (lower risk if <30%)
5. **Expense Ratio** - Operating expenses as percentage of revenue
6. **Cash Flow Volatility** - Month-over-month cash flow variation
7. **Data Completeness** - Percentage of transactions with proper categorization
8. **Business Verification** - Document verification status (PAN, VAT, business registration)

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
        └── tax_returns (optional)
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

## Recommended Project Plan

### Phase 1: Reframe the product ✅
- Update naming and documentation so the backend is clearly "SME financial access and lender-readiness," with tax as a secondary feature
- Rewrite the roadmap around bookkeeping, cash flow visibility, and credit scoring
- Define 8 core lender metrics before writing more endpoints

### Phase 2: Build core financial records
- Add persisted tables and CRUD APIs for clients, invoices, expenses, payments
- Add statuses and timestamps that support analysis, not just storage
- Keep everything scoped by business_id and protected by Supabase RLS

### Phase 3: Build analytics
- Add dashboard endpoints for revenue, expenses, profit, receivables, overdue invoices, and monthly cash flow
- Add a "financial summary" endpoint that banks can understand quickly
- Add a "data completeness" score so lenders know how trustworthy the numbers are

### Phase 4: Build lender-readiness / credit scoring
- Start with a transparent rule-based score, not a black-box ML model
- Return score plus reasons, for example: strong invoice repayment history, low revenue volatility, high customer concentration
- Add document and business verification flags

### Phase 5: Add AI ingestion where it truly helps
- Invoice OCR
- Receipt OCR
- Categorization suggestions
- Manual review workflow when confidence is low

### Phase 6: Keep tax lightweight
- VAT summaries
- Exportable transaction records
- Optional compliance reports
- Tax should reuse the same bookkeeping data, not create a separate product track

---

## Concrete Next Steps For This Repo

If I were prioritizing the next 2 weeks, I'd do this:

1. **Update README and pom.xml language** to match the new direction
2. **Design Supabase tables** for clients, invoices, payments, expenses
3. **Implement CRUD** for those four areas
4. **Add one real dashboard endpoint**: monthly revenue, expenses, profit, unpaid invoices
5. **Add one lender-readiness endpoint**: GET /businesses/{id}/financial-summary
6. **Add tests** for auth protection, business ownership, and financial calculations
7. **Leave tax return generation** as a later derived feature

---

## Biggest Gaps Right Now

- Many APIs in the README do not exist yet: README.md (line 113)
- Invoice and tax services are stubs, not real workflows: InvoiceProcessingService.java (line 14), TaxReturnService.java (line 13)
- No domain model yet for lender analysis data
- No scoring logic yet
- Almost no tests
- Product description in code still reflects the older tax-heavy positioning: pom.xml (line 15)

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