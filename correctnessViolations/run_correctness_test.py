import json
import subprocess
import os

def run_test(json_path, atm_jar_path, port, ip):
    with open(json_path, "r") as f:
        test_case = json.load(f)

    print(f"🚀 Running correctness test: {test_case['problem']}\n")

    for i, cmd in enumerate(test_case["inputs"]):
        args = cmd["input"]
        # Replace placeholders
        args = [arg.replace("%PORT%", str(port)).replace("%IP%", ip) for arg in args]

        full_cmd = ["java", "-jar", atm_jar_path] + args
        print(f"▶️ Command {i+1}: {' '.join(full_cmd)}")

        try:
            result = subprocess.run(full_cmd, capture_output=True, text=True, timeout=10)
            stdout = result.stdout.strip()
            stderr = result.stderr.strip()
            exit_code = result.returncode

            print("STDOUT:\n", stdout)
            print("STDERR:\n", stderr)
            print("Exit Code:", exit_code)
            print("-" * 40)

            # Simple crash or error detection
            # This will mark real crashes or unexpected behavior
            if "Exception" in stderr or "NullPointer" in stderr or "Traceback" in stderr:
                print("❌ Crash detected — correctness violation.")
            elif exit_code != 0 and "invalid" not in stderr.lower() and "insufficient" not in stderr.lower() and "already exists" not in stderr.lower():
                print("❌ Unexpected error behavior — might be a correctness violation.")
            else:
                print("✅ Error handled properly — no correctness issue.")


            # Optional: You can add expected_output to the JSON later and check here
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

if __name__ == "__main__":
    # You're in ./build/libs already!
    atm_jar_path = "../build/libs/atm-1.0.0-all.jar"
    json_path = "correctness_test.json"  # Place your JSON in the main project root
    port = 3000
    ip = "127.0.0.1"

    run_test(json_path, atm_jar_path, port, ip)
