# USC Tree Hole Android Application

## Overview

USC Tree Hole is an Android application developed to help students, faculty, and staff at the University of Southern California connect with each other. The app allows users to create and interact with posts across various categories—**Academic**, **Life**, and **Event**—and engage in conversations through replies, including anonymous interactions.

## Team Information

- **Team Number:** 55
- **Team Name:** N/A
- **Team Members:**
  - Marie Fotso, fotso@usc.edu
  - Zach Dodson, zdodson@usc.edu
  - Steven Xiao, stevenxi@usc.edu

## Prerequisites

- **Android Studio:** Version 4.0 or higher.
- **Firebase Account:** Ensure you have a Firebase project set up with Authentication, Realtime Database, Storage, and Cloud Messaging enabled.
- **Internet Connection:** Required for Firebase services.

## Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/m-fotso/CSCI-310-Project-2-Tree-Hole.git
   ```

2. **Open in Android Studio:**
   - Launch Android Studio.
   - Click on `Open an existing Android Studio project`.
   - Navigate to the cloned repository folder and select it.

3. **Configure Firebase:**
   - The `google-services.json` file is already included in the repository under the `app/` directory. Ensure that it remains in this location for Firebase integration.

4. **Sync Project:**
   - In Android Studio, click on `Sync Project with Gradle Files` to ensure all dependencies are correctly set up.

5. **DNS Configuration (If Required):**
   - Some Firebase services may require specific DNS settings. If you encounter connectivity issues, consider adding the following DNS servers to your machine's network settings:
     - **Primary DNS:** `8.8.8.8`
     - **Secondary DNS:** `8.8.4.4`

   - **How to Add DNS Servers:**
     - **Windows:**
       1. Go to `Control Panel` > `Network and Internet` > `Network and Sharing Center`.
       2. Click on your active network connection.
       3. Click `Properties`.
       4. Select `Internet Protocol Version 4 (TCP/IPv4)` and click `Properties`.
       5. Choose `Use the following DNS server addresses` and enter `8.8.8.8` and `8.8.4.4`.
       6. Click `OK` to save changes.
     - **macOS:**
       1. Go to `System Preferences` > `Network`.
       2. Select your active network connection and click `Advanced`.
       3. Navigate to the `DNS` tab.
       4. Click the `+` button and add `8.8.8.8` and `8.8.4.4`.
       5. Click `OK` and then `Apply` to save changes.

6. **Run the Application:**
   - Connect an Android device or start an emulator.
   - Click on the `Run` button or press `Shift + F10` to build and deploy the app.

## Usage

1. **Account Creation and Login:**
   - Launch the app and navigate to the login screen.
   - If you don't have an account, click on the `Sign Up` option to create one.
   - After successful registration, log in with your credentials.

2. **Profile Management:**
   - Access your profile via the `Profile` tab on top right.
   - Edit your details, including uploading a profile photo.

3. **Creating and Interacting with Posts:**
   - Navigate to any of the three categories: Academic, Life, Event.
   - Create new posts using the plus icon bottom right, choose to post anonymously if desired.
   - Reply to posts and other replies, maintaining consistent anonymity within posts.

4. **Subscriptions and Notifications:**
   - Subscribe to categories to receive notifications for new posts.
   - Ensure that your device allows notifications for the app.

