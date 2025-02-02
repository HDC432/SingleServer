# Key-Value Store Application (TCP & UDP)

This project is a simple key-value store application that supports communication over both **TCP** and **UDP** protocols. The application includes a server that handles key-value storage operations (`PUT`, `GET`, `DELETE`) and a client that interacts with the server to perform these operations.

---

##  **Running the Application**

##  **Compilation Instructions**

- **Compile TCP-related files:**
   ```bash
   javac -d bin src/common/*.java src/tcp/*.java
   ```

- **Compile UDP-related files:**
   ```bash
   javac -d bin src/common/*.java src/udp/*.java
   ```

---

### **1 TCP Mode**

- **Start the TCP Server:**
  ```bash
  java -cp bin tcp.TCPServer 8080
  ```
  - `8080` is the port number the server listens on.

- **Start the TCP Client:**
  ```bash
  java -cp bin tcp.TCPClient localhost 8080
  ```
  - Connects to the server at `localhost` on port `8080`.

---

### **2️ UDP Mode**

- **Start the UDP Server:**
  ```bash
  java -cp bin udp.UDPServer 8080
  ```
  - `8080` is the port number the server listens on.

- **Start the UDP Client:**
  ```bash
  java -cp bin udp.UDPClient localhost 8080
  ```
  - Connects to the server at `localhost` on port `8080`.


