javac -d bin src/common/*.java src/tcp/*.java
java -cp bin tcp.TCPServer 8080   
java -cp bin tcp.TCPClient localhost 8080 

javac -d bin src/common/*.java src/udp/*.java
java -cp bin udp.UDPServer 8080   
java -cp bin udp.UDPClient localhost 8080