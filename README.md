# 💰 ExpenseTracker — Android App

A full-featured wallet-style expense tracker built with **Kotlin**, **Room DB**, **MVVM**, **WorkManager**, and **MPAndroidChart**.

---

## ✅ Features

| Feature | Details |
|---|---|
| 💵 Income & Expense Tracking | Add income or expense with category, amount, date, note |
| 📊 Dashboard | Net Balance = Income − Expenses, pie chart breakdown |
| 🔔 Budget Notifications | Alerts at 50% and 75% of monthly budget used |
| 📁 Export to CSV | Share full transaction history as a `.csv` file |
| 🔁 Recurring Expenses | Auto-add monthly bills (rent, subscriptions) via WorkManager |
| 🗂 Categories | Food, Transport, Shopping, Bills, Entertainment, Health, Education, Other |
| 📂 Budget Manager | Set overall + per-category monthly budgets |
| 🔍 Filter Transactions | Filter by All / Income / Expense |

---

## 🏗 Project Structure

```
app/src/main/java/com/expensetracker/
│
├── data/
│   ├── model/
│   │   ├── Transaction.kt         ← Entity + TransactionType + Category enums
│   │   └── Budget.kt              ← Budget entity
│   ├── dao/
│   │   ├── TransactionDao.kt      ← All DB queries
│   │   └── BudgetDao.kt
│   ├── db/
│   │   └── AppDatabase.kt         ← Room database singleton
│   └── repository/
│       ├── TransactionRepository.kt
│       └── BudgetRepository.kt
│
├── ui/
│   ├── MainActivity.kt            ← Bottom nav host
│   ├── dashboard/
│   │   ├── DashboardFragment.kt   ← Home screen with stats + chart
│   │   └── DashboardViewModel.kt
│   ├── transactions/
│   │   ├── TransactionsFragment.kt     ← Full list with filter + export
│   │   ├── TransactionsViewModel.kt
│   │   ├── AddTransactionActivity.kt   ← Add/edit transaction form
│   │   ├── AddTransactionViewModel.kt
│   │   └── TransactionAdapter.kt       ← RecyclerView adapter
│   └── budget/
│       ├── BudgetActivity.kt      ← Set monthly budgets
│       ├── BudgetViewModel.kt
│       └── BudgetAdapter.kt       ← Per-category budget rows
│
├── worker/
│   └── RecurringExpenseWorker.kt  ← WorkManager: runs daily, adds recurring expenses
│
├── service/
│   └── BootReceiver.kt            ← Reschedules worker after device reboot
│
└── util/
    ├── NotificationHelper.kt      ← Create channels + send budget/recurring alerts
    ├── CsvExporter.kt             ← Export all transactions to CSV file
    └── DateUtils.kt               ← Format dates, get current month/year
```

---

## ⚙️ Setup Instructions

### 1. Clone / Create Project

In Android Studio:
- **File → New → New Project → Empty Views Activity**
- Package: `com.expensetracker`
- Min SDK: **26 (Android 8.0)**
- Language: **Kotlin**

### 2. Copy Files

Copy all files from this project into your Android Studio project, maintaining the same folder structure.

### 3. Replace `build.gradle` files

Replace your project-level and app-level `build.gradle` with the provided ones.

> ⚠️ Make sure your `settings.gradle` includes JitPack (needed for MPAndroidChart):
```groovy
repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

### 4. Sync Gradle

Click **"Sync Now"** in Android Studio after pasting the `build.gradle` files.

### 5. Add Missing Launcher Icons

Android Studio auto-generates `ic_launcher` / `ic_launcher_round`. If you get a build error, right-click `res → New → Image Asset` to regenerate them.

---

## 🔔 How Notifications Work

Notifications are sent when monthly spending crosses **50%** or **75%** of the set overall budget.

- Uses `SharedPreferences` to avoid duplicate alerts in the same month
- On Android 13+, asks for `POST_NOTIFICATIONS` permission at launch
- Budget alert is re-evaluated every time a new expense is added

---

## 🔁 How Recurring Expenses Work

1. When adding a transaction, toggle **"Recurring Monthly"** ON
2. Enter the **day of month** (1–28) it should repeat
3. `RecurringExpenseWorker` runs **daily at midnight** via WorkManager
4. On matching day, a new transaction instance is inserted automatically
5. A notification is sent each time a recurring expense is added
6. Works even after device reboot (via `BootReceiver`)

---

## 📁 How CSV Export Works

1. Go to **Transactions tab → overflow menu (⋮) → Export CSV**
2. All transactions (all time) are written to a `.csv` file in external storage
3. A **share sheet** opens so you can send via email, Drive, WhatsApp, etc.

CSV format:
```
Date, Title, Category, Type, Amount, Note
22-03-2026 10:30, Rent, BILLS, Expense, 12000.00, March rent
```

---

## 📊 Dashboard Explained

| Widget | Description |
|---|---|
| **Net Balance** | `Total Income − Total Expenses` for the current month |
| **Income / Expense boxes** | Quick summary cards inside the balance header |
| **Budget Progress Bar** | Visual fill showing % of monthly budget used |
| **Pie Chart** | Spending split by category (MPAndroidChart) |
| **Recent Transactions** | Last 5 transactions with See All link |

---

## 🗃 Database Schema

### `transactions` table
| Column | Type | Notes |
|---|---|---|
| id | Long (PK, autoIncrement) | |
| title | String | |
| amount | Double | |
| category | String | Enum name (e.g. "FOOD") |
| type | String | "INCOME" or "EXPENSE" |
| date | Long | Unix timestamp (ms) |
| note | String | Optional |
| isRecurring | Boolean | Template flag |
| recurringDay | Int | Day of month (1–28) |

### `budgets` table
| Column | Type | Notes |
|---|---|---|
| id | Long (PK, autoIncrement) | |
| category | String | "OVERALL" or category name |
| monthlyLimit | Double | |
| month | Int | 1–12 |
| year | Int | e.g. 2026 |

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM + Repository pattern |
| Database | Room (SQLite) |
| Async | Coroutines + LiveData |
| Background work | WorkManager |
| Charts | MPAndroidChart |
| Navigation | Jetpack Navigation Component |
| UI | Material Components 3 |
| Notifications | NotificationCompat |
| File sharing | FileProvider |

---

## Working App Module

<img width="1080" height="1350" alt="Screenshot 2026-03-23 202152" src="https://github.com/user-attachments/assets/8ce71234-f711-4f41-b9b7-5449d8de10f3" />
<img width="1080" height="1350" alt="Screenshot 2026-03-23 202152" src="https://github.com/user-attachments/assets/bd878062-121d-49a8-a46b-bc0079905e59" />
<img width="1080" height="1350" alt="Screenshot 2026-03-23 202152" src="https://github.com/user-attachments/assets/804c648d-c561-4a73-8f2f-60ea722746cf" />
<img width="1080" height="1350" alt="Screenshot 2026-03-23 202152" src="https://github.com/user-attachments/assets/918611b0-4b56-4ad4-bcdd-99c0eba6366c" />
<img width="1080" height="1350" alt="Screenshot 2026-03-23 202152" src="https://github.com/user-attachments/assets/38cd80b7-434d-425e-bbee-25b8951807c3" />
