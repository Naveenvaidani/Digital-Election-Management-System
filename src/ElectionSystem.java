import java.util.*;

// ================= ENUMS =================
enum ElectionStatus {
    NOT_STARTED, ONGOING, ENDED
}

enum Role {
    VOTER, ADMIN
}

// ================= ABSTRACT PERSON =================
abstract class Person {
    protected String id;
    protected String name;
    protected Role role;

    public Person(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}

// ================= ELECTION OFFICER =================
class ElectionOfficer extends Person {
    private String password;

    public ElectionOfficer(String id, String name, String password) {
        super(id, name, Role.ADMIN);
        this.password = password;
    }

    public boolean authenticate(String pass) {
        return password.equals(pass);
    }
}

// ================= VOTER =================
class Voter extends Person {
    private boolean eligible;
    private boolean tokenIssued;

    public Voter(String id, String name) {
        super(id, name, Role.VOTER);
        this.eligible = true;
        this.tokenIssued = false;
    }

    public boolean isEligible() {
        return eligible;
    }

    public boolean hasToken() {
        return tokenIssued;
    }

    public void issueToken() {
        tokenIssued = true;
    }
}

// ================= CANDIDATE =================
class Candidate {
    private String id;
    private String name;
    private int votes;

    public Candidate(String id, String name) {
        this.id = id;
        this.name = name;
        this.votes = 0;
    }

    public void addVote() {
        votes++;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }
}

// ================= IMMUTABLE VOTE =================
final class Vote {
    private final String token;
    private final String candidateId;

    public Vote(String token, String candidateId) {
        this.token = token;
        this.candidateId = candidateId;
    }

    public String getCandidateId() {
        return candidateId;
    }
}

// ================= TOKEN SERVICE =================
class TokenService {
    private Set<String> validTokens = new HashSet<>();

    public String generateToken() {
        String token = UUID.randomUUID().toString();
        validTokens.add(token);
        return token;
    }

    public boolean isValid(String token) {
        return validTokens.contains(token);
    }

    public void invalidate(String token) {
        validTokens.remove(token);
    }
}

// ================= AUDIT LOG =================
class AuditLog {
    private List<String> logs = new ArrayList<>();

    public void record(String action) {
        logs.add(action + " at " + new Date());
    }

    public void showLogs() {
        System.out.println("\n📜 Audit Log (Secure & Anonymous):");
        for (String log : logs) {
            System.out.println(log);
        }
    }
}

// ================= BALLOT =================
class Ballot {
    private List<Candidate> candidates;

    public Ballot(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public void displayCandidates() {
        System.out.println("\n🗳️ Available Candidates:");
        for (Candidate c : candidates) {
            System.out.println(c.getId() + ". " + c.getName());
        }
    }

    public Candidate findCandidate(String id) {
        for (Candidate c : candidates) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    public List<Candidate> getAll() {
        return candidates;
    }
}

// ================= ELECTION SYSTEM =================
class ElectionCore {

    private Map<String, Voter> voters = new HashMap<>();
    private Map<String, ElectionOfficer> officers = new HashMap<>();
    private List<Vote> votes = new ArrayList<>();

    private Ballot ballot;
    private TokenService tokenService = new TokenService();
    private AuditLog audit = new AuditLog();

    private ElectionStatus status = ElectionStatus.NOT_STARTED;
    private ElectionOfficer loggedInOfficer = null;

    public ElectionCore(Ballot ballot) {
        this.ballot = ballot;

        // Default Indian-style admin
        officers.put("admin", new ElectionOfficer("admin", "Rajesh Kumar (Chief Election Officer)", "admin123"));
    }

    // ================= ADMIN LOGIN =================
    public void login(String id, String password) {
        ElectionOfficer officer = officers.get(id);

        if (officer != null && officer.authenticate(password)) {
            loggedInOfficer = officer;
            System.out.println("✅ Welcome " + officer.name);
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    // ================= START =================
    public void startElection() {
        if (loggedInOfficer == null) {
            System.out.println("⚠️ Please login as Election Officer first.");
            return;
        }

        status = ElectionStatus.ONGOING;
        audit.record("Election Started");
        System.out.println("🟢 Election has started.");
    }

    // ================= END =================
    public void endElection() {
        if (loggedInOfficer == null) {
            System.out.println("⚠️ Unauthorized!");
            return;
        }

        status = ElectionStatus.ENDED;
        audit.record("Election Ended");
        System.out.println("🔴 Election has ended.");
    }

    // ================= REGISTER =================
    public void registerVoter(String id, String name) {
        voters.put(id, new Voter(id, name));
        audit.record("Voter Registered");
        System.out.println("✅ Voter " + name + " registered successfully.");
    }

    // ================= TOKEN =================
    public String issueToken(String voterId) {
        Voter v = voters.get(voterId);

        if (v == null) {
            System.out.println("❌ Voter not found.");
            return null;
        }

        if (v.hasToken()) {
            System.out.println("⚠️ Token already issued.");
            return null;
        }

        String token = tokenService.generateToken();
        v.issueToken();

        audit.record("Token Issued");
        return token;
    }

    // ================= VOTE =================
    public void castVote(String token, String candidateId) {

        if (status != ElectionStatus.ONGOING) {
            System.out.println("⚠️ Election is not active.");
            return;
        }

        if (!tokenService.isValid(token)) {
            System.out.println("❌ Invalid or used token.");
            return;
        }

        Candidate c = ballot.findCandidate(candidateId);

        if (c == null) {
            System.out.println("❌ Invalid candidate.");
            return;
        }

        votes.add(new Vote(token, candidateId));
        c.addVote();
        tokenService.invalidate(token);

        audit.record("Vote Cast");
        System.out.println("✅ Your vote has been securely recorded.");
    }

    // ================= RESULTS =================
    public void showResults() {
        if (status != ElectionStatus.ENDED) {
            System.out.println("⚠️ Election not completed yet.");
            return;
        }

        System.out.println("\n📊 Final Results:");
        Candidate winner = null;

        for (Candidate c : ballot.getAll()) {
            System.out.println(c.getName() + " : " + c.getVotes());

            if (winner == null || c.getVotes() > winner.getVotes()) {
                winner = c;
            }
        }

        System.out.println("\n🏆 Winner: " + winner.getName());
    }

    public void showLogs() {
        if (loggedInOfficer == null) {
            System.out.println("⚠️ Unauthorized access.");
            return;
        }
        audit.showLogs();
    }
}

// ================= MAIN =================
public class ElectionSystem {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<Candidate> candidates = Arrays.asList(
                new Candidate("1", "Amit Sharma"),
                new Candidate("2", "Priya Verma"),
                new Candidate("3", "Rahul Singh")
        );

        Ballot ballot = new Ballot(candidates);
        ElectionCore system = new ElectionCore(ballot);

        while (true) {

            System.out.println("\n====== 🇮🇳 DIGITAL ELECTION SYSTEM ======");
            System.out.println("1. Election Officer Login");
            System.out.println("2. Start Election");
            System.out.println("3. Register Voter");
            System.out.println("4. Get Voting Token");
            System.out.println("5. Cast Vote");
            System.out.println("6. End Election");
            System.out.println("7. View Results");
            System.out.println("8. View Audit Logs");
            System.out.println("9. Exit");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {

                case 1:
                    System.out.print("Officer ID: ");
                    String id = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();
                    system.login(id, pass);
                    break;

                case 2:
                    system.startElection();
                    break;

                case 3:
                    System.out.print("Enter Voter ID: ");
                    String vid = sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    system.registerVoter(vid, name);
                    break;

                case 4:
                    System.out.print("Enter Voter ID: ");
                    String v = sc.nextLine();
                    String token = system.issueToken(v);
                    if (token != null)
                        System.out.println("🔐 Your Secure Token: " + token);
                    break;

                case 5:
                    System.out.print("Enter Token: ");
                    String t = sc.nextLine();
                    ballot.displayCandidates();
                    System.out.print("Enter Candidate ID: ");
                    String cid = sc.nextLine();
                    system.castVote(t, cid);
                    break;

                case 6:
                    system.endElection();
                    break;

                case 7:
                    system.showResults();
                    break;

                case 8:
                    system.showLogs();
                    break;

                case 9:
                    System.exit(0);
            }
        }
    }
}
