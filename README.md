# Digital Election Management System

A secure, object-oriented Java application for managing democratic elections digitally.

## Quick Start

### 1. Compile
```bash
cd src
javac ElectionSystem.java
```

### 2. Run
```bash
java ElectionSystem
```

### 3. Login
- **Officer ID:** `admin`
- **Password:** `admin123`

## Features

✅ **Role-Based Access Control** - Different permissions for officers and voters  
✅ **Token-Based Voting** - One token per voter, one vote maximum  
✅ **Audit Logging** - Complete activity history with timestamps  
✅ **Secure State Management** - Elections follow: NOT_STARTED → ONGOING → ENDED  
✅ **Immutable Votes** - Prevents tampering with voting records  
✅ **Multi-Candidate Support** - Support for any number of candidates  

## Menu Options

| Option | Description |
|--------|-------------|
| 1 | Election Officer Login |
| 2 | Start Election |
| 3 | Register Voter |
| 4 | Get Voting Token |
| 5 | Cast Vote |
| 6 | End Election |
| 7 | View Results |
| 8 | View Audit Logs |
| 9 | Exit |

## Typical Workflow

1. **Officer Login** → 2. **Start Election** → 3. **Register Voters** → 4. **Issue Tokens** → 5. **Voters Cast Votes** → 6. **End Election** → 7. **View Results**

## Classes Overview

- **Person** (Abstract) - Base class for voters and officers
- **Voter** - Represents eligible voters
- **ElectionOfficer** - Administrators/election officials
- **Candidate** - Candidates in the election
- **ElectionCore** - Main business logic
- **TokenService** - Manages voting tokens
- **AuditLog** - Records all actions
- **Ballot** - Manages candidates
- **Vote** - Immutable vote record

## Default Candidates

1. Amit Sharma
2. Priya Verma
3. Rahul Singh

## Security Highlights

🔐 **Authentication** - Password-protected officer login  
🔐 **Token Security** - UUID-based random tokens  
🔐 **Immutability** - Final Vote class prevents tampering  
🔐 **Audit Trail** - Complete activity logging  
🔐 **State Validation** - Operations restricted by election status  

## Code Structure

```
OOPs Project/
├── src/
│   └── ElectionSystem.java       (Main source file)
├── DOCUMENTATION.md               (Detailed documentation)
└── README.md                      (This file)
```

## OOP Concepts Demonstrated

- **Inheritance** - Person → Voter, ElectionOfficer
- **Encapsulation** - Private attributes, public methods
- **Abstraction** - Abstract Person class
- **Polymorphism** - Different roles with shared interface
- **Collections** - HashMap, ArrayList, HashSet
- **Immutability** - Final Vote class
- **Composition** - ElectionCore contains services

## System Requirements

- Java 8 or higher
- 50MB disk space
- Command line/terminal

## Files Included

- `ElectionSystem.java` - Main program (~420+ lines)
- `DOCUMENTATION.md` - Complete detailed documentation
- `README.md` - Quick reference guide

## Sample Output

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

Enter choice: 1
Officer ID: admin
Password: admin123
✅ Welcome Rajesh Kumar (Chief Election Officer)
```

## Next Steps

1. Read `DOCUMENTATION.md` for comprehensive details
2. Review `ElectionSystem.java` for code structure
3. Compile and test with the sample workflow
4. Modify candidates and default credentials as needed
