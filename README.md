# SpaceTime-WikiMaps

A Java-based application that leverages Apache Jena, Spring Boot, and WikiData Toolkit to retrieve and perform SPARQL queries against WikiData. The project provides a platform for querying WikiData's vast knowledge base and processing semantic web data.

## Overview

SpaceTime-WikiMaps is a Java Spring Boot application that enables users to execute SPARQL queries against WikiData using Apache Jena and WikiData Toolkit. The project demonstrates semantic web technologies and provides a foundation for building WikiData-powered applications with relational database persistence.

## Features

- **SPARQL Query Execution**: Execute SPARQL queries against WikiData using Apache Jena and WikiData Toolkit
- **Data Processing**: Retrieve data from WikiData and process it based on user-defined queries
- **Spring Boot Integration**: Seamlessly integrated with Spring Boot for easy deployment and management
- **H2 Database**: Embedded database for local data persistence
- **RESTful API**: Expose WikiData queries through REST endpoints
- **Data Mapping**: Automatic mapping between WikiData entities and application models

## Technology Stack

- **Java** - Core programming language
- **Spring Boot** - Application framework
- **Apache Jena** - Semantic web framework for RDF and SPARQL
- **WikiData Toolkit** - Java library for WikiData access
- **H2 Database** - Embedded relational database
- **Maven** - Build and dependency management

## Prerequisites

Ensure you have the following installed:

- **Java JDK 11** or higher
- **Maven 3.6** or higher
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/WhyN0t101/SpaceTime-WikiMaps.git
   cd SpaceTime-WikiMaps
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the compiled JAR:
   ```bash
   java -jar target/spacetime-wikimaps-*.jar
   ```

## Database Configuration

The application uses an embedded H2 database for data persistence. Follow these steps to configure it properly:

### Setting Up H2 Database in IntelliJ IDEA

1. **Add New Data Source**
   - Go to `Database` tab
   - Click `+` → `Data Source` → `H2`

   ![Database Setup](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/1782760c-65ae-4bb4-a39c-71e998d54c6f)

2. **Configure Database Properties**
   - **Name**: `demo`
   - **Connection Type**: `Embedded`
   - **Path**: `/data/demo.mv.db`
   - **User**: `root`
   - **Password**: `root`

   ![Database Properties](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/2fbfcfed-fc62-47e8-97a2-f08042b3b436)

3. **Map Entity Classes**
   - Assign the `User` entity class to the data source

   ![Entity Mapping](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/5424f536-44bd-498c-8bf4-be3109f18dde)

4. **Define Data Source**
   - Link the configured data source to the database

   ![Data Source Definition](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/8a2044bf-62f6-47c7-881c-579038ddb847)

5. **Initialize Database**
   - Run the application to create database tables automatically

   ![Database Tables](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/eb909d28-8148-4ac0-a98d-548ab7492c75)
   ![Table Verification](https://github.com/WhyN0t101/SpaceTime-WikiMaps/assets/100608872/ae87a03f-97da-4af1-a7eb-5f68e95d943e)

## Usage

### Executing SPARQL Queries

The application provides endpoints for querying WikiData:

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Access the application**
   - The application runs on `http://localhost:8080` by default

3. **Execute queries**
   - Use the REST API to submit SPARQL queries
   - Results are processed and stored in the H2 database

### Example SPARQL Query

```sparql
SELECT ?item ?itemLabel WHERE {
  ?item wdt:P31 wd:Q5.
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
}
LIMIT 10
```

## Configuration

### Application Properties

Edit `src/main/resources/application.properties`:

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:file:./data/demo
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=root
spring.datasource.password=root

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# H2 Console (optional, for development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Maven Dependencies

Key dependencies are defined in `pom.xml`:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Apache Jena
- WikiData Toolkit
- H2 Database

## Troubleshooting

### Database Connection Issues

**Issue**: Can't connect to H2 database

**Solutions**:
- Verify database path is correct
- Check user credentials (root/root)
- Ensure application has write permissions in the data directory
- Try deleting the database file and letting the application recreate it

### Maven Build Failures

**Issue**: Build fails with dependency errors

**Solutions**:
- Run `mvn clean install -U` to force update dependencies
- Check internet connection for dependency downloads
- Verify Java version compatibility (JDK 11+)
- Clear Maven local repository: `~/.m2/repository`

### WikiData Query Timeout

**Issue**: SPARQL queries time out

**Solutions**:
- Simplify query (reduce LIMIT, fewer joins)
- Check internet connection
- Try query directly on WikiData Query Service first
- Consider implementing query caching

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the GNU General Public License - see the [LICENSE](LICENSE) file for details.

---

**Note**: This project demonstrates semantic web technologies and WikiData integration. It's designed for educational purposes and as a foundation for building WikiData-powered applications.
