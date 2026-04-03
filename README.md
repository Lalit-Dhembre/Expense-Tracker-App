# ExpenseTracker

ExpenseTracker is a personal finance companion app built with Kotlin, Jetpack Compose, Room, and Hilt. It is designed as a lightweight mobile product that helps users track daily transactions, understand spending habits, monitor a savings goal, and view simple financial insights in a clean mobile-first interface.

## Overview

The app focuses on everyday money awareness rather than banking workflows. Users can:

- View a home dashboard with balance, income, expenses, goal progress, charts, and recent activity
- Add, edit, and delete transactions
- Browse transaction history with mobile-friendly interactions
- Track a savings goal
- Explore insights such as weekly spending, expense breakdown, cash-flow split, and pie-chart category distribution


## Screenshots
<img width="300" alt="Screenshot_20260403_193039" src="https://github.com/user-attachments/assets/b9fbe3d5-a355-4dd8-b8ef-5a53f03d9565" />
<img width="300" alt="Screenshot_20260403_193029" src="https://github.com/user-attachments/assets/d55c5ff5-d673-4291-b9c1-238fea2cbfe1" />
<img width="300" alt="Screenshot_20260403_193020" src="https://github.com/user-attachments/assets/df5f47f5-ec44-493d-9627-a8d48e106fa8" />
<img width="300" alt="Screenshot_20260403_193006" src="https://github.com/user-attachments/assets/71ee3cb8-fa12-48d7-94eb-7bab932360e1" />
<img width="300" alt="Screenshot_20260403_192946" src="https://github.com/user-attachments/assets/10b692e4-d9a3-4d3a-ae36-9f78a2024840" />


https://github.com/user-attachments/assets/1713c278-38e4-4b7e-82ea-ce598e571f2a






## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- Hilt for dependency injection
- Navigation Compose
- Kotlin Flow and StateFlow

## Current Features

### Home Dashboard

- Total balance overview
- Income and expense summary cards
- Weekly spending chart
- Savings goal progress card
- Recent transactions preview
- Floating action button to add a transaction

### Transactions

- Create new transactions
- View transaction history
- Tap a transaction to edit it
- Swipe to delete with confirmation
- Supports:
  - amount
  - type
  - category
  - date
  - optional note

### Savings Goal

- Create a savings goal
- Update goal title, target amount, and deadline
- Delete goal
- Goal progress is calculated against current balance

### Insights

- Weekly spending chart
- Income vs expense comparison
- Category breakdown bars
- Expense share pie chart
- Smart summary insight

## Architecture

The project follows a layered structure:

- `ui/`
  - Compose screens and reusable UI components
- `domain/`
  - Models and repository contracts
- `data/`
  - Room entities, DAOs, database, and repository implementation
- `di/`
  - Hilt modules

State is exposed from `FinanceDashboardViewModel` using `StateFlow`, while persistence is handled locally through Room for offline-first behavior.




This project was built as a practical personal finance companion app with emphasis on:

- clear mobile UX
- offline-first data handling
- structured architecture
- reusable Compose components
- thoughtful, product-oriented feature design
