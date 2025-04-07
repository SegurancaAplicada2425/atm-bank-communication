import json
import subprocess
import os
import signal
import time

def run_test(json_path, atm_jar_path, mitm_script_path, port_mitm, ip, port_bank):
    with open(json_path, "r") as f:
        test_case = json.load(f)

    print(f"🚀 Running integrity test: {test_case['problem']}\n")

    # Step 1: Launch MITM
    print("🛰️ Starting MITM...")
    mitm_proc = subprocess.Popen(
        ["python3", mitm_script_path, "-p", str(port_mitm), "-s", ip, "-q", str(port_bank)],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    time.sleep(1)  # Give MITM a second to boot up

    # Step 2: Run ATM commands (pointing to MITM)
    for i, cmd in enumerate(test_case["inputs"]):
        args = cmd["input"]
        # Replace placeholders
        args = [arg.replace("%PORT%", str(port_mitm)).replace("%IP%", ip) for arg in args]

        full_cmd = ["java", "-jar", atm_jar_path] + args
        print(f"\n▶️ Command {i+1}: {' '.join(full_cmd)}")

        try:
            result = subprocess.run(full_cmd, capture_output=True, text=True, timeout=10)
            stdout = result.stdout.strip()
            stderr = result.stderr.strip()
            exit_code = result.returncode

            print("STDOUT:\n", stdout)
            print("STDERR:\n", stderr)
            print("Exit Code:", exit_code)

            if "Exception" in stderr or "NullPointer" in stderr or "Traceback" in stderr:
                print("❌ Crash detected — correctness violation.")
            elif exit_code != 0 and "invalid" not in stderr.lower() and "insufficient" not in stderr.lower() and "already exists" not in stderr.lower():
                print("❌ Unexpected error behavior — might be a correctness violation.")
            else:
                print("✅ Error handled properly — no correctness issue.")

            # Optional expected output match
            if "expected_output" in cmd:
                expected = cmd["expected_output"].strip()
                if stdout != expected:
                    print("❌ Output mismatch!")
                    print("Expected:", expected)
                    print("Got:", stdout)

        except subprocess.TimeoutExpired:
            print("❌ ATM client timed out.")
        except Exception as e:
            print(f"❌ Error running ATM client: {e}")

    # Step 3: Clean up MITM
    print("\n🧹 Shutting down MITM...")
    mitm_proc.send_signal(signal.SIGTERM)
    try:
        mitm_stdout, mitm_stderr = mitm_proc.communicate(timeout=3)
        print("📄 MITM STDOUT:\n", mitm_stdout)
        print("📄 MITM STDERR:\n", mitm_stderr)
    except subprocess.TimeoutExpired:
        print("⚠️ MITM didn’t shut down in time, killing it.")
        mitm_proc.kill()


if __name__ == "__main__":
    atm_jar_path = "../../atm-bank-communication/build/libs/atm-1.0.0-all.jar"
    mitm_script_path = "mitm_integrity.py"
    json_path = "integrity_test.json"

    port_mitm = 4000  # MITM listens here (ATM connects here)
    port_bank = 3000  # Bank listens here
    ip = "127.0.0.1"

    run_test(json_path, atm_jar_path, mitm_script_path, port_mitm, ip, port_bank)
