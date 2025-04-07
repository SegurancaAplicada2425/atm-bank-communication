# 🧾 Full Report – Security Against MITM Attacks

## 🔐 ATM-Bank Secure Communication Protocol — Security Report

### 📘 Introduction
This report evaluates the security of a custom communication protocol between an ATM and a bank server against three common classes of Man-in-the-Middle (MITM) attacks: confidentiality, integrity, and correctness. A simulated MITM proxy was developed to intercept and alter messages, and various attack scenarios were tested.

## Attack Scenarios and Results

### 1. 🕵️ Confidentiality Attack
**Objective:** Attempt to read message content as it travels through the network.

**Method:** The MITM proxy logs the raw message. It does not possess the keys or secrets needed to decrypt them.

**Observed Behavior:**
- Messages intercepted by the MITM appear as encrypted blobs (e.g., long hex strings or base64)
- No sensitive information (like account numbers, balances, or commands) is visible
- Even with the `--confidentiality` flag, the MITM failed to retrieve useful data

**Conclusion:** ✅ Confidentiality is preserved. The protocol uses encryption to protect message contents. Without access to keys, eavesdropping reveals no secrets.

### 2. ✍️ Integrity Attack
**Objective:** Modify a message in transit and trick the recipient into accepting it.

**Method:** The MITM proxy attempts to flip bits or modify ciphertext.

**Observed Behavior:**
- Modified messages cause the receiver to terminate the connection or discard the message
- No tampered messages were accepted or processed

**Explanation:** This indicates that the messages include cryptographic integrity checks, such as:
- Message Authentication Codes (MAC)
- Authenticated Encryption (e.g., AES-GCM)
- Digital Signatures

Even the slightest tampering causes the cryptographic check to fail, and the message is rejected.

**Conclusion:** ✅ Integrity is preserved. The protocol ensures messages can't be modified without detection.

### 3. 🔄 Correctness Attack
**Objective:** Replay, reorder, or drop messages, aiming to confuse the protocol.

**Method:** MITM intercepts and reorders or drops packets.

**Observed Behavior:**
- ATM fails to complete handshake and aborts with:


- The server logs show message reordering attempts but reject the connection due to protocol mismatch

**Explanation:** This suggests the protocol enforces session state, message ordering, or includes nonces/timestamps that prevent replay or out-of-order messages from succeeding.

**Conclusion:** ✅ Correctness is preserved. The protocol resists desynchronization or state confusion caused by MITM interference.

## 🔐 Final Verdict
Your protocol demonstrates strong resilience to:

| Attack Type     | Resilience Level | Protection Mechanism                    |
|-----------------|------------------|----------------------------------------|
| Confidentiality | ✅ Strong        | Encryption (e.g., AES)                  |
| Integrity       | ✅ Strong        | MACs / Authenticated Encryption         |
| Correctness     | ✅ Strong        | Session management, Nonces, Ordering    |

## 📚 Appendix: Explanation of Each Attack

### 🔒 1. Confidentiality Attack
- **Goal:** Spy on the conversation between two parties
- **Defensive Strategy:** Use encryption (e.g., TLS, AES), so intercepted messages are unreadable

### ✍️ 2. Integrity Attack
- **Goal:** Modify a message in transit (e.g., change withdrawal amount)
- **Defensive Strategy:** Use MACs or Authenticated Encryption to detect tampering

### 🔁 3. Correctness Attack
- **Goal:** Replay old messages, reorder them, or drop them, hoping to desynchronize or exploit logic bugs
- **Defensive Strategy:** Use sequence numbers, nonces, and ensure every message is fresh and expected