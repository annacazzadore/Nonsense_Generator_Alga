User stories: https://nonsense-generator-alga.atlassian.net/jira/software/projects/ALGA/boards/67

# 📘 Nonsense Generator ALGA

## 1. High-Level Project Description

**Nonsense Generator ALGA** is a Java-based application designed to generate nonsensical sentences from textual inputs using semantic analysis. It features a JavaFX graphical interface and integrates with **Google Cloud Natural Language API** for natural language processing.

The project is structured to be modular, testable (JUnit5), and portable using Maven for dependency and build management.

---

## 2. Installation and Execution Instructions

### ⚙️ Requirements

- Java **JDK 24**
- Maven **3.6+**
- Internet connection (required for external APIs)
- Google Cloud account with Natural Language API enabled

There are three metod of installation/launch:
- **Maven-based**: Run `mvn clean package` to build the project, then `mv
- **IDE-based**: Import the project into your IDE (e.g., Eclipse, IntelliJ IDEA)
- **Executable-based**: launch the executable file (.exe for windows or .dmg for macOS)

### 🔧 Installation

1. Clone or download the project:
   ```bash
   git clone https://your.repo.url/Nonsense_Generator_Alga.git
   cd Nonsense_Generator_Alga
   ```

2. Build the project with Maven:
   ```bash
   mvn clean package
   ```

   This generates an executable JAR with all dependencies (`target/*.jar`).

### 🚀 Launch

Run the program using:

```bash
java --module-path "C:\javafx-sdk-24.0.1\lib" `
     --add-modules javafx.controls,javafx.fxml `
     -jar target/Nonsense_Generator_Alga-1.0-SNAPSHOT.jar 
```

Or (if JavaFX plugin is configured)?????:

```bash
mvn javafx:run
```

---

## 3. Execution Environment

- **Java:** JDK 24
- **Maven:** Used for building and managing dependencies
- **OS:** Compatible with Windows, macOS, and Linux
- **UI Framework:** JavaFX 24.0.1

> ⚠️ Ensure JavaFX is properly set up in your environment. Some Linux systems may require additional libraries (`libopenjfx`).

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

### Usage Instructions

You must:

- Enable the API on your Google Cloud project
- Set credentials via `GOOGLE_APPLICATION_CREDENTIALS`
- At the moment there is a config.properties

  (Win: C:\Users\YourUsername\Documents\GitHub\Nonsense_Generator_Alga\src\main\resources\it\unipd\nonsense\config.properties;

macOS: /Users/lari/Documents/GitHub/Nonsense_Generator_Alga/src/main/resources/it/unipd/nonsense/config.properties) file where you can put the path of your credentials file

Example:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/key.json"
```

---