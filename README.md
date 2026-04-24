# Smart Campus API

**Name:** Movindu Gamage  
**Student ID:** 20241487  
**UOW ID:** w2121365  

---

## API Overview

The Smart Campus API is a RESTful web service developed with JAX-RS (Java API for RESTful Web Services) using the Jersey framework. Its purpose is to oversee campus rooms and the IoT sensors installed in them, offering managers and automated building systems a clear and organized interface to engage with campus infrastructure data.

The API has three main resources:

- **Rooms** - Physical spaces on campus. You can create, list, get, and delete rooms
- **Sensors** - IoT devices deployed within rooms. You can register sensors inside rooms and filter them by type
- **Sensor Readings** - Historical measurement data recorded by each sensor. You can add readings to a sensor and view its reading history

All responses come back as JSON. All errors return a clean JSON message — no Java stack traces are ever shown to the user.

**Base URL:** `http://localhost:8080/smart-campus-w2121365/api/v1/`

---

## Build & Run Instructions

### Prerequisites
- Java JDK 11 or higher installed
- Apache NetBeans installed
- Apache Tomcat 9.0 extracted (e.g. to `C:\apache-tomcat-9.0.100`)

### Step 1 Clone the Repository
```bash
git clone https://github.com/Movindu-Gamage/Smart-Campus-API.git
```

### Step 2 - Build the Project
Open the project in NetBeans, then:
- Right click the project → **Clean and Build**
- Wait for **BUILD SUCCESS** in the output window

### Step 3 - Copy the WAR file to Tomcat
Go to your project folder → open the `target` folder → copy `smart-campus-w2121365-1.0-SNAPSHOT.war` → paste it into `C:\apache-tomcat-9.0.100\webapps\`

### Step 4 - Start Tomcat
In NetBeans:
- Right click the project → **Run**
- NetBeans will start Tomcat and deploy automatically

### Step 5 - Verify the API is Running
Open your browser and navigate to:
```
http://localhost:8080/smart-campus-w2121365/api/v1/
```

### Step 6 — Stopping the Server
```bash
C:\apache-tomcat-9.0.100\bin\shutdown.bat
```

---

## Sample curl Commands

**1. Get the API discovery info**
```bash
curl -X GET http://localhost:8080/smart-campus-w2121365/api/v1/
```

**2. Get all rooms**
```bash
curl -X GET http://localhost:8080/smart-campus-w2121365/api/v1/rooms
```

**3. Create a new room**
```bash
curl -X POST http://localhost:8080/smart-campus-w2121365/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "HALL-001", "name": "Main Hall", "capacity": 200}'
```

**4. Register a new sensor**
```bash
curl -X POST http://localhost:8080/smart-campus-w2121365/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "currentValue": 22.0, "roomId": "CLB-101"}'
```

**5. Filter sensors by type**
```bash
curl -X GET "http://localhost:8080/smart-campus-w2121365/api/v1/sensors?type=CO2"
```

**6. Post a reading to a sensor**
```bash
curl -X POST http://localhost:8080/smart-campus-w2121365/api/v1/sensors/CO2-101/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 520.0}'
```

**7. Get the reading history for a sensor**
```bash
curl -X GET http://localhost:8080/smart-campus-w2121365/api/v1/sensors/CO2-101/readings
```

---

## Report — Written Question Answers

### Part 1: Service Architecture & Setup

**Question 1**

By default, JAX-RS creates a new instance of a resource class for each HTTP request, meaning variables in the class are discarded after the request. As a result, shared data cannot be stored in a resource class. To preserve data across requests, this project uses a separate DataStore class with static lists and maps. Static fields belong to the class itself and persist as long as the server runs.

**Question 2**

HATEOAS stands for Hypermedia as the Engine of Application State, where an API includes links to related resources in its responses, allowing clients to easily navigate. For example, the discovery endpoint offers links to /api/v1/rooms and /api/v1/sensors, eliminating the need for clients to hardcode URLs. If a URL changes later, clients can still function by following the links. This makes the API self-describing and simpler to use.

---

### Part 2: Room Management

**Question 1**

Returning only IDs results in a smaller, faster response, but requires the client to make additional requests for each room's details. This means 51 requests for 50 rooms, which is inefficient. In contrast, sending full room objects offers all information in a single request, and since the room data is small, this approach is preferable to avoid extra round trips, even if responses are larger.

**Question 2**

Yes, DELETE is idempotent. When you delete a room the first time, it is removed and you get a 200 OK. If you send the same request again, you receive a 404 Not Found because the room is gone. The outcome is the same — the room does not exist. This aligns with the HTTP standard, which mandates that DELETE be idempotent: the server state remains unchanged whether you request it once or multiple times.

---

### Part 3: Sensor Operations & Linking

**Question 1**

JAX-RS verifies the Content-Type header of each incoming request prior to invoking your Java method. If the client transmits text/plain or application/xml instead of application/json, JAX-RS will automatically return HTTP 415 Unsupported Media Type and will not execute your code. There is no need to implement any additional code to manage this — Jersey handles it automatically.

**Question 2**

The URL path identifies a particular resource. The path /api/v1/sensors signifies the entire collection of sensors. Incorporating the filter type into the path, such as /sensors/type/CO2, implies that type/CO2 is an independent resource, which is misleading and incorrect. Using query parameters like ?type=CO2 is the appropriate method for filtering — they are intended for searching and refining results. They also work seamlessly together, allowing more filters to be added later without altering the URL structure.

---

### Part 4: Deep Nesting with Sub-Resources

**Question 1**

Instead of consolidating all code into a single resource class, the sub-resource locator enables dividing work into smaller, focused classes. In this project, when a request is made to /sensors/{sensorId}/readings, the SensorResource class creates and returns a SensorReadingResource object for processing. This design allows SensorReadingResource to concentrate solely on readings, separating it from sensor logic. As a result, the code is more readable, testable, and easier to modify while reflecting the data structure where a reading belongs to a sensor.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

**Question 1**

A 404 error indicates that the URL you are requesting does not exist. But /api/v1/sensors exists — the request was sent to the correct location. The request body itself has the problem. The roomId field points to a nonexistent room. The JSON is correct and the server recognises the request, but cannot process it because the data contains an incorrect value. HTTP 422 Unprocessable Entity is specifically designed to handle this case: the server received the request correctly, but the request content is malformed.

**Question 2**

Stack traces give attackers a lot of useful information. First, they show the exact names and versions of the frameworks and libraries being used, which allows attackers to search for known security weaknesses in those versions. Second, they expose the internal package and class names of the application, revealing its code structure. Third, they reveal which methods are called, thereby showing the application's internal logic. This project fixes this by using the GlobalExceptionMapper, which catches any unexpected errors and sends only a plain safe message back to the client, while logging the real details on the server side only.

**Question 3**

If you manually add logging to every resource method, you have to remember to add it every time you create a new endpoint. If you forget one, that request goes unlogged. You also end up repeating the same logging code across many different classes, making it hard to maintain. A JAX-RS filter registered once with @Provider runs automatically for every single request and response without needing to touch any resource class. If you ever want to change what gets logged, you only update one file. This keeps the resource classes focused on business logic while the filter handles logging cleanly in one place.
