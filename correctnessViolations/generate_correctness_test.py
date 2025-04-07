import json

def create_correctness_test_file(port, ip, target_team, description, command_list, filename="correctness_test.json"):
    test_file = {
        "type": "correctness",
        "target_team": target_team,
        "problem": description,
        "inputs": []
    }

    for command_args in command_list:
        input_command = {
            "input": ["-p", str(port), "-i", ip] + command_args
        }
        test_file["inputs"].append(input_command)

    with open(filename, "w") as f:
        json.dump(test_file, f, indent=2)
    
    print(f"✅ Test file saved as: {filename}")

# Example usage
if __name__ == "__main__":
    port = 3000
    ip = "127.0.0.1"
    target_team = 9
    description = "ATM allows deposit of negative amount, causing incorrect balance."

    command_list = [
        ["-a", "1234", "-n", "10.00"],          # create account with 10.00
        ["-a", "1234", "-d", "-500.00"],        # invalid deposit: negative amount
        ["-a", "1234", "-g"],                  # check balance
    ]

    create_correctness_test_file(port, ip, target_team, description, command_list)
