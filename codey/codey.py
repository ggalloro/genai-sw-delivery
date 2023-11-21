import argparse
import sys
import os
from google.cloud import aiplatform
from vertexai.preview.language_models import CodeChatModel

# Define the path to the file containing code to be processed.
PATH = "/workspace/diff.txt"

# Define the common parameters for the chat model.
COMMON_PARAMETERS = {
    "temperature": 0.5,
    "max_output_tokens": 1024,
}

# Get the CodeChat model.
def codechat_bison_model():
    return CodeChatModel.from_pretrained("codechat-bison@001").start_chat()

# Chat with the model.
def chat_with_model(chat, query):
    response = chat.send_message(query, **COMMON_PARAMETERS)
    return response.text

# Process the code file.
def process_code_file():
    if not os.path.exists(PATH):
        print(f"🚩 Error 🚩: The file {PATH} does not exist.")
        sys.exit(1)
    with open(PATH, 'r') as file:
        data = file.read().replace('\n', '')
    return data

# Generate documentation for the code.
def generate_documentation():
    chat = codechat_bison_model()
    query = "Document the following code " + process_code_file()
    response = chat_with_model(chat, query)
    print(f"Response from Model: {response}")

# Generate release notes for the code.
def generate_release_notes():
    chat = codechat_bison_model()
    query = "Write release notes for " + process_code_file()
    response = chat_with_model(chat, query)
    with open("release_notes.md", "w") as release_notes_file:
        release_notes_file.write(response)
    print("The generated release notes are:\n\n" + response)

# Generate a function.
def generate_function():
    chat = codechat_bison_model()
    query = "Please help write a function to calculate the min of two numbers"
    response = chat_with_model(chat, query)
    print(f"Response from Model: {response}")

# Generate optimization for the code.
def generate_optimization():
    chat = codechat_bison_model()
    query = "Please optimize this code " + process_code_file()
    response = chat_with_model(chat, query)
    print("A possible optimization is:\n\n" + response)

# Generate security optimization for the code.
def generate_security_optimization():
    chat = codechat_bison_model()
    query = "Please optimize this code to make it more secure " + process_code_file()
    response = chat_with_model(chat, query)
    print("A possible security optimization is:\n\n" + response)

# Define the main function.
def main():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers()

    parser_documentation = subparsers.add_parser('documentation', help='Generate documentation for provided code')
    parser_documentation.set_defaults(func=generate_documentation)
    
    parser_release_notes = subparsers.add_parser('release-notes', help='Generate Release notes')
    parser_release_notes.set_defaults(func=generate_release_notes)

    parser_function = subparsers.add_parser('write-a-function', help='Generate a function')
    parser_function.set_defaults(func=generate_function)

    parser_optimize = subparsers.add_parser('optimize', help='Look at code and suggest optimization')
    parser_optimize.set_defaults(func=generate_optimization)

    parser_security_optimize = subparsers.add_parser('optimize-security', help='Look at code and suggest security optimization')
    parser_security_optimize.set_defaults(func=generate_security_optimization)

    if len(sys.argv) <= 1:
        sys.argv.append('--help')

    options = parser.parse_args()
    options.func()

# Run the main function.
if __name__ == '__main__':
    main()
