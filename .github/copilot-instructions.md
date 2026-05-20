This project is a Java multithreading simulation.

Network represents the shared communication medium.
Clients and Servers run as separate threads.
Use synchronized blocks when accessing shared buffers.
Prefer simple educational implementations over production-ready abstractions.

# COMP346 PA1 Context

This project is a Java multithreaded client-server banking simulation for COMP346 Operating Systems.

Architecture:
- Client-server model communicating through a shared Network object.
- Network contains:
    - input buffer (inComingPacket[])
    - output buffer (outGoingPacket[])
- Both buffers have capacity 10.
- Client sends banking transactions to server through the network.
- Server processes transactions and returns updated transactions.

Required Threads:
- 4 total concurrent threads:
    - Network thread
    - Server thread
    - Client sending thread
    - Client receiving thread

Client Responsibilities:
- Read transactions from transaction.txt into transaction[].
- Send transactions using Network.send().
- Receive processed transactions using Network.receive().
- Display completed transactions immediately.
- If network buffer is full/empty:
    - first implement busy waiting
    - then implement Thread.yield()

Server Responsibilities:
- Read accounts from account.txt into account[].
- Retrieve transactions using Network.transferIn().
- Process:
    - withdraw
    - deposit
    - query
- Return completed transactions using Network.transferOut().
- Yield CPU if buffers are empty/full.

Network Responsibilities:
- Simulate the communication infrastructure.
- Maintain:
    - connection status
    - input/output buffers
    - buffer indexes
    - buffer states (full/empty/normal)
- Network thread runs infinite loop until:
    - client disconnected
    - server disconnected
- While active, network thread continuously yields CPU.

Performance Requirements:
- Measure running times using:
  System.currentTimeMillis()
- Measure:
    - client sending thread
    - client receiving thread
    - server thread
- Compare:
    - busy waiting
    - Thread.yield()
- Explain runtime differences across 3 runs.

Important Constraints:
- Do NOT redesign the provided architecture.
- Keep provided data types and array sizes unchanged.
- Focus on educational threading concepts.
- Prefer explicit thread lifecycle visibility over abstraction.

Threading Model:
- Classes likely extend Thread.
- run() contains concurrent logic.
- Shared Network object accessed concurrently.
- Synchronization and buffer coordination are central concepts.

Educational Goal:
This project demonstrates:
- multithreading
- CPU yielding
- busy waiting
- shared resources
- producer-consumer style communication
- concurrent client/server interaction
- scheduling behavior