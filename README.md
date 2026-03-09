# Car Renting System

A robust Java-based Car Rental Management System featuring a multi-layered architecture, support for various persistence strategies, and a dual user interface (GUI & CLI).

## 🚀 Key Features

* **Car Management**: Complete CRUD operations (Create, Read, Update, Delete) for vehicles, including brand and model details.
* **Rental Management**: Schedule and manage car rentals with specific start and end dates.
* **Advanced Persistence**: Supports multiple storage engines via a flexible Repository pattern:
* **In-Memory**: Fast, volatile storage.
* **Text/Binary Files**: Local file-based persistence.
* **SQL (SQLite)**: Relational database support.
* **NoSQL (MongoDB)**: Document-based database support.


* **Analytics & Reports**:
* Most rented car models.
* Rental volume per month.
* Total rental days accumulated per model.


* **Dual Interface**: Switch between a JavaFX Graphical User Interface (GUI) and a Command Line Interface (CLI).
* **Automatic Data Generation**: Includes a utility to populate the system with sample data if the database is empty.

## 🛠️ Technical Stack

* **Language**: Java (JDK 25).
* **GUI Framework**: JavaFX 21.
* **Build Tool**: Maven.
* **Databases**: SQLite (via JDBC), MongoDB.
* **Logging**: SLF4J.

## 📂 Project Structure

* `domain/`: Core entities (`Car`, `Rental`).
* `repository/`: Implementation of storage strategies and factories.
* `service/`: Business logic and reporting services.
* `ui/`: Both CLI (`UI.java`) and JavaFX GUI controllers.
* `utils/`: `DataGenerator` for testing purposes.

## ⚙️ Configuration

The application is configured via the `settings.properties` file:

```properties
Repository=nosql          # Options: memory, text, binary, sql, nosql
RepoLocation=rentals.db   # SQLite database path
CarsFile=cars.txt         # Text/Binary storage file for cars
RentalsFile=rentals.txt   # Text/Binary storage file for rentals
UIType=GUI                # Options: GUI, UI (for Console)
MongoURL=mongodb://localhost:27017
MongoDbName=CarRentalDB

```

## 🚀 Getting Started

1. **Prerequisites**: Ensure you have JDK 25 and Maven installed.
2. **Configure**: Set your preferred repository type in `settings.properties`.
3. **Build**:
```bash
mvn clean install

```


4. **Run**:
```bash
mvn javafx:run

```



## 📊 Database Schema (SQL)

The system automatically manages the following tables:

* **Cars**: `id` (PK), `brand`, `model`.
* **Rentals**: `id` (PK), `carId` (FK), `startDate`, `endDate`.
