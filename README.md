# 💰 FinWise LK
### Smart Personal Finance Management | Android Application

<p align="center">
  <strong>Take Control of Your Money. Plan Smarter. Build a Better Financial Future.</strong>
</p>

<p align="center">
  An Android-based personal finance management application designed to help users calculate, compare, track, and plan their financial activities through a collection of practical financial tools.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Backend-PHP-777BB4?style=for-the-badge&logo=php&logoColor=white" />
  <img src="https://img.shields.io/badge/Database-MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white" />
</p>

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Project Objectives](#-project-objectives)
- [Key Features](#-key-features)
- [Financial Modules](#-financial-modules)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [Database Design](#-database-design)
- [API Integration](#-api-integration)
- [Application Screenshots](#-application-screenshots)
- [Installation and Setup](#-installation-and-setup)
- [Security Considerations](#-security-considerations)
- [Future Improvements](#-future-improvements)
- [Project Structure](#-project-structure)
- [Author](#-author)
- [License](#-license)

---

## 🌟 Overview

**FinWise LK** is a personal finance management Android application developed to simplify everyday financial calculations and improve financial planning.

Managing personal finances involves making important decisions about savings, fixed deposits, loans, salary deductions, investments, and financial goals. These calculations can become complicated when users need to compare different financial options.

FinWise LK brings several of these financial tools together in one application.

The application provides an interactive mobile interface where users can perform financial calculations, compare available fixed deposit rates from supported banks, manage savings goals, and store their financial calculation records.

The backend is developed using PHP, with MySQL providing persistent data storage.

### 🎯 Project Vision

To create a practical and accessible financial management solution that helps individuals make informed financial decisions through simple calculations, organized financial records, and goal-based planning.

---

## 🎯 Project Objectives

- Develop an easy-to-use Android personal finance application.
- Simplify common financial calculations.
- Help users compare fixed deposit interest rates.
- Provide loan repayment and EMI calculations.
- Estimate salary deductions and take-home salary.
- Support investment growth calculations.
- Help users plan and monitor financial goals.
- Integrate Android, PHP, and MySQL through REST-style API communication.
- Store user accounts and financial records in a centralized database.

---

## 🚀 Key Features

| Feature | Description |
|---|---|
| 🔐 User Authentication | User registration and login |
| 🏦 Fixed Deposit Calculator | Calculate interest and maturity amounts |
| 📊 FD Comparison | Compare supported bank FD rates |
| 💳 Loan Calculator | Calculate monthly EMI and total repayment |
| 💼 Salary Calculator | Estimate EPF, APIT and net salary |
| 📈 Investment Calculator | Estimate investment growth and returns |
| 🎯 Financial Goal Planner | Set savings targets and track progress |
| 🗄️ Database Integration | Store financial records using MySQL |
| 📱 Mobile Interface | Android application with a dark navy and gold theme |

---

## 💡 Financial Modules

### 1. 🏦 Fixed Deposit Calculator

Calculate the expected interest and maturity value of a fixed deposit.

**Main capabilities:**
- Enter the deposit amount.
- Select the interest rate.
- Choose the deposit period.
- Calculate estimated interest.
- View the maturity amount.
- Save calculation records.

### 2. 📊 Fixed Deposit Comparison

Compare fixed deposit rates from supported banks to identify potentially better deposit options.

**Main capabilities:**
- Retrieve bank rates from the backend.
- Filter rates according to deposit periods.
- Compare available interest rates.
- Calculate estimated returns.
- Display comparison results.
- Save the selected calculation.

### 3. 💳 Loan Calculator

Estimate loan repayment amounts using a reducing-balance EMI calculation.

**Main capabilities:**
- Enter the loan amount.
- Enter the annual interest rate.
- Select the repayment period.
- Calculate monthly EMI.
- Calculate total interest.
- Calculate total repayment amount.
- Save loan calculation records.

### 4. 💼 Salary Calculator

Estimate salary deductions and take-home salary.

**Main capabilities:**
- Enter gross salary.
- Estimate employee EPF contributions.
- Estimate APIT deductions.
- Calculate estimated net salary.
- Display employer EPF and ETF information.
- Save salary calculation records.

*Note: Salary tax calculations are estimates and should be checked against current Sri Lankan tax regulations before financial decisions are made.*

### 5. 📈 Investment Calculator

Estimate the potential growth of an investment over a selected period.

**Main capabilities:**
- Enter initial investment.
- Enter monthly investment contributions.
- Set the expected annual return.
- Select the investment period.
- Calculate total invested amount.
- Estimate potential profit.
- Display estimated final investment value.
- Save investment calculation records.

*Investment projections are estimates, not guaranteed returns.*

### 6. 🎯 Financial Goal Planner

Create savings goals and calculate the amount required to achieve them.

**Main capabilities:**
- Create a financial goal.
- Set the target amount.
- Enter current savings.
- Select the target period.
- Calculate the remaining amount.
- Estimate required monthly savings.
- Display savings progress.
- Store goals in the database.

---

## 🛠️ Technology Stack

### Frontend – Android Application

| Technology | Purpose |
|---|---|
| Java | Application development |
| XML | User interface design |
| Android Studio | Development environment |
| Android SDK | Android application framework |
| SharedPreferences | Local login session persistence |

### Backend – Server-Side Development

| Technology | Purpose |
|---|---|
| PHP | Backend logic and API development |
| MySQLi | Database connectivity |
| JSON | Communication data format |
| HTTP | Android-to-backend communication |

### Database

| Technology | Purpose |
|---|---|
| MySQL | Relational database management |
| SQL | Database creation and queries |

---

## 🏗️ System Architecture

FinWise LK follows a client-server architecture.

The Android application communicates with the PHP backend through HTTP requests. The backend processes requests and interacts with the MySQL database.

```text
             FINWISE LK APPLICATION
                       |
                       |
              Android Frontend
                  Java + XML
                       |
                       |
                HTTP / JSON
                       |
                       |
               PHP REST-STYLE API
                       |
                       |
                 MySQL Database
                       |
           -------------------------
           |           |           |
         Users      Financial    Bank Rates
                     Records
```

### Application Layers

**1. Presentation Layer**

The Android application provides the user interface and accepts financial information.

**2. Application Layer**

Java handles application logic, calculations, user interactions, and API communication.

**3. Backend Layer**

PHP processes API requests, validates input, and performs database operations.

**4. Data Layer**

MySQL stores user information, financial goals, bank rates, and calculation records.

---

## 🗄️ Database Design

The application uses a MySQL relational database named:

`finwise_db`

### Main Database Tables

| Table | Purpose |
|---|---|
| `users` | Stores registered user information |
| `financial_goals` | Stores user savings goals |
| `fd_records` | Stores fixed deposit calculations |
| `loan_records` | Stores loan calculations |
| `salary_records` | Stores salary calculations |
| `investment_records` | Stores investment calculations |
| `bank_rates` | Stores supported bank interest rates |

### Database Relationships

- One user can create multiple financial goals.
- One user can have multiple fixed deposit records.
- One user can have multiple loan records.
- One user can have multiple salary records.
- One user can have multiple investment records.

Financial records are associated with users through `user_id` foreign-key relationships.

---

## 🔌 API Integration

The Android application communicates with the PHP backend using JSON-based HTTP requests.

### Backend API Endpoints

| Endpoint | Method | Function |
|---|---|---|
| `register.php` | POST | Register a new user |
| `login.php` | POST | Authenticate a user |
| `save_goal.php` | POST | Save a financial goal |
| `get_goals.php` | GET | Retrieve financial goals |
| `save_calculation.php` | POST | Save financial calculations |
| `get_bank_rates.php` | GET | Retrieve available bank rates |
| `get_calculation.php` | GET | Retrieve calculation records |

### API Communication Process

1. The user performs an action in the Android application.
2. Java sends an HTTP request to the PHP backend.
3. PHP validates and processes the request.
4. MySQL performs the required database operation.
5. PHP returns a JSON response.
6. Android receives and displays the response.

---

## 📸 Application Screenshots

Screenshots of the application will be added here.

Create a `Screenshots` folder in the repository and add your actual application screenshots.

Suggested screenshots:

| Screen | Screenshot |
|---|---|
| Main Dashboard | `Screenshots/dashboard.png` |
| Login Screen | `Screenshots/login.png` |
| Registration Screen | `Screenshots/register.png` |
| FD Calculator | `Screenshots/fd-calculator.png` |
| FD Comparison | `Screenshots/fd-comparison.png` |
| Loan Calculator | `Screenshots/loan-calculator.png` |
| Salary Calculator | `Screenshots/salary-calculator.png` |
| Investment Calculator | `Screenshots/investment-calculator.png` |
| Goal Planner | `Screenshots/goal-planner.png` |

---

## ⚙️ Installation and Setup

Follow these steps to run the project locally.

### Prerequisites

Install the following software:

- Android Studio
- Java Development Kit (JDK)
- XAMPP
- MySQL Server
- Visual Studio Code
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/gayathpathirana/FinWise-LK.git
```

Navigate into the project:

```bash
cd FinWise-LK
```

### 2. Configure the PHP Backend

Copy the `FinWiseAPI` folder into your XAMPP `htdocs` directory.

Example macOS location:

```text
/Applications/XAMPP/xamppfiles/htdocs/
```

Your backend directory should look like:

```text
FinWiseAPI/
├── api/
├── db.php
└── index.php
```

### 3. Configure MySQL

1. Start Apache in XAMPP.
2. Start your MySQL server.
3. Create a database named `finwise_db`.
4. Import the database SQL file when it is added to the repository.
5. Configure the database connection in `db.php`.

Example configuration:

```php
$host = "127.0.0.1";
$port = 3306;
$username = "root";
$password = "YOUR_DATABASE_PASSWORD";
$database = "finwise_db";
```

Replace the placeholder with your own local database password.

### 4. Test the Backend

Open the following URL in your browser:

```text
http://localhost/FinWiseAPI/api/test.php
```

For Android Emulator testing, the backend address is:

```text
http://10.0.2.2/FinWiseAPI/api/
```

**Important:** `10.0.2.2` is an Android Emulator address for reaching the host computer. It is not a public server address.

### 5. Open the Android Application

1. Open Android Studio.
2. Select **Open**.
3. Choose the Android application project directory.
4. Allow Gradle synchronization to complete.
5. Check the API base URL in `ApiConfig.java`.
6. Start an Android Emulator.
7. Run the application.

---

## 🔐 Security Considerations

Security is an important part of financial application development.

The project includes or uses the following security-related practices:

- Password hashing using PHP `password_hash()`.
- Password verification using `password_verify()`.
- Prepared SQL statements for database operations.
- Input validation in backend API endpoints.
- Local login state persistence using SharedPreferences.
- Exclusion of local secrets and build files through `.gitignore`.

### Important Production Considerations

Before deploying this application for real users:

- Use HTTPS for all API communication.
- Never expose database credentials in public repositories.
- Use a dedicated database account with restricted permissions instead of the MySQL root account.
- Implement server-side authentication using secure tokens or sessions.
- Verify user authorization on every private API request.
- Protect sensitive financial information.
- Validate all financial calculations against authoritative sources.

---

## 🚀 Future Improvements

FinWise LK can be expanded with additional financial management capabilities.

| Planned Feature | Description |
|---|---|
| 📊 Financial Dashboard | Visualize income, expenses and savings |
| 💸 Expense Tracker | Record and categorize daily expenses |
| 📅 Monthly Budget Planner | Set and monitor monthly budgets |
| 🔔 Smart Notifications | Reminders for savings goals and payments |
| 📈 Financial Analytics | Visual reports and financial insights |
| 🏦 More Bank Integrations | Expand supported bank rate information |
| ☁️ Cloud Deployment | Make backend services accessible online |
| 👤 Profile Management | Allow users to update personal details |
| 📄 Financial Reports | Generate downloadable financial summaries |

---

## 📁 Project Structure

The repository is organized into the following main components:

```text
FinWise-LK/
│
├── FinWiseAPI/
│   ├── api/
│   │   ├── register.php
│   │   ├── login.php
│   │   ├── save_goal.php
│   │   ├── get_goals.php
│   │   ├── save_calculation.php
│   │   ├── get_bank_rates.php
│   │   └── get_calculation.php
│   │
│   ├── db.php
│   └── index.php
│
├── Android-App/
│   └── Android Studio project files
│
├── Database/
│   └── finwise_db.sql
│
├── Screenshots/
│   └── Application screenshots
│
├── .gitignore
└── README.md
```

*The Android-App, Database, and Screenshots directories should be added as those project components are uploaded.*

---

## 👨‍💻 Author

**Gayath Pathirana**

Computer Science and Technology Undergraduate

Uva Wellassa University of Sri Lanka

### Connect With Me

- GitHub: [@gayathpathirana](https://github.com/gayathpathirana)
- LinkedIn: [My LinkedIn Profile](https://www.linkedin.com/)

---

## 📄 License

This project is developed as an educational and portfolio project.

A formal open-source license has not yet been selected. All rights remain with the project author unless otherwise stated.

---

<p align="center">
  <strong>FINWISE LK</strong>
  <br />
  <em>Think Smart. Save Better. Grow Financially.</em>
  <br /><br />
  Built with dedication using Java, PHP, XML and MySQL.
</p>