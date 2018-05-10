# Akka HTTP WebSocket

## Code Structure

    ├── src
    │   └── main
    │       ├── java
    │       │   └── com
    │       │       └── kevinsimard
    │       │           └── akka
    │       │               └── http
    │       │                   └── websocket
    │       │                       ├── Application.java
    │       │                       ├── ChatRoomActor.java
    │       │                       ├── WallclockActor.java
    │       │                       └── WallclockWorker.java
    │       └── resources
    │           └── log4j.properties
    ├── .editorconfig
    ├── .gitattributes
    ├── .gitignore
    ├── README.md
    └── pom.xml

## Usage

Run `mvn compile exec:java` to run the application.

### Chat Room

Connect to `ws://localhost:8080/chatroom/<username>` with a WebSocket client to send messages between connections.

### Wallclock

Connect to `ws://localhost:8080/wallclock` with a WebSocket client to receive push messages of the current date every second.
