import json

def create_integrity_test_file(port, ip, target_team, description, command_list, filename="integrity_test.json"):
    test_file = {
        "type": "integrity",
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
    port = 4000
    ip = "127.0.0.1"
    target_team = 9
    description = "MITM alters the deposit amount from 50 to 5000 undetected."

    command_list = [
        ["-a", "4444", "-n", "10.00"],        
        ["-a", "4444", "-d", "50.00"],          
        ["-a", "4444", "-g"],                  
    ]

    create_integrity_test_file(port, ip, target_team, description, command_list)