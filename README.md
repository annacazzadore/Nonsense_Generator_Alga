User stories: https://nonsense-generator-alga.atlassian.net/jira/software/projects/ALGA/boards/67

# 📘 Nonsense Generator ALGA

## 1. High-Level Project Description

**Nonsense Generator ALGA** is a Java-based application designed to generate nonsensical sentences from textual inputs using semantic analysis. It features a JavaFX graphical interface and integrates with **Google Cloud Natural Language API** for natural language processing.

The project is structured to be modular, testable (JUnit5), and portable using Maven for dependency and build management.

---

## 2. Installation and Execution Instructions

### ⚙️ Requirements

#### ✅ Common Requirements
- Java **JDK 24**
- JavaFX **24.0.1**
- Internet connection (required for Google Cloud APIs)
- Google Cloud account with **Natural Language API** enabled
- Maven **3.9.9** (required for macOs only, recomended for Windows) 

#### 🪟 For **Windows** users
1. Install **JDK 24**  
   ➤ [Download JDK](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)  
   ➤ Add `JAVA_HOME` and update your system `PATH`.

2. Install **JavaFX 24**  
   ➤ [Download JavaFX SDK](https://gluonhq.com/products/javafx/)  
   ➤ Extract it to a folder, e.g., `C:\javafx-sdk-24.0.1`.

3. Install **Maven 3.9.9**  
   ➤ [Download Maven](https://maven.apache.org/download.cgi)  
   ➤ Add `MAVEN_HOME` and update your system `PATH`.

#### 🍎 For **macOS** users
1. Install **JDK 24**  
   ➤ [Download JDK](https://www.oracle.com/java/technologies/downloads/#jdk24-mac)  
   ➤ Add `JAVA_HOME` and update your system `PATH`.

2. Install **JavaFX 24**  
   ➤ [Download JavaFX SDK](https://gluonhq.com/products/javafx/)  
   ➤ Extract it to a folder, e.g., `C:\javafx-sdk-24.0.1`.

3. Install **Maven 3.9.9**  
   You can use [Homebrew](https://brew.sh/) to install Maven:
   ```bash
   brew install maven
   ```

Make sure `JAVA_HOME` and JavaFX are properly configured in your shell (e.g., `.zshrc` or `.bash_profile`).

---

### 🔧 Installation

1. Clone or download the project:
   ```bash
   git clone https://github.com/annacazzadore/Nonsense_Generator_Alga.git
   cd Nonsense_Generator_Alga
   ```
2. 🔐 Google Cloud Configuration

   1. Enable the **Natural Language API** in your Google Cloud project.
   2. Download your service account JSON key file.
   3. Configure the credentials path inside the `config.properties` file.

   **Windows path:**

   ```text
   C:\Users\YourUsername\Documents\GitHub\Nonsense_Generator_Alga\src\main\resources\it\unipd\nonsense\config.properties
   ```
   
   **macOS path:**
   
   ```text
   /Users/YourUsername/Documents/GitHub/Nonsense_Generator_Alga/src/main/resources/it/unipd/nonsense/config.properties
   ```

3. Build the project with Maven:
   ```bash
   mvn clean package
   ```

   This will generate an executable JAR with all dependencies in the `target/` directory:

  ```text
  target/Nonsense_Generator_Alga-1.0-SNAPSHOT.jar
  ```

---

### 🚀 Launch Instructions

#### ▶️ Run with JAR (Cross-platform)
Make sure JavaFX SDK is correctly referenced in the module path.

**Windows:**

```bash
java --module-path "C:\javafx-sdk-24.0.1\lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     -jar target/Nonsense_Generator_Alga-1.0-SNAPSHOT.jar
```

**macOS:**

```bash
java --module-path "/path/to/javafx-sdk-24.0.1/lib" \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/Nonsense_Generator_Alga-1.0-SNAPSHOT.jar
```

#### ▶️ Run Executable File (Windows only)

If an `.exe` version of the program is provided:

1. Ensure **JDK 24** and **JavaFX 24** are installed.
2. Double-click the `.exe` file to launch the application.

---

## 3. Execution Environment

- **Java:** JDK 24
- **Maven:** Used for building and managing dependencies
- **OS:** Compatible with Windows, macOS
- **UI Framework:** JavaFX 24.0.1

---

## 4. Main Reused Libraries (Excluding Basic Ones)

| Library                          | Purpose |
|----------------------------------|---------|
| `com.google.cloud:google-cloud-language` | Text semantic analysis |
| `io.grpc:*`                      | Communication with remote services (Google Cloud API) |
| `com.google.guava:guava`        | Utilities (collections, cache, immutability) |
| `com.google.code.gson:gson`     | JSON parsing and serialization |
| `org.openjfx:javafx-*`          | GUI (FXML, controls) |
| `org.junit.jupiter:*`           | Unit and integration testing |

---

## 5. External APIs Used

- **Google Cloud Natural Language API**  
  Used for syntactic and semantic analysis of text to generate nonsense sentences.

---
