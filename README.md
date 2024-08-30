# Password-Management-Tool
A user-friendly password management tool designed to securely store, manage, and retrieve online account credentials through robust encryption. This tool includes features like a password generator, encrypted storage, and a straightforward graphical user interface, ensuring enhanced digital security for users of all technical levels.

## Key Components

The project consists of several core components:

- **Password.java**: Defines the structure for storing password information, including the URL, username, and password.
- **PasswordVault.java**: Manages a collection of passwords, providing functionality to add, remove, and update passwords.
- **GUI.java**: Provides a user-friendly graphical interface for managing your passwords.
- **FileIO.java**: Handles reading and writing password data to and from files.
- **CryptoUtils.java**: Utilizes AES/GCM encryption to securely encrypt and decrypt password data.
- **PasswordUtils.java**: Includes tools for generating secure passwords and assessing password strength.

## Key Features

- **AES/GCM Encryption**: Ensures that all password data is securely encrypted, keeping your information safe.
- **Password Generator**: Allows you to generate strong, random passwords with customizable options for length, case, digits, and symbols.
- **Password Strength Meter**: Provides real-time feedback on the security level of your passwords, helping you create stronger credentials.
- **User-Friendly Interface**: A clean and intuitive graphical interface makes managing passwords simple and efficient.

## Usage

1. **Launching the Application**: 
   - Choose to create a new password file or open an existing one.
   - For a new file, set a file name and master password, which is required to access the file.

2. **Managing Passwords**:
   - View all stored passwords along with usernames, URLs, edit times, and password strength.
   - Add, delete, modify, search, and save password entries.
   - Right-click to copy passwords easily.

3. **Security Measures**:
   - The file is encrypted when closed, and all sensitive data is securely erased after use.

## Copyright

© 2024 CC Li. All rights reserved.


