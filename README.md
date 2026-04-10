# Nepal Accountant Backend

Backend-only Spring Boot service for a smart accountant product that turns invoice photos into structured accounting data and draft Nepal tax returns.

## Current Status

This repository is now in the setup phase. The codebase includes:

- a renamed Spring Boot application with a real package structure
- placeholder configuration for Supabase and an AI provider
- starter REST endpoints for invoice analysis and tax return generation
- validation and centralized API error handling
- a roadmap below so the repo can grow in a planned way

What is not implemented yet:

- real Supabase authentication and persistence
- image upload to Supabase Storage
- OCR / multimodal invoice extraction
- Nepal tax logic and return filing workflows

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Supabase for database, auth, and file storage
- AI provider for OCR / extraction / reasoning

## Planned Architecture

1. Client uploads an invoice image.
2. Backend stores the image in Supabase Storage.
3. Backend sends the image to an AI/OCR pipeline for field extraction.
4. Extracted invoice data is normalized and saved in Supabase Postgres.
5. Tax rules map invoices into deductible / taxable categories.
6. Backend generates draft tax returns and exposes review endpoints.

## Project Plan

### Phase 1: Foundation

1. Finalize project naming, package structure, and environment config.
2. Add core modules for invoice ingestion, extraction, and tax workflows.
3. Initialize git, create GitHub repository, and publish the first commit.

### Phase 2: Supabase Integration

1. Add Supabase client integration for Postgres, Auth, and Storage.
2. Model users, invoices, extracted fields, and tax return drafts.
3. Introduce signed upload/download workflows for invoice images.

### Phase 3: AI Invoice Extraction

1. Add AI provider abstraction for OCR and structured extraction.
2. Convert invoice photos into normalized fields like vendor, PAN/VAT, dates, subtotal, tax, and total.
3. Add confidence scores and manual review flags for uncertain extractions.

### Phase 4: Tax Logic

1. Define Nepal-specific tax concepts and filing periods.
2. Map invoice categories to expense and tax treatment rules.
3. Generate draft return summaries with warnings and validation steps.

### Phase 5: Production Readiness

1. Add authentication and authorization.
2. Add integration tests and contract tests.
3. Add observability, CI, and deployment configuration.

## API Shape For The MVP

- `GET /api/v1/system/health`
- `POST /api/v1/invoices/analyze`
- `POST /api/v1/tax-returns/generate`

These are starter endpoints right now. They exist so we can grow the backend around stable routes instead of starting from a demo app later.

## Configuration

The application reads these environment variables:

- `PORT`
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `SUPABASE_INVOICE_BUCKET`
- `AI_PROVIDER`
- `AI_MODEL`
- `AI_API_KEY`
- `AI_BASE_URL`

## Local Run

```bash
./mvnw spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/v1/system/health
```

## Suggested Next Build Steps

1. Create the Supabase schema and storage bucket.
2. Replace the placeholder invoice service with real storage + AI extraction.
3. Add database persistence for invoices and extracted fields.
4. Add Nepal tax rule models and draft generation logic.
5. Add authentication before exposing user-specific endpoints.
