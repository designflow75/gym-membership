# SMART GYM MANAGEMENT SYSTEM 2.0
> **A Comprehensive Java Swing Desktop Application & OOP Reference for College Viva**

---

## 📌 Project Overview
**Smart Gym Management System 2.0** is an interactive, role-based desktop application built using **Java (AWT / Swing GUI)**, **Object-Oriented Programming (OOP) principles**, and **MySQL with JDBC** (with automatic in-memory fallback cache).

### 🌟 Role-Based Portals:
1. **🔐 Authentication Portal (Login & Create Account)**:
   - **Sign In**: Role-aware authentication for Admin, Trainer, and Member.
   - **Create Account**: Register new Members (with weight & fitness goals), Trainers, or Admins.
   - **1-Click Quick Demo Access**: Quick login buttons for Admin (`admin`), Trainer (`trainer1`), and Member (`rahul`) for effortless examination & viva evaluation.

2. **🏋️ Staff / Trainer Panel**:
   - **Mark Attendance**: Log member check-in date, time, status (Present, Late, Absent), workout focus, and notes.
   - **Create / Edit Diet Plans**: Prescribe personalized nutrition plans with macro targets (Calories, Protein, Carbs, Fats), water intake, and meal breakdowns (Breakfast, Lunch, Snacks, Dinner).
   - **Create / Edit Workout Routines**: Prescribe 7-day training splits (Monday through Sunday), cardio guidelines, and progressive overload advice.
   - **Member Directory & Weight Logger**: Log new measured weights for members directly on the scale.

3. **🏃 User / Member Panel**:
   - **Weight Loss & Goal Progress**: Input/update Current Weight (kg), Target Weight (kg), and Height (cm). Features a live **custom-painted graphical progress bar, timeline, and color-coded BMI scale indicator**!
   - **Attendance History & Visual Column Chart**: Features a **custom-painted column/bar chart** with circular percentage attendance gauge.
   - **Assigned Diet Plan**: View trainer-prescribed nutrition goals, plus an interactive daily journal where the member can record food & water consumed.
   - **Assigned Workout Routine**: View weekly exercise split and log personal workout records & PRs.

4. **👑 Admin Panel**:
   - **User Accounts Management**: Full CRUD on system users with search, role filters, and delete capabilities.
   - **Gym Analytics & Operations**: Real-time KPI cards for Total Members, Active Coaches, Today's Check-ins, and DB Connection status.
   - **Export Reports**: Generate `.txt` files for member rosters and attendance logs.

---

## 🎓 Mapping of Required OOP Concepts (For College Viva)

| OOP Concept | Where it is Implemented in Code | Explanation / Viva Answer |
|---|---|---|
| **Classes & Objects** | All files in `com.smartgym.*` | Real-world entities (`AdminUser`, `TrainerUser`, `MemberUser`, `Attendance`, `DietPlan`, `WorkoutPlan`) instantiated as objects. |
| **Encapsulation** | `User.java`, `MemberUser.java`, `DietPlan.java`, etc. | All critical fields (`password`, `currentWeight`, `targetCalories`) are `protected`/`private` with `public` getters, setters, and boundary validation. |
| **Inheritance** | `User` hierarchy and `FitnessPlan` hierarchy | `AdminUser`, `TrainerUser`, and `MemberUser` extend `User`. `DietPlan` and `WorkoutPlan` extend `FitnessPlan`. |
| **Abstract Class** | `User.java`, `FitnessPlan.java` | Base classes declared `abstract` that cannot be instantiated directly; define abstract contracts like `getDashboardTitle()`, `getRoleDescription()`, and `getPlanType()`. |
| **Method Overriding** | In `AdminUser`, `TrainerUser`, `MemberUser`, `DietPlan`, `WorkoutPlan` | Subclasses override `@Override public String getDashboardTitle()` and `@Override public boolean hasAccessTo(String feature)` with role-specific logic. |
| **Runtime Polymorphism** | `GymManagementFrame.java`, `UserService.java` | A superclass reference `User u` holds an `AdminUser`, `TrainerUser`, or `MemberUser` instance at runtime and dynamically calls the overridden methods. |
| **Interface** | `UserOperations.java`, `AttendanceOperations.java`, `FitnessOperations.java` | Pure contracts defining CRUD operations implemented by `UserDAO`, `AttendanceDAO`, and `FitnessDAO`. |
| **Method Overloading** | `UserOperations.java`, `UserService.java` | `searchUsers(int id)` vs `searchUsers(String query)` and overloaded constructor methods in `MemberUser` and `TrainerUser`. |
| **Exception Handling** | `GymException.java`, `AuthenticationException.java`, `try-catch-finally` | Custom checked exceptions extending `java.lang.Exception` for validation and authentication error handling. |
| **Collections Framework** | `UserDAO`, `AttendanceDAO`, `FitnessDAO` | `List<User>`, `ArrayList<Attendance>`, `Map<Integer, DietPlan>` for in-memory and database result handling. |
| **Java 2D Graphics API** | `WeightProgressChartPanel.java`, `AttendanceChartPanel.java` | Extends `JPanel` and overrides `paintComponent(Graphics g)` to render custom column charts, gauges, and gradient progress bars. |
| **File Handling** | `FileHandler.java` | Uses `FileWriter`, `BufferedWriter`, `PrintWriter`, and `BufferedReader` to export and preview member rosters and attendance reports. |
| **JDBC & PreparedStatement** | `DatabaseConnection.java`, `UserDAO.java`, etc. | Secure parameterized queries (`?`) preventing SQL Injection attacks. |

---

## 🚀 How to Run

### Method 1: Double-Click Launcher (Windows)
Double-click `compile_and_run.bat`. It will compile all source files and open the application GUI immediately.

### Method 2: Command Line (PowerShell / Terminal)
```powershell
# 1. Compile source files
$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.3.0.jar" -d "bin" $sources

# 2. Run Main Application
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.smartgym.main.Main

# 3. (Optional) Run 10-Point Verification Test Suite
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.smartgym.test.TestRunner
```

---

## 🔑 Default Credentials for Quick Demo

| Role | Username | Password | Full Name | Description |
|---|---|---|---|---|
| **Admin** | `admin` | `admin123` | Vikram Malhotra | Executive Management & Analytics |
| **Trainer** | `trainer1` | `trainer123` | Sneha Sen | Nutritionist & Coach (Attendance, Diets, Workouts) |
| **Trainer** | `trainer2` | `trainer123` | Kabir Joshi | Strength & Conditioning Coach |
| **Member** | `rahul` | `member123` | Rahul Sharma | Goal: Weight Loss (85kg -> 72kg, Current: 79kg) |
| **Member** | `priya` | `member123` | Priya Nair | Goal: Lean Recomposition (68kg -> 58kg) |
| **Member** | `arjun` | `member123` | Arjun Patel | Goal: Hypertrophy & Bulk (68kg -> 78kg) |

*(Note: You can also click the 1-Click Quick Demo buttons directly on the Login screen!)*
