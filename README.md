# ExpenseTracker

ExpenseTracker is a personal finance companion app built with Kotlin, Jetpack Compose, Room, and Hilt. It is designed as a lightweight mobile product that helps users track daily transactions, understand spending habits, monitor a savings goal, and view simple financial insights in a clean mobile-first interface.

## Overview

The app focuses on everyday money awareness rather than banking workflows. Users can:

- View a home dashboard with balance, income, expenses, goal progress, charts, and recent activity
- Add, edit, and delete transactions
- Browse transaction history with mobile-friendly interactions
- Track a savings goal
- Explore insights such as weekly spending, expense breakdown, cash-flow split, and pie-chart category distribution

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

## Project Structure

Key files:

- [`MainActivity.kt`](L:/Android%20Projects/ExpenseTracker/app/src/main/java/com/cosmic_struck/expensetracker/MainActivity.kt)
- [`MainNavGraph.kt`](L:/Android%20Projects/ExpenseTracker/app/src/main/java/com/cosmic_struck/expensetracker/ui/navigation/MainNavGraph.kt)
- [`FinanceDashboardViewModel.kt`](L:/Android%20Projects/ExpenseTracker/app/src/main/java/com/cosmic_struck/expensetracker/ui/viewmodel/FinanceDashboardViewModel.kt)
- [`ExpenseTrackerDatabase.kt`](L:/Android%20Projects/ExpenseTracker/app/src/main/java/com/cosmic_struck/expensetracker/data/local/database/ExpenseTrackerDatabase.kt)
- [`OfflineFirstFinanceRepository.kt`](L:/Android%20Projects/ExpenseTracker/app/src/main/java/com/cosmic_struck/expensetracker/data/repository/OfflineFirstFinanceRepository.kt)

## How to Run

### Requirements

- Android Studio
- Android SDK matching the project configuration
- JDK 11+

### Steps

1. Open the project in Android Studio.
2. Let Gradle sync complete.
3. Run the app on an emulator or Android device.

From the command line:

```powershell
.\gradlew.bat assembleDebug
```

## Data Handling

- Transactions and goals are stored locally using Room.
- The app is offline-first and does not require a backend to function.
- UI state reacts automatically to data changes via Flow and ViewModel state collection.

## UX Notes

- The UI is optimized for mobile usage and touch interaction.
- Empty states are included for transactions and goals.
- Transaction deletion uses swipe interaction for a more native mobile feel.
- Editing is designed to be fast by tapping an existing transaction row.

## Known Gaps / Future Improvements

- Transaction search and filters can be added
- Better loading and error states can be introduced
- Dark mode can be added
- Export, reminders, and notifications can be added later
- Swipe-to-delete currently uses a deprecated Compose callback API and can be modernized in a future pass

## Assumptions

- The app uses local storage only
- Savings goal progress is based on current balance
- Only one primary savings goal is actively managed in the current experience

## Submission Notes

This project was built as a practical personal finance companion app with emphasis on:

- clear mobile UX
- offline-first data handling
- structured architecture
- reusable Compose components
- thoughtful, product-oriented feature design
