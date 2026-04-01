# 🇮🇳 Digital Election System - Complete Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Class Descriptions](#class-descriptions)
4. [Key Features](#key-features)
5. [Security Features](#security-features)
6. [How to Run](#how-to-run)
7. [User Guide](#user-guide)
8. [Data Flow](#data-flow)
9. [OOP Concepts Used](#oop-concepts-used)

---

## Project Overview

The **Digital Election System** is a secure, object-oriented Java application designed to manage democratic elections digitally. It implements real-world election processes with authentication, token-based voting, and comprehensive audit logging.

### Objectives:
- Provide a secure voting mechanism
- Ensure each voter votes only once
- Maintain transparency through audit logs
- Support multiple candidates
- Enforce role-based access control

---

## System Architecture

```
┌─────────────────────────────────────────┐
│       ElectionSystem (Main)             │
│      - Menu-driven Interface            │
└────────────┬────────────────────────────┘
             │
             ├─ ElectionCore (Business Logic)
             │  ├─ voters (HashMap)
             │  ├─ officers (HashMap)
             │  ├─ votes (ArrayList)
             │  └─ status (ElectionStatus)
             │
             ├─ Ballot (Candidate Management)
             │  └─ candidates (List)
             │
             ├─ TokenService (Token Generation)
             │  └─ validTokens (Set)
             │
             └─ AuditLog (Activity Tracking)
                └─ logs (List)
```

---

## Class Descriptions

### 1. **Enums**

#### `ElectionStatus`
Tracks the current state of the election process.
```java
enum ElectionStatus {
    NOT_STARTED,  // Election hasn't begun
    ONGOING,      // Voting is active
    ENDED         // Voting has concluded
}
```

#### `Role`
Defines user roles in the system.
```java
enum Role {
    VOTER,  // General voter
    ADMIN   // Election Officer/Administrator
}
```

---

### 2. **Abstract Class: Person**

**Purpose:** Base class for all individuals in the system.

**Attributes:**
- `id` (String): Unique identifier
- `name` (String): Full name
- `role` (Role): User role (VOTER or ADMIN)

**Methods:**
- `Person(String id, String name, Role role)`: Constructor

**Key Concept:** Uses inheritance to ensure common properties for both voters and election officers.

---

### 3. **ElectionOfficer Class**

**Extends:** Person

**Purpose:** Represents administrators/election officials who manage the election process.

**Attributes:**
- `password` (String): Secure authentication credential

**Methods:**
- `ElectionOfficer(String id, String name, String password)`: Constructor
  - Automatically assigns Role.ADMIN
  
- `authenticate(String pass)`: Validates password
  - Returns `true` if password matches, `false` otherwise

**Responsibilities:**
- Start/end elections
- Register voters
- Issue voting tokens
- View audit logs

**Default Officer:**
```
ID: admin
Name: Rajesh Kumar (Chief Election Officer)
Password: admin123
```

---

### 4. **Voter Class**

**Extends:** Person

**Purpose:** Represents eligible voters in the system.

**Attributes:**
- `eligible` (boolean): Eligibility status (always true on creation)
- `tokenIssued` (boolean): Tracks if voter received a voting token

**Methods:**
- `Voter(String id, String name)`: Constructor
  - Automatically assigns Role.VOTER
  - Sets eligible = true, tokenIssued = false
  
- `isEligible()`: Returns eligibility status
- `hasToken()`: Checks if token already issued
- `issueToken()`: Marks token as issued

**Protection:** Prevents duplicate token issuance to the same voter.

---

### 5. **Candidate Class**

**Purpose:** Represents candidates in the election.

**Attributes:**
- `id` (String): Unique candidate identifier
- `name` (String): Candidate name
- `votes` (int): Vote count (initialized to 0)

**Methods:**
- `Candidate(String id, String name)`: Constructor
- `addVote()`: Increments vote count
- `getId()`: Returns candidate ID
- `getName()`: Returns candidate name
- `getVotes()`: Returns current vote count

**Default Candidates:**
1. Amit Sharma
2. Priya Verma
3. Rahul Singh

---

### 6. **Vote Class (Immutable)**

**Purpose:** Represents a single vote cast in the system.

**Key Feature:** Final class - ensures immutability for security.

**Attributes:**
- `token` (final String): The token used to cast vote
- `candidateId` (final String): ID of chosen candidate

**Methods:**
- `Vote(String token, String candidateId)`: Constructor
- `getCandidateId()`: Returns candidate ID

**Why Immutable?**
- Prevents tampering with vote records
- Ensures data integrity
- Thread-safe

---

### 7. **TokenService Class**

**Purpose:** Manages voting tokens to ensure each voter votes only once.

**Attributes:**
- `validTokens` (Set<String>): Stores active token IDs

**Methods:**
- `generateToken()`: Creates new UUID-based token
  - Generates unique token using UUID.randomUUID()
  - Adds to validTokens set
  - Returns token string
  
- `isValid(String token)`: Checks if token exists and hasn't been used
  - Returns true if token is in the set
  
- `invalidate(String token)`: Marks token as used
  - Removes token from validTokens set

**Security Logic:**
```
Token Lifecycle:
1. Generated → Added to validTokens
2. Used for voting → Checked in validTokens
3. Vote recorded → Removed from validTokens
4. Cannot vote again → Token not in validTokens
```

---

### 8. **AuditLog Class**

**Purpose:** Records all actions for transparency and security.

**Attributes:**
- `logs` (List<String>): Timeline of all system actions

**Methods:**
- `record(String action)`: Records action with timestamp
  - Format: "[Action] at [Date/Time]"
  
- `showLogs()`: Displays all recorded logs

**Logged Actions:**
- Election Started
- Election Ended
- Voter Registered
- Token Issued
- Vote Cast

**Importance:**
- Provides accountability
- Enables investigation of irregularities
- Maintains election integrity

---

### 9. **Ballot Class**

**Purpose:** Manages the list of candidates and voting options.

**Attributes:**
- `candidates` (List<Candidate>): All available candidates

**Methods:**
- `Ballot(List<Candidate> candidates)`: Constructor
  
- `displayCandidates()`: Shows all candidates with IDs
  - Terminal output format: "ID. Name"
  
- `findCandidate(String id)`: Searches for candidate by ID
  - Returns Candidate object or null
  
- `getAll()`: Returns all candidates

**Functionality:**
- Displayed to voters during voting
- Validates candidate selection
- Retrieves candidate for vote recording

---

### 10. **ElectionCore Class**

**Purpose:** Central business logic managing the entire election process.

**Attributes:**
- `voters` (Map<String, Voter>): Registered voters by ID
- `officers` (Map<String, ElectionOfficer>): Administrators
- `votes` (List<Vote>): All cast votes
- `ballot` (Ballot): Available candidates
- `tokenService` (TokenService): Token management
- `audit` (AuditLog): Activity logging
- `status` (ElectionStatus): Current election state
- `loggedInOfficer` (ElectionOfficer): Currently authenticated officer

**Core Methods:**

#### `login(String id, String password)`
- Authenticates election officer
- Verifies credentials against stored officers
- Sets loggedInOfficer on success
- Prevents unauthorized actions

#### `startElection()`
- **Requirement:** Officer must be logged in
- Sets status to ONGOING
- Records in audit log
- Enables voting

#### `endElection()`
- **Requirement:** Officer must be logged in
- Sets status to ENDED
- Records in audit log
- Prevents new votes

#### `registerVoter(String id, String name)`
- Creates new Voter object
- Stores in voters map
- Records registration in audit log
- Returns confirmation message

#### `issueToken(String voterId)`
- Retrieves voter by ID
- Validates voter exists
- Checks voter hasn't already received token
- Generates unique token via TokenService
- Marks voter as token-issued
- Returns token string for voter

#### `castVote(String token, String candidateId)`
- **Validations:**
  1. Election must be ONGOING
  2. Token must be valid (not used)
  3. Candidate ID must exist
  
- **Process:**
  1. Creates Vote object
  2. Increments candidate vote count
  3. Invalidates token (prevents reuse)
  4. Records vote in audit log

#### `showResults()`
- **Requirement:** Election must be ENDED
- Displays all candidates with vote counts
- Calculates and announces winner (highest votes)

#### `showLogs()`
- **Requirement:** Officer must be logged in
- Displays complete audit log
- Ensures transparency and accountability

---

### 11. **ElectionSystem Class (Main)**

**Purpose:** Entry point and user interface.

**Main Features:**
- Menu-driven console interface
- 9 operational options
- Continuous loop until exit

**Menu Options:**
```
1. Election Officer Login      - Admin authentication
2. Start Election              - Begin voting period
3. Register Voter              - Add new voter
4. Get Voting Token            - Issue token to voter
5. Cast Vote                   - Record a vote
6. End Election                - Close voting period
7. View Results                - Display final results
8. View Audit Logs             - Show action history
9. Exit                        - Terminate program
```

---

## Key Features

### 1. **Role-Based Access Control**
- Only authenticated officers can perform administrative actions
- Voters cannot access officer functions
- `loggedInOfficer` null-check prevents unauthorized operations

### 2. **Token-Based Voting**
- Each voter receives unique token
- One token = one vote
- Token invalidated after use
- Prevents duplicate voting

### 3. **State Management**
- Election lifecycle: NOT_STARTED → ONGOING → ENDED
- Operations restricted based on current state
- Voting only allowed when ONGOING

### 4. **Audit Trail**
- Every action logged with timestamp
- Provides accountability
- Enables fraud detection

### 5. **Candidate Management**
- Multiple candidates supported
- Easy candidate lookup
- Vote counting per candidate
- Winner determination

---

## Security Features

### 1. **Authentication**
- Password verification for officers
- Default credentials provided for demo

### 2. **Token Security**
- UUID-based random token generation
- Cryptographically secure random strings
- One-time use tokens

### 3. **Immutability**
- Vote class is final and immutable
- Prevents vote tampering
- Ensures data integrity

### 4. **Access Control**
- Operations require officer login
- Null checks prevent unauthorized access
- Role-based permissions

### 5. **Audit Logging**
- Complete activity history
- Timestamp on every action
- Transparent record keeping

---

## How to Run

### Prerequisites:
- Java Development Kit (JDK) 8 or higher installed
- Command line/terminal access

### Compilation:
```bash
# Navigate to src directory
cd src

# Compile the Java file
javac ElectionSystem.java
```

### Execution:
```bash
# Run the program
java ElectionSystem
```

### Expected Output:
```
====== 🇮🇳 DIGITAL ELECTION SYSTEM ======
1. Election Officer Login
2. Start Election
3. Register Voter
4. Get Voting Token
5. Cast Vote
6. End Election
7. View Results
8. View Audit Logs
9. Exit
```

---

## User Guide

### Scenario: Complete Election Process

#### Step 1: Officer Login
```
Menu Option: 1
Officer ID: admin
Password: admin123
Output: ✅ Welcome Rajesh Kumar (Chief Election Officer)
```

#### Step 2: Start Election
```
Menu Option: 2
Output: 🟢 Election has started.
```

#### Step 3: Register Voters
```
Menu Option: 3
Enter Voter ID: V001
Enter Name: John Doe
Output: ✅ Voter John Doe registered successfully.

(Repeat for more voters)
```

#### Step 4: Issue Voting Tokens
```
Menu Option: 4
Enter Voter ID: V001
Output: 🔐 Your Secure Token: [UUID STRING]
```

#### Step 5: Cast Votes (By Voters)
```
Menu Option: 5
Enter Token: [Received UUID]
🗳️ Available Candidates:
1. Amit Sharma
2. Priya Verma
3. Rahul Singh
Enter Candidate ID: 1
Output: ✅ Your vote has been securely recorded.
```

#### Step 6: End Election
```
Menu Option: 6
Output: 🔴 Election has ended.
```

#### Step 7: View Results
```
Menu Option: 7
Output:
📊 Final Results:
Amit Sharma : 5
Priya Verma : 3
Rahul Singh : 2

🏆 Winner: Amit Sharma
```

#### Step 8: View Audit Logs
```
Menu Option: 8
Output:
📜 Audit Log (Secure & Anonymous):
Election Started at [TIMESTAMP]
Voter Registered at [TIMESTAMP]
Token Issued at [TIMESTAMP]
Vote Cast at [TIMESTAMP]
...
```

---

## Data Flow

```
┌─────────────────┐
│  Officer Login  │
└────────┬────────┘
         │ (Authenticate)
         ▼
┌──────────────────┐
│ Start Election   │
│ (Status: ONGOING)│
└────────┬─────────┘
         │
    ┌────┴────┬──────────┬────────────────┐
    │          │          │                │
    ▼          ▼          ▼                ▼
┌────────┐ ┌───────┐ ┌────────┐    ┌─────────────┐
│Register│ │Issue  │ │ Cast   │    │End Election │
│ Voter  │ │Token  │ │ Vote   │    │(Status: END)│
└────────┘ └───────┘ └────────┘    └─────────────┘
    │          │          │
    └──────────┴──────────┘
         │
         ▼
    ┌─────────────┐
    │View Results │
    └─────────────┘
         │
         ▼
    ┌─────────────┐
    │View Audit   │
    │Logs         │
    └─────────────┘
```

---

## OOP Concepts Used

### 1. **Inheritance**
```java
class ElectionOfficer extends Person
class Voter extends Person
```
- Code reusability
- Common person properties
- Polymorphic behavior

### 2. **Encapsulation**
```java
private String password;      // Protected attribute
private List<String> logs;    // Hidden implementation
public void record(String action)  // Controlled access
```
- Data hiding
- Controlled access through methods
- Security and integrity

### 3. **Abstraction**
```java
abstract class Person
abstract methods define contract
```
- Hiding complex logic
- Simplified interface
- Focus on "what" not "how"

### 4. **Polymorphism**
```java
Person p = new Voter(...);
Person p = new ElectionOfficer(...);
```
- Different behaviors for same interface
- Runtime type determination

### 5. **Collections Framework**
```java
Map<String, Voter> voters                // HashMap
List<Candidate> candidates               // ArrayList
Set<String> validTokens                  // HashSet
```
- Efficient data management
- Built-in operations

### 6. **Immutability**
```java
final class Vote {
    private final String token;          // Cannot change
    private final String candidateId;
}
```
- Thread-safe
- Prevents tampering
- Data integrity

### 7. **Composition**
```java
class ElectionCore {
    private Ballot ballot;
    private TokenService tokenService;
    private AuditLog audit;
}
```
- "Has-a" relationship
- Flexible component combining

### 8. **Enums**
```java
enum ElectionStatus { NOT_STARTED, ONGOING, ENDED }
enum Role { VOTER, ADMIN }
```
- Type-safe constants
- Prevents invalid states
- Self-documenting code

---

## System Improvements & Considerations

### Current Limitations:
1. **In-Memory Storage:** Data lost when program exits (no persistence)
2. **Plain Text Passwords:** Should use hashing in production
3. **No Network:** Single-machine only, not distributed
4. **Single Officer:** Only one officer can be logged in

### Recommended Enhancements:
1. **Database Integration:** MySQL/PostgreSQL for persistence
2. **Password Hashing:** Use BCrypt or similar
3. **Network/API:** Enable remote voting
4. **Multi-Officer Support:** Multiple concurrent officers
5. **UI:** Graphical interface (JavaFX/Swing)
6. **Encryption:** End-to-end encryption for sensitive data
7. **Unit Tests:** JUnit for testing
8. **Error Handling:** Custom exceptions

---

## Conclusion

The Digital Election System demonstrates core OOP principles through a practical, real-world application. It provides a foundation for understanding secure voting mechanisms while showcasing proper use of inheritance, encapsulation, collections, and state management.

**Key Takeaway:** This system proves that proper object-oriented design can create secure, maintainable, and easily extensible applications suitable for critical infrastructure like electoral processes.

---

*Documentation Version: 1.0*  
*Last Updated: 2026*
