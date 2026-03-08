# HackSecure Messenger - Complete Setup and Build Guide

**Complete guide from absolute zero to running app** - Install everything from scratch and run the HackSecure Messenger application on Android emulator.

This guide assumes you have **NOTHING installed** and will walk you through every single step with expected outputs, screenshots descriptions, and troubleshooting for every stage.

---

## Table of Contents

1. [System Requirements](#1-system-requirements)
2. [Install Java JDK 17](#2-install-java-jdk-17)
3. [Install Node.js](#3-install-nodejs-18)
4. [Install Android Studio & SDK](#4-install-android-studio--sdk)
5. [Setup Android Emulator](#5-setup-android-emulator)
6. [Setup Environment Variables](#6-setup-environment-variables)
7. [Setup Relay Server](#7-setup-relay-server)
8. [Configure Android Application](#8-configure-android-application)
9. [Build the Application](#9-build-the-application)
10. [Deploy to Emulator](#10-deploy-to-android-emulator)
11. [Testing with Two Devices](#11-testing-with-two-devicesemulators)
12. [Troubleshooting](#12-troubleshooting)
13. [Quick Reference](#13-quick-reference)
14. [Automation Script](#14-complete-automation-script)

---

## 1. System Requirements

### Minimum Hardware
- **RAM:** 8 GB (16 GB recommended for smooth emulator performance)
- **Storage:** 15 GB free space (20 GB recommended, SSD strongly recommended)
- **Processor:** Intel i5 / AMD Ryzen 5 or better (with virtualization support)
- **Internet:** Stable connection for downloading ~5 GB of dependencies

### Supported Operating Systems
- Windows 10/11 (64-bit only)
- macOS 10.14 (Mojave) or later
- Linux: Ubuntu 20.04+, Debian 11+, Fedora 33+

### Before You Start
- Close all unnecessary applications to free up RAM
- Connect to power (this will take 30-60 minutes)
- Ensure you have administrator/sudo access

---

## 2. Install Java JDK 17

Java Development Kit (JDK) is required to run Gradle, which builds Android applications.

### 2.1 Windows Installation

#### Step 1: Download Java JDK 17

**Option A: Oracle JDK (Official)**
1. Open your web browser
2. Go to: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
3. Accept the license agreement
4. Download: **Windows x64 Installer** (`jdk-17_windows-x64_bin.exe`)
   - File size: ~160 MB

**Option B: Microsoft OpenJDK (Recommended)**
1. Go to: https://learn.microsoft.com/en-us/java/openjdk/download
2. Download: **Microsoft Build of OpenJDK 17**
3. Choose: **Windows x64 MSI** (~180 MB)

#### Step 2: Install Java

1. **Locate the downloaded file** in your Downloads folder
2. **Double-click** the installer (`jdk-17_windows-x64_bin.exe` or `.msi`)
3. **User Account Control** will prompt - click "Yes"
4. **Installation wizard opens:**
   - Click **"Next"**
   - Installation path will be: `C:\Program Files\Java\jdk-17` or `C:\Program Files\Microsoft\jdk-17.x.x`
   - **Note this path** - you'll need it later
   - Click **"Next"** → **"Install"**
   - Wait 2-3 minutes for installation
   - Click **"Close"** when finished

#### Step 3: Verify Installation

1. **Open Command Prompt or Git Bash:**
   - Press `Win + R`
   - Type `cmd` and press Enter
   - **OR** open Git Bash if you have it

2. **Run the verification command:**
   ```bash
   java -version
   ```

3. **Expected Output:**
   ```
   openjdk version "17.0.10" 2024-01-16 LTS
   OpenJDK Runtime Environment Microsoft-8902769 (build 17.0.10+7-LTS)
   OpenJDK 64-Bit Server VM Microsoft-8902769 (build 17.0.10+7-LTS, mixed mode, sharing)
   ```

4. **If you see this output**, Java is installed correctly. Proceed to Step 4.

5. **If you see "java is not recognized"**, you need to set environment variables manually (see Step 4).

#### Step 4: Set JAVA_HOME Environment Variable

1. **Open Environment Variables dialog:**
   - Right-click **"This PC"** on desktop or in File Explorer
   - Click **"Properties"**
   - Click **"Advanced system settings"** on the left
   - Click **"Environment Variables"** button at the bottom

2. **Create JAVA_HOME variable:**
   - In the **"System variables"** section (bottom half), click **"New..."**
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** `C:\Program Files\Java\jdk-17` (or your actual JDK path)
   - Click **"OK"**

3. **Add to PATH:**
   - Find **"Path"** variable in **"System variables"**
   - Click **"Edit..."**
   - Click **"New"**
   - Add: `%JAVA_HOME%\bin`
   - Click **"OK"** on all dialogs

4. **Verify (open NEW terminal):**
   - Close all Command Prompt/Git Bash windows
   - Open a NEW Command Prompt
   - Run: `java -version` and `javac -version`
   - Both should work now

---

### 2.2 macOS Installation

#### Method 1: Homebrew (Recommended)

1. **Install Homebrew** (if not already installed):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
   - This takes 5-10 minutes
   - Follow any additional instructions shown after installation

2. **Install OpenJDK 17:**
   ```bash
   brew install openjdk@17
   ```

3. **Link JDK to system:**
   ```bash
   sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
   ```

4. **Add to PATH** (add to `~/.zshrc` or `~/.bash_profile`):
   ```bash
   echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
   ```

5. **Verify:**
   ```bash
   java -version
   ```

   **Expected Output:**
   ```
   openjdk version "17.0.10" 2024-01-16
   OpenJDK Runtime Environment Homebrew (build 17.0.10+0)
   OpenJDK 64-Bit Server VM Homebrew (build 17.0.10+0, mixed mode, sharing)
   ```

#### Method 2: Manual Download

1. **Download JDK:**
   - Go to: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
   - Download: **macOS x64 DMG** (~180 MB)

2. **Install:**
   - Open the `.dmg` file
   - Double-click the package installer
   - Follow the installation wizard

3. **Verify:**
   ```bash
   java -version
   ```

---

### 2.3 Linux Installation (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk openjdk-17-jre -y

# Verify installation
java -version
javac -version
```

**Expected Output:**
```
openjdk version "17.0.10" 2024-01-16
OpenJDK Runtime Environment (build 17.0.10+7-Ubuntu-1ubuntu1~22.04)
OpenJDK 64-Bit Server VM (build 17.0.10+7-Ubuntu-1ubuntu1~22.04, mixed mode, sharing)
```

**Set JAVA_HOME:**
```bash
# Find Java installation path
sudo update-alternatives --config java
# Note the path (usually /usr/lib/jvm/java-17-openjdk-amd64)

# Add to ~/.bashrc or ~/.zshrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify
echo $JAVA_HOME
```

---

## 3. Install Node.js 18+

Node.js is required to run the HackSecure relay server.

### 3.1 Windows Installation

#### Step 1: Download Node.js

1. Open browser and go to: https://nodejs.org/
2. **Download the LTS version** (Long Term Support)
   - Look for green button: "20.x.x LTS Recommended For Most Users"
   - Click to download `node-v20.11.0-x64.msi` (~30 MB)

#### Step 2: Install Node.js

1. **Run the installer** from Downloads folder
2. **Setup wizard:**
   - **Welcome screen** → Click "Next"
   - **License Agreement** → Check "I accept" → Click "Next"
   - **Destination Folder** → Keep default `C:\Program Files\nodejs\` → Click "Next"
   - **Custom Setup** → Keep all default selections → Click "Next"
   - **Tools for Native Modules** → Uncheck (not needed) → Click "Next"
   - Click **"Install"**
   - Wait 2-3 minutes
   - Click **"Finish"**

#### Step 3: Verify Installation

1. **Open NEW Command Prompt or Git Bash**
2. **Check Node.js version:**
   ```bash
   node --version
   ```
   **Expected Output:**
   ```
   v20.11.0
   ```

3. **Check npm version:**
   ```bash
   npm --version
   ```
   **Expected Output:**
   ```
   10.2.4
   ```

4. **If both commands work**, Node.js is installed correctly!

---

### 3.2 macOS Installation

#### Method 1: Homebrew (Recommended)

```bash
# Install Node.js LTS
brew install node

# Verify
node --version
npm --version
```

#### Method 2: Download Installer

1. Go to: https://nodejs.org/
2. Download **macOS Installer (.pkg)** for LTS version
3. Open the downloaded `.pkg` file
4. Follow installation wizard
5. Verify:
   ```bash
   node --version
   npm --version
   ```

---

### 3.3 Linux Installation (Ubuntu/Debian)

```bash
# Add NodeSource repository for Node.js 20.x
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -

# Install Node.js
sudo apt-get install -y nodejs

# Verify
node --version
npm --version
```

**Expected Output:**
```
v20.11.0
10.2.4
```

---

## 4. Install Android Studio & SDK

Android Studio includes the Android SDK, build tools, and emulator needed to build and run Android apps.

### 4.1 Download Android Studio

#### All Platforms

1. **Open browser** and go to: https://developer.android.com/studio
2. **Download Android Studio:**
   - **Windows:** Click "Download Android Studio" button
     - File: `android-studio-2023.2.1.23-windows.exe` (~1.1 GB)
   - **macOS:**
     - Intel: `android-studio-2023.2.1.23-mac.dmg`
     - Apple Silicon: `android-studio-2023.2.1.23-mac_arm.dmg`
   - **Linux:** `android-studio-2023.2.1.23-linux.tar.gz` (~1.1 GB)

3. **Accept Terms and Conditions** and download

---

### 4.2 Install Android Studio

#### Windows:

1. **Run the installer** (`android-studio-xxxx.exe`)
2. **Installation wizard:**
   - **Welcome** → Click "Next"
   - **Choose Components:**
     - ✅ Android Studio
     - ✅ Android Virtual Device
     - Click "Next"
   - **Configuration Settings:** Keep defaults → Click "Next"
   - **Install Location:** Keep default `C:\Program Files\Android\Android Studio\` → Click "Next"
   - Click **"Install"**
   - Wait 5-10 minutes for installation
   - ✅ Check "Start Android Studio" → Click "Finish"

#### macOS:

1. **Open the DMG file**
2. **Drag Android Studio icon** to the **Applications** folder
3. **Open Android Studio** from Applications
4. **First time:** Right-click and select "Open" (to bypass security)
5. Click **"Open"** in the security dialog

#### Linux:

```bash
# Extract archive to /opt
sudo tar -xvzf android-studio-*-linux.tar.gz -C /opt/

# Launch Android Studio
cd /opt/android-studio/bin
./studio.sh
```

---

### 4.3 Android Studio Setup Wizard

When you launch Android Studio for the first time, a setup wizard will guide you:

#### Step 1: Import Settings
- **Screen:** "Import Android Studio Settings"
- **Select:** "Do not import settings"
- Click **"OK"**

#### Step 2: Data Sharing
- **Screen:** "Data Sharing"
- Choose your preference (you can click "Don't send")
- Click **"Next"**

#### Step 3: Welcome
- **Screen:** "Welcome to Android Studio"
- Click **"Next"**

#### Step 4: Install Type
- **Screen:** "Install Type"
- **Select:** "Standard" (Recommended)
- Click **"Next"**

#### Step 5: Select UI Theme
- **Screen:** "Select UI Theme"
- Choose "Light" or "Darcula" (dark theme)
- Click **"Next"**

#### Step 6: Verify Settings
- **Screen:** "Verify Settings"
- **Important:** Note the SDK Location shown here:
  - **Windows:** `C:\Users\YourUsername\AppData\Local\Android\Sdk`
  - **macOS:** `/Users/YourUsername/Library/Android/sdk`
  - **Linux:** `/home/YourUsername/Android/Sdk`
- **Write down this path** - you'll need it later!
- **Download Size:** Shows ~3-5 GB will be downloaded
- Click **"Next"**

#### Step 7: Downloading Components
- **Screen:** "Downloading Components"
- This downloads:
  - Android SDK Platform-Tools
  - Android SDK Build-Tools
  - Android Emulator
  - Android SDK Platform (latest API)
- **Time:** 10-20 minutes depending on internet speed
- **Progress bar** will show download and installation progress
- Click **"Finish"** when complete

#### Step 8: Welcome Screen
- **Screen:** "Welcome to Android Studio"
- You'll see options:
  - New Project
  - Open
  - Get from VCS
- **Don't create a project yet** - we need to install more SDK components

---

### 4.4 Install Required SDK Components

#### Step 1: Open SDK Manager

1. On the **Welcome screen**, click **"More Actions"** (three dots) → **"SDK Manager"**
2. **OR** if you're in a project: **Tools** → **SDK Manager** (from menu bar)

#### Step 2: SDK Platforms Tab

1. In SDK Manager, you'll see two tabs: **"SDK Platforms"** and **"SDK Tools"**
2. Make sure you're on **"SDK Platforms"** tab
3. **Check the box** for:
   - ✅ **Android 14.0 ("UpsideDownCake") - API Level 34**
   - ✅ **Android 13.0 ("Tiramisu") - API Level 33** (optional, but recommended)
4. Click **"Show Package Details"** at bottom right
5. Expand **Android 14.0 (API 34)** and verify these are checked:
   - ✅ Android SDK Platform 34
   - ✅ Sources for Android 34
   - ✅ Google APIs Intel x86_64 Atom System Image (for emulator)
6. Click **"Apply"**
7. **Confirm Change** dialog appears → Click "OK"
8. **License Agreement** dialog:
   - Click each unchecked license in the list
   - Click **"Accept"** for each
   - Click **"Next"**
9. **Downloading and installing** (5-10 minutes)
10. Click **"Finish"** when done

#### Step 3: SDK Tools Tab

1. Click **"SDK Tools"** tab
2. **Check these boxes:**
   - ✅ **Android SDK Build-Tools 34.0.0** (should already be installed)
   - ✅ **Android SDK Platform-Tools** (should already be installed)
   - ✅ **Android Emulator** (should already be installed)
   - ✅ **Android SDK Command-line Tools (latest)**
   - ✅ **Google Play Services** (optional)
3. Click **"Apply"** → **"OK"**
4. Accept licenses if prompted
5. Wait for download and installation (3-5 minutes)
6. Click **"Finish"** → **"OK"**

---

### 4.5 Accept Android SDK Licenses

This step is crucial for command-line builds.

#### Windows:

**Method 1: Using sdkmanager (if cmdline-tools installed)**
```cmd
cd %LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest\bin
sdkmanager --licenses
```

**Method 2: Manual (if above fails)**
```cmd
cd %LOCALAPPDATA%\Android\Sdk
mkdir licenses
cd licenses
echo 24333f8a63b6825ea9c5514f83c2829b004d1fee > android-sdk-license
echo d56f5187479451eabf01fb78af6dfcb131a6481e >> android-sdk-license
```

#### macOS / Linux:

```bash
# Navigate to SDK directory
cd ~/Library/Android/sdk/cmdline-tools/latest/bin  # macOS
# OR
cd ~/Android/Sdk/cmdline-tools/latest/bin          # Linux

# Accept all licenses
./sdkmanager --licenses
```

**You'll see multiple licenses:**
- Type `y` and press **Enter** for each license
- Repeat until you see: "All SDK package licenses accepted"

**Expected Output:**
```
7 of 7 SDK package licenses not accepted.
Review licenses that have not been accepted (y/N)? y
...
Accept? (y/N): y
...
All SDK package licenses accepted
```

---

## 5. Setup Android Emulator

An Android emulator is a virtual Android device that runs on your computer.

### 5.1 Open AVD Manager

1. On **Welcome screen**: Click **"More Actions"** → **"Virtual Device Manager"**
2. **OR** in any project: **Tools** → **Device Manager**

### 5.2 Create Virtual Device

#### Step 1: Select Hardware

1. **Screen:** "Select Hardware"
2. **Category:** Phone (already selected)
3. **Choose a device:**
   - **Recommended:** **Pixel 5** (good balance of features)
   - **Alternative:** Pixel 4, Pixel 6, Nexus 5X
4. Click **"Next"**

#### Step 2: Select System Image

1. **Screen:** "System Image"
2. **Recommended tab** shows recommended system images
3. **Choose one of these:**
   - **UpsideDownCake (API 34)** - x86_64 - latest Android 14
   - **Tiramisu (API 33)** - x86_64 - Android 13
4. **If "Download" link appears:**
   - Click **"Download"** next to the system image
   - **License Agreement** → Click "Accept" → "Next"
   - Wait for download (~1-2 GB, takes 5-15 minutes)
   - Click **"Finish"** when download completes
5. **Select the downloaded system image**
6. **Important:** Choose **x86_64** architecture (not ARM) for better performance
7. Click **"Next"**

#### Step 3: Verify Configuration

1. **Screen:** "Android Virtual Device (AVD)"
2. **AVD Name:** Keep default or change to something like `Pixel_5_API_34`
3. **Startup orientation:** Portrait
4. Click **"Show Advanced Settings"** (optional but recommended)
5. **Advanced Settings:**
   - **RAM:** 2048 MB (or more if you have 16+ GB system RAM)
   - **VM heap:** 512 MB
   - **Internal Storage:** 2048 MB
   - **SD card:** 512 MB (Studio-managed)
   - **Emulated Performance:**
     - **Graphics:** Hardware - GLES 2.0 (or Automatic)
   - Leave other settings as default
6. Click **"Finish"**

#### Step 4: Verify AVD Created

- You'll see your new virtual device in the list
- Device name: "Pixel 5 API 34" or whatever you named it
- **Actions column** has ▶ (Play), ✎ (Edit), ⬇ (Download), 🗑 (Delete) buttons

---

### 5.3 Enable Hardware Acceleration

Hardware acceleration makes the emulator run much faster.

#### Windows (Intel CPU):

**Intel HAXM should be installed automatically with Android Studio.**

**To verify or install manually:**

1. Check if HAXM is enabled:
   ```cmd
   sc query intelhaxm
   ```
   - If running, you're good!

2. If not installed:
   - Download from: https://github.com/intel/haxm/releases
   - Download: `haxm-windows_v7.8.0.zip`
   - Extract and run `intelhaxm-android.exe`
   - Follow installer

#### Windows (AMD CPU):

1. **Enable Windows Hypervisor Platform:**
   - Press `Win + R`
   - Type `optionalfeatures` and press Enter
   - Scroll down and check:
     - ✅ **Windows Hypervisor Platform**
     - ✅ **Virtual Machine Platform**
   - Click **"OK"**
   - **Restart your computer** when prompted

2. **Verify in BIOS:**
   - Restart computer
   - Enter BIOS (usually F2, Del, or F12 during startup)
   - Find and enable **AMD-V** or **SVM Mode**
   - Save and exit BIOS

#### macOS:

- **Intel Macs:** Hypervisor.framework is built-in, no setup needed
- **Apple Silicon (M1/M2):** Use ARM-based system images, works natively

#### Linux (Ubuntu/Debian):

```bash
# Check if CPU supports virtualization
egrep -c '(vmx|svm)' /proc/cpuinfo
# If output is > 0, KVM is supported

# Install KVM
sudo apt-get install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils -y

# Add user to KVM group
sudo adduser $USER kvm
sudo adduser $USER libvirt

# Verify KVM is loaded
lsmod | grep kvm

# Reboot to apply changes
sudo reboot
```

---

### 5.4 Start the Emulator

#### Method 1: From Device Manager (GUI)

1. Open **Device Manager** in Android Studio
2. Find your virtual device in the list
3. Click the **▶ (Play)** button
4. **Wait 2-5 minutes** for first boot (subsequent boots are faster)
5. **Expected:** Emulator window opens showing Android lock screen/home screen

#### Method 2: From Command Line

```bash
# List available emulators
emulator -list-avds

# Start emulator (replace with your AVD name)
emulator -avd Pixel_5_API_34 &
```

**On Windows, you may need full path:**
```cmd
%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe -avd Pixel_5_API_34
```

---

### 5.5 Verify Emulator is Running

```bash
adb devices
```

**Expected Output:**
```
List of devices attached
emulator-5554	device
```

**If you see this, your emulator is running and connected!**

**If you see `offline` or `unauthorized`:**
- Wait 30 seconds and try again
- Click "Allow" on the emulator if prompted
- Restart adb: `adb kill-server && adb start-server`

---

## 6. Setup Environment Variables

Environment variables tell your system where to find Android SDK tools.

### 6.1 Windows Setup

#### Step 1: Find Your Android SDK Path

**Default locations:**
- `C:\Users\YourUsername\AppData\Local\Android\Sdk`

**To verify:**
1. Open File Explorer
2. In address bar, paste: `%LOCALAPPDATA%\Android\Sdk`
3. If folder opens, this is your SDK path
4. **Copy this full path**

#### Step 2: Open Environment Variables

1. Right-click **"This PC"** → **Properties**
2. Click **"Advanced system settings"** (left sidebar)
3. Click **"Environment Variables"** button

#### Step 3: Create ANDROID_HOME Variable

1. In **"System variables"** section (lower half), click **"New..."**
2. **Variable name:** `ANDROID_HOME`
3. **Variable value:** `C:\Users\YourUsername\AppData\Local\Android\Sdk`
   - **Replace YourUsername** with your actual username
4. Click **"OK"**

#### Step 4: Update PATH Variable

1. In **"System variables"**, find and select **"Path"**
2. Click **"Edit..."**
3. Click **"New"** and add these paths one by one:
   ```
   %ANDROID_HOME%\platform-tools
   %ANDROID_HOME%\emulator
   %ANDROID_HOME%\cmdline-tools\latest\bin
   %ANDROID_HOME%\tools
   ```
4. Click **"OK"** on all dialogs

#### Step 5: Verify (IMPORTANT: Open NEW terminal)

**Close all existing Command Prompt/Git Bash windows, then open a NEW one:**

```bash
# Check ANDROID_HOME is set
echo %ANDROID_HOME%
# Should output: C:\Users\YourUsername\AppData\Local\Android\Sdk

# Check adb is accessible
adb version
# Should output: Android Debug Bridge version 1.0.41

# Check emulator is accessible
emulator -version
# Should output: Android emulator version...
```

**If any command fails:**
- Make sure you opened a NEW terminal
- Verify the paths in environment variables are correct
- Restart your computer if needed

---

### 6.2 macOS / Linux Setup

#### macOS:

**Edit your shell profile** (`~/.zshrc` for Zsh or `~/.bash_profile` for Bash):

```bash
# Open the file in a text editor
nano ~/.zshrc

# Add these lines at the end:
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/emulator

# Save and exit (Ctrl+O, Enter, Ctrl+X in nano)

# Apply changes
source ~/.zshrc
```

#### Linux:

```bash
# Edit ~/.bashrc
nano ~/.bashrc

# Add these lines at the end:
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/emulator

# Save and exit

# Apply changes
source ~/.bashrc
```

#### Verify:

```bash
# Check ANDROID_HOME
echo $ANDROID_HOME
# Should output: /Users/YourName/Library/Android/sdk (macOS)
#            or: /home/YourName/Android/Sdk (Linux)

# Check adb
adb version

# Check emulator
emulator -version
```

---

## 7. Setup Relay Server

The HackSecure relay server routes encrypted messages between clients without inspecting content.

### 7.1 Navigate to Project

First, we need to navigate to the project directory.

**Windows (Git Bash or Command Prompt):**
```bash
cd /c/Users/arung/OneDrive/Desktop/HackSecureMessenger_v1.0.2_fixed/HackSecureMessenger
```

**macOS / Linux:**
```bash
cd ~/path/to/HackSecureMessenger
```

**Verify you're in the right directory:**
```bash
ls
```

**Expected Output:**
```
app/  build.gradle.kts  gradle/  gradlew  gradlew.bat  local.properties  README.md  server/  settings.gradle.kts
```

### 7.2 Install Server Dependencies

```bash
cd server
npm install
```

**Expected Output:**
```
npm WARN deprecated <some packages may show warnings>
...
added 95 packages, and audited 96 packages in 8s

12 packages are looking for funding
  run `npm fund` for details

found 0 vulnerabilities
```

**Time:** 1-2 minutes

**If errors occur:**
- Make sure you're in the `server/` directory: `pwd` (should end with `/server`)
- Make sure Node.js is installed: `node --version`
- Check internet connection

### 7.3 Start the Relay Server

```bash
node index.js
```

**Expected Output:**
```
⚠️  NEW SERVER KEYPAIR GENERATED
   Hardcode this public key in BuildConfig.SERVER_PUBLIC_KEY_HEX:
   83d11e5ec0ac2a183e092f7541c3cf8c2bbdea3a23a460a40a9cd50e4cf2e002

🔒 HackSecure Relay Server v1.0.0
   Listening on port 8443
   REST: http://localhost:8443/api/v1/
   WebSocket: ws://localhost:8443/ws?conv=<conversationId>
```

**IMPORTANT: Copy the Server Public Key**

- Find the line with the hex string (64 characters)
- **Copy this entire string**: `83d11e5ec0ac2a183e092f7541c3cf8c2bbdea3a23a460a40a9cd50e4cf2e002`
- Save it in a text file - you'll need it in the next step
- **NOTE:** Your key will be DIFFERENT from this example

**The server is now running!**

- Keep this terminal window open
- The server must stay running while testing the app
- To stop: Press `Ctrl+C`

### 7.4 Run Server in Background (Optional)

If you want to run the server in the background:

**Linux/Mac:**
```bash
nohup node index.js > server.log 2>&1 &
echo $! > server.pid
```

**To stop background server:**
```bash
kill $(cat server.pid)
```

**Windows (Git Bash):**
```bash
node index.js &
```

### 7.5 Test Server is Running

**Open a NEW terminal** (keep the server terminal running), and test:

```bash
curl http://localhost:8443/api/v1/health
```

**Expected Output:**
```json
{"status":"ok","version":"1.0.0"}
```

**If this works, your server is running correctly!**

---

## 8. Configure Android Application

### 8.1 Navigate to Project Root

```bash
# Go back to project root
cd /c/Users/arung/OneDrive/Desktop/HackSecureMessenger_v1.0.2_fixed/HackSecureMessenger
```

### 8.2 Verify Gradle Wrapper Files

Check if these files exist:

```bash
ls -la gradle/wrapper/
ls -la gradlew*
```

**You should see:**
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradlew` (Linux/Mac)
- `gradlew.bat` (Windows)

**If any are missing, create them:**

#### Create gradle-wrapper.properties

```bash
mkdir -p gradle/wrapper
cat > gradle/wrapper/gradle-wrapper.properties << 'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF
```

#### Download gradle-wrapper.jar

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
```

### 8.3 Create/Update local.properties

This file tells Gradle where your Android SDK is located.

**IMPORTANT:** Use your actual SDK path from Step 6.

**Windows:**
```bash
cat > local.properties << 'EOF'
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
EOF
```

**Replace `YourUsername` with your actual Windows username.**

**Alternatively, use ANDROID_HOME:**
```bash
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

**macOS:**
```bash
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
```

**Linux:**
```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

**Verify the file:**
```bash
cat local.properties
```

**Expected Output (Windows example):**
```
sdk.dir=C\:\\Users\\arung\\AppData\\Local\\Android\\Sdk
```

### 8.4 Create/Update gradle.properties

```bash
cat > gradle.properties << 'EOF'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
org.gradle.caching=true
EOF
```

### 8.5 Update Server Public Key in build.gradle.kts

**Open the file** `app/build.gradle.kts` in a text editor.

**Find these lines** (around line 33-39):

```kotlin
buildConfigField(
    "String",
    "SERVER_PUBLIC_KEY_HEX",
    "\"83d11e5ec0ac2a183e092f7541c3cf8c2bbdea3a23a460a40a9cd50e4cf2e002\""
)
buildConfigField("String", "RELAY_BASE_URL", "\"ws://10.0.2.2:8443\"")
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8443\"")
```

**Replace the SERVER_PUBLIC_KEY_HEX value** with the key you copied from Step 7.3.

**Important Notes:**
- Keep the `\"` quotes around the key
- `10.0.2.2` is the special IP that Android emulator uses to access host machine's `localhost`
- If testing on a **physical Android device** connected to same WiFi:
  - Find your computer's IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
  - Replace `10.0.2.2` with your actual IP (e.g., `192.168.1.100`)

**Save the file.**

### 8.6 Make Gradle Wrapper Executable (Linux/Mac only)

```bash
chmod +x gradlew
chmod +x gradlew.bat
```

### 8.7 Accept Android SDK Licenses (if not done already)

```bash
# Windows
%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat --licenses

# macOS/Linux
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
```

Type `y` for all licenses.

---

## 9. Build the Application

### 9.1 Clean Build (Recommended First Time)

```bash
./gradlew clean
```

**On Windows, if `./gradlew` doesn't work:**
```cmd
gradlew.bat clean
```

**Expected Output:**
```
> Task :clean UP-TO-DATE

BUILD SUCCESSFUL in 3s
1 actionable task: 1 up-to-date
```

### 9.2 Build Debug APK

This is the main build command:

```bash
./gradlew assembleDebug
```

**What happens:**
1. Gradle downloads dependencies (~5 minutes first time)
2. Compiles Kotlin code
3. Processes resources
4. Runs code generation (Hilt, Room)
5. Packages everything into an APK

**Expected Output (shortened):**
```
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:compileDebugKotlin
> Task :app:kaptGenerateStubsDebugKotlin
> Task :app:kaptDebugKotlin
> Task :app:compileDebugJavaWithJavac
> Task :app:mergeDebugResources
> Task :app:processDebugManifest
> Task :app:packageDebug
> Task :app:assembleDebug

BUILD SUCCESSFUL in 4m 23s
145 actionable tasks: 145 executed
```

**Time:**
- First build: 5-15 minutes (downloading dependencies)
- Subsequent builds: 1-3 minutes

**If successful, the APK is created at:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Verify APK exists:**
```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

**Expected Output:**
```
-rw-r--r-- 1 user user 15M Mar 8 14:30 app/build/outputs/apk/debug/app-debug.apk
```

### 9.3 Common Build Issues

#### Issue: "JAVA_HOME is not set"

**Solution:**
```bash
# Check Java version
java -version

# Set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew assembleDebug

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
gradlew.bat assembleDebug
```

#### Issue: "SDK location not found"

**Solution:** Check `local.properties` file exists and has correct path.

#### Issue: "Execution failed for task ':app:kaptDebugKotlin'"

**Solution:**
```bash
./gradlew clean
rm -rf .gradle app/build
./gradlew assembleDebug
```

#### Issue: Build hangs at "Downloading..."

**Solution:** Check internet connection, wait (first build downloads ~1 GB of dependencies).

---

## 10. Deploy to Android Emulator

### 10.1 Start Emulator (if not running)

**Check if emulator is already running:**
```bash
adb devices
```

**If no devices listed, start emulator:**

**Method 1: From Android Studio**
1. Open Android Studio
2. **Tools** → **Device Manager**
3. Click ▶ (Play) button next to your virtual device
4. Wait 2-3 minutes for emulator to boot

**Method 2: From Command Line**
```bash
# List available AVDs
emulator -list-avds

# Start emulator
emulator -avd Pixel_5_API_34 &
```

**Wait until you see Android home screen in emulator window.**

### 10.2 Verify Device Connection

```bash
adb devices
```

**Expected Output:**
```
List of devices attached
emulator-5554	device
```

**Status meanings:**
- `device` - Connected and ready (good!)
- `offline` - Not ready yet (wait 30 seconds)
- `unauthorized` - Click "Allow" on emulator screen
- No devices - Emulator not running or adb issue

**If no devices shown:**
```bash
adb kill-server
adb start-server
adb devices
```

### 10.3 Install APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Expected Output:**
```
Performing Streamed Install
Success
```

**Time:** 5-15 seconds

**If you see "INSTALL_FAILED_UPDATE_INCOMPATIBLE":**
```bash
# Uninstall old version first
adb uninstall com.hacksecure.messenger.debug

# Then install again
adb install app/build/outputs/apk/debug/app-debug.apk
```

**If you want to reinstall without uninstalling:**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 10.4 Launch the Application

```bash
adb shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity
```

**Expected Output:**
```
Starting: Intent { cmp=com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity }
```

**You should now see the HackSecure Messenger app open on the emulator!**

**Expected app screens:**
1. **First launch:** "Welcome to HackSecure Messenger" or identity setup screen
2. **Identity generation:** Create your secure identity
3. **Home screen:** Empty conversation list

### 10.5 Verify App is Running

```bash
# Check current focused app
adb shell "dumpsys window | grep mCurrentFocus"
```

**Expected Output:**
```
mCurrentFocus=Window{abc1234 u0 com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity}
```

**Check installed packages:**
```bash
adb shell pm list packages | grep hacksecure
```

**Expected Output:**
```
package:com.hacksecure.messenger.debug
```

### 10.6 View Application Logs

**Real-time logs:**
```bash
adb logcat
```

**Filter for HackSecure logs only:**
```bash
adb logcat | grep "HackSecure"
```

**Filter for errors only:**
```bash
adb logcat *:E *:W
```

**Clear logs before starting:**
```bash
adb logcat -c
adb logcat
```

**Save logs to file:**
```bash
adb logcat > app_logs.txt
```

**To stop viewing logs:** Press `Ctrl+C`

---

## 11. Testing with Two Devices/Emulators

To test peer-to-peer encrypted messaging, you need two instances of the app running.

### 11.1 Start Second Emulator

**Method 1: From Android Studio**
1. **Device Manager** → Click ▶ on a different virtual device
2. Wait for it to boot

**Method 2: From Command Line**
```bash
# Create second AVD first (if you don't have one)
# Then start it:
emulator -avd Pixel_4_API_33 &
```

### 11.2 Verify Both Emulators Connected

```bash
adb devices
```

**Expected Output:**
```
List of devices attached
emulator-5554	device
emulator-5556	device
```

**Note the device IDs:** `emulator-5554` and `emulator-5556`

### 11.3 Install App on Both Emulators

```bash
# Install on first emulator
adb -s emulator-5554 install app/build/outputs/apk/debug/app-debug.apk

# Install on second emulator
adb -s emulator-5556 install app/build/outputs/apk/debug/app-debug.apk
```

**Expected Output for each:**
```
Performing Streamed Install
Success
```

### 11.4 Launch App on Both Devices

```bash
# Launch on first emulator
adb -s emulator-5554 shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity

# Launch on second emulator
adb -s emulator-5556 shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity
```

### 11.5 Testing Messaging Flow

#### Device 1 (Alice):

1. **Open app** - should auto-generate identity
2. **Tap "Show QR Code"** or equivalent button
3. **QR code is displayed** with your public identity

#### Device 2 (Bob):

1. **Open app** - should auto-generate identity
2. **Tap "Add Contact"** or "Scan QR"**
3. **Scan the QR code** from Device 1's screen
   - You may need to position emulator windows side-by-side
   - Or use screenshots: `adb -s emulator-5554 exec-out screencap -p > device1_qr.png`
4. **Contact is added**

#### Send Messages:

1. **On Device 2:** Select Alice's contact
2. **Type a message:** "Hello from Bob!"
3. **Send**
4. **On Device 1:** You should receive the encrypted message
5. **Reply from Device 1** to test two-way communication

#### Test Encryption:

- Messages are end-to-end encrypted
- Server cannot read message content
- Check logs to see encryption in action:

```bash
# Device 1 logs
adb -s emulator-5554 logcat | grep -E "HackSecure|Encrypt|Decrypt"

# Device 2 logs
adb -s emulator-5556 logcat | grep -E "HackSecure|Encrypt|Decrypt"
```

### 11.6 Test Self-Destruct Messages

1. **Send a message** with expiry time (e.g., 30 seconds)
2. **Wait for expiry**
3. **Message should disappear** from both devices
4. **Check storage:** Message is overwritten and deleted

---

## 12. Troubleshooting

### 12.1 Server Connection Issues

#### Problem: "Failed to connect to server"

**Diagnosis:**
```bash
# Test server from host machine
curl http://localhost:8443/api/v1/health

# Test from emulator's perspective
adb shell "curl http://10.0.2.2:8443/api/v1/health"
```

**Solutions:**

1. **Check server is running:**
   ```bash
   # Look for node process
   ps aux | grep "node index.js"

   # Check port 8443 is listening
   netstat -an | grep 8443
   ```

2. **Restart server:**
   ```bash
   cd server
   node index.js
   ```

3. **Check firewall:**
   - **Windows:** Windows Defender Firewall → Allow an app → Allow Node.js on port 8443
   - **macOS:** System Preferences → Security & Privacy → Firewall → Firewall Options → Allow Node
   - **Linux:** `sudo ufw allow 8443` or `sudo iptables -A INPUT -p tcp --dport 8443 -j ACCEPT`

4. **Check network configuration in app/build.gradle.kts:**
   - For emulator: Use `10.0.2.2:8443`
   - For physical device: Use your computer's LAN IP (e.g., `192.168.1.100:8443`)

#### Problem: "Connection timed out"

**Solution:** Make sure server is using `0.0.0.0` not `localhost` for binding. Check `server/index.js`.

---

### 12.2 Build Failures

#### Problem: "Execution failed for task ':app:compileDebugKotlin'"

**Solution 1: Clean rebuild**
```bash
./gradlew clean
rm -rf .gradle
rm -rf app/build
./gradlew assembleDebug --stacktrace
```

**Solution 2: Check Java version**
```bash
java -version
# Should be 17.x.x

# If wrong version, set JAVA_HOME correctly
export JAVA_HOME=/path/to/jdk-17
```

#### Problem: "SDK location not found"

**Solution:** Create/fix `local.properties`:
```bash
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Verify
cat local.properties
```

#### Problem: "Failed to install the following SDK components"

**Solution:**
```bash
# Accept all licenses
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# Install missing components
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

#### Problem: "Gradle sync failed"

**Solution:**
```bash
# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.4

# Sync again
./gradlew assembleDebug
```

---

### 12.3 ADB Connection Issues

#### Problem: "adb: command not found"

**Solution:**
```bash
# Check PATH includes platform-tools
echo $PATH | grep platform-tools

# Add to PATH if missing (Linux/Mac)
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Windows: Add to environment variables (see Section 6)
```

#### Problem: "error: device offline"

**Solution:**
```bash
# Restart adb server
adb kill-server
adb start-server

# Wait 30 seconds
adb devices
```

#### Problem: "error: device unauthorized"

**Solution:**
1. Look at emulator screen
2. You should see a popup: "Allow USB debugging?"
3. Check "Always allow from this computer"
4. Click "OK"
5. Run `adb devices` again

#### Problem: Multiple devices but want to target one

**Solution:**
```bash
# Use -s flag with device ID
adb -s emulator-5554 install app-debug.apk
adb -s emulator-5556 logcat
```

---

### 12.4 Emulator Issues

#### Problem: Emulator won't start

**Solution:**

1. **Check virtualization is enabled:**
   - **Windows:** Task Manager → Performance → CPU → Virtualization should be "Enabled"
   - **Linux:** `egrep -c '(vmx|svm)' /proc/cpuinfo` should be > 0

2. **Try cold boot:**
   - Device Manager → Click ⬇ next to emulator → "Cold Boot Now"

3. **Wipe data:**
   - Device Manager → Click ⬇ → "Wipe Data"
   - Creates a fresh emulator instance

4. **Check available disk space:**
   ```bash
   df -h
   ```
   - Need at least 5 GB free

#### Problem: Emulator is very slow

**Solutions:**

1. **Increase RAM allocation:**
   - Edit AVD → Show Advanced Settings → RAM: 4096 MB

2. **Enable hardware acceleration** (see Section 5.3)

3. **Use x86_64 system image** (not ARM)

4. **Close other applications** to free RAM

5. **Disable animations:**
   ```bash
   adb shell settings put global window_animation_scale 0
   adb shell settings put global transition_animation_scale 0
   adb shell settings put global animator_duration_scale 0
   ```

---

### 12.5 App Crashes

#### Problem: App crashes on launch

**Check logs:**
```bash
adb logcat *:E
```

**Common causes:**

1. **Missing BuildConfig values:**
   - Check `app/build.gradle.kts` has `SERVER_PUBLIC_KEY_HEX` set correctly

2. **Server not running:**
   - Start server: `cd server && node index.js`

3. **Permission issues:**
   - Check AndroidManifest.xml has required permissions

4. **Database migration issues:**
   ```bash
   # Clear app data
   adb shell pm clear com.hacksecure.messenger.debug

   # Reinstall
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

#### Problem: "App not installed"

**Solutions:**
```bash
# Method 1: Uninstall old version
adb uninstall com.hacksecure.messenger.debug
adb install app/build/outputs/apk/debug/app-debug.apk

# Method 2: Reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Method 3: Check storage space
adb shell df /data
```

---

### 12.6 Node.js Server Issues

#### Problem: "npm install" fails

**Solutions:**

1. **Check Node.js version:**
   ```bash
   node --version
   # Should be >= 18.0.0
   ```

2. **Clear npm cache:**
   ```bash
   npm cache clean --force
   npm install
   ```

3. **Delete node_modules and reinstall:**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **Check internet connection**

#### Problem: "Error: listen EADDRINUSE"

**Solution:** Port 8443 is already in use.

**Find and kill process:**
```bash
# Linux/Mac
lsof -ti:8443 | xargs kill

# Windows
netstat -ano | findstr :8443
taskkill /PID <PID> /F
```

---

## 13. Quick Reference

### Essential Commands

```bash
# ══════════════════════════════════════════════════════════════
# SERVER
# ══════════════════════════════════════════════════════════════
cd server
npm install                    # Install dependencies (first time)
node index.js                  # Start server
nohup node index.js &          # Start in background (Linux/Mac)

# Test server
curl http://localhost:8443/api/v1/health

# ══════════════════════════════════════════════════════════════
# BUILD
# ══════════════════════════════════════════════════════════════
./gradlew clean                # Clean build artifacts
./gradlew assembleDebug        # Build debug APK
./gradlew assembleRelease      # Build release APK
./gradlew assembleDebug --info # Build with verbose output
./gradlew test                 # Run unit tests

# ══════════════════════════════════════════════════════════════
# EMULATOR
# ══════════════════════════════════════════════════════════════
emulator -list-avds            # List available virtual devices
emulator -avd Pixel_5_API_34 & # Start emulator

# ══════════════════════════════════════════════════════════════
# ADB
# ══════════════════════════════════════════════════════════════
adb devices                    # List connected devices
adb kill-server                # Restart adb
adb start-server

# ══════════════════════════════════════════════════════════════
# INSTALL & LAUNCH
# ══════════════════════════════════════════════════════════════
# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Reinstall (keep data)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity

# ══════════════════════════════════════════════════════════════
# UNINSTALL & CLEAR DATA
# ══════════════════════════════════════════════════════════════
adb uninstall com.hacksecure.messenger.debug  # Uninstall
adb shell pm clear com.hacksecure.messenger.debug  # Clear data

# ══════════════════════════════════════════════════════════════
# LOGS
# ══════════════════════════════════════════════════════════════
adb logcat                     # All logs
adb logcat -c                  # Clear logs
adb logcat | grep "HackSecure" # Filter by tag
adb logcat *:E *:W             # Errors and warnings only
adb logcat > logs.txt          # Save to file

# ══════════════════════════════════════════════════════════════
# DEBUGGING
# ══════════════════════════════════════════════════════════════
# Check app is running
adb shell "dumpsys window | grep mCurrentFocus"

# Check installed packages
adb shell pm list packages | grep hacksecure

# View app info
adb shell pm dump com.hacksecure.messenger.debug

# View app data directory
adb shell run-as com.hacksecure.messenger.debug ls -la /data/data/com.hacksecure.messenger.debug/

# Take screenshot
adb exec-out screencap -p > screenshot.png

# ══════════════════════════════════════════════════════════════
# MULTIPLE DEVICES
# ══════════════════════════════════════════════════════════════
adb devices                    # List devices
adb -s emulator-5554 install app-debug.apk  # Install on specific device
adb -s emulator-5556 logcat    # Logs from specific device

# ══════════════════════════════════════════════════════════════
# ENVIRONMENT
# ══════════════════════════════════════════════════════════════
# Check Java version
java -version                  # Should be 17.x.x

# Check Node version
node --version                 # Should be >= 18.x.x

# Check Android SDK
echo $ANDROID_HOME             # Should point to SDK directory
adb version                    # Should work
```

---

### Important File Locations

```
PROJECT STRUCTURE:
C:/Users/arung/OneDrive/Desktop/HackSecureMessenger_v1.0.2_fixed/HackSecureMessenger/
├── server/                              # Relay server
│   ├── index.js                         # Server code
│   └── package.json                     # Node dependencies
├── app/
│   ├── build.gradle.kts                 # App build config (SERVER_PUBLIC_KEY_HEX here)
│   ├── src/main/
│   │   ├── java/com/hacksecure/messenger/
│   │   └── AndroidManifest.xml
│   └── build/outputs/apk/debug/
│       └── app-debug.apk                # ★ Generated APK (after build)
├── build.gradle.kts                     # Root build config
├── settings.gradle.kts                  # Gradle settings
├── local.properties                     # ★ SDK location
├── gradle.properties                    # ★ Gradle JVM settings
└── gradlew / gradlew.bat                # Gradle wrapper scripts

APK OUTPUT:
app/build/outputs/apk/debug/app-debug.apk

ANDROID SDK (Windows):
C:\Users\YourUsername\AppData\Local\Android\Sdk\

ANDROID SDK (macOS):
/Users/YourUsername/Library/Android/sdk/

ANDROID SDK (Linux):
/home/YourUsername/Android/Sdk/
```

---

### Network Configuration

| Scenario | SERVER_PUBLIC_KEY_HEX | RELAY_BASE_URL | API_BASE_URL |
|----------|----------------------|----------------|--------------|
| **Android Emulator** | (from server startup) | `ws://10.0.2.2:8443` | `http://10.0.2.2:8443` |
| **Physical Device (same WiFi)** | (from server startup) | `ws://192.168.1.X:8443` | `http://192.168.1.X:8443` |
| **Production (TLS)** | (from server startup) | `wss://your-domain.com:8443` | `https://your-domain.com:8443` |

**Note:** `10.0.2.2` is the magic IP that Android emulator uses to access the host machine's `localhost`.

To find your LAN IP:
- **Windows:** `ipconfig` → Look for "IPv4 Address"
- **macOS:** `ifconfig en0 | grep inet` → Look for "inet"
- **Linux:** `ip addr show` → Look for "inet"

---

### Common Workflows

#### First Time Setup (From Zero)
```bash
# 1. Start server (copy public key)
cd server && npm install && node index.js

# 2. Update server key in app/build.gradle.kts

# 3. Build app
cd ..
./gradlew clean assembleDebug

# 4. Start emulator
emulator -avd Pixel_5_API_34 &

# 5. Install and launch
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity

# 6. View logs
adb logcat | grep "HackSecure"
```

#### Rebuild and Redeploy
```bash
# 1. Build
./gradlew assembleDebug

# 2. Reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. Launch
adb shell am start -n com.hacksecure.messenger.debug/com.hacksecure.messenger.MainActivity
```

#### Clean Everything and Start Fresh
```bash
# 1. Clean build
./gradlew clean
rm -rf .gradle app/build build

# 2. Stop server
pkill -f "node index.js"

# 3. Uninstall app
adb uninstall com.hacksecure.messenger.debug

# 4. Start server (new key will be generated)
cd server && node index.js

# 5. Update key in app/build.gradle.kts

# 6. Build and install
cd ..
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 14. Complete Automation Script

Save this as `build-and-run.sh` in the project root directory.

```bash
#!/bin/bash

###############################################################################
# HackSecure Messenger - Automated Build and Deploy Script
# Usage: ./build-and-run.sh
###############################################################################

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.hacksecure.messenger.debug"
MAIN_ACTIVITY="com.hacksecure.messenger.MainActivity"

echo -e "${BLUE}"
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║        HackSecure Messenger - Build and Deploy               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

###############################################################################
# Step 1: Check Prerequisites
###############################################################################
echo -e "${YELLOW}[1/7] Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java not found. Please install JDK 17.${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" != "17" ]; then
    echo -e "${RED}✗ Java 17 required, found version $JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java 17 found${NC}"

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js not found. Please install Node.js 18+.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Node.js found: $(node --version)${NC}"

# Check ADB
if ! command -v adb &> /dev/null; then
    echo -e "${RED}✗ adb not found. Please install Android SDK Platform-Tools.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ adb found${NC}"

###############################################################################
# Step 2: Start Relay Server
###############################################################################
echo -e "\n${YELLOW}[2/7] Starting relay server...${NC}"

cd "$PROJECT_ROOT/server"

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "Installing server dependencies..."
    npm install
fi

# Kill existing server if running
pkill -f "node index.js" 2>/dev/null || true

# Start server in background
nohup node index.js > "$PROJECT_ROOT/server.log" 2>&1 &
SERVER_PID=$!
echo $SERVER_PID > "$PROJECT_ROOT/server.pid"
echo -e "${GREEN}✓ Server started (PID: $SERVER_PID)${NC}"
echo -e "${BLUE}  Check server.log for server public key${NC}"

# Wait for server to start
sleep 3

# Test server
if curl -s http://localhost:8443/api/v1/health > /dev/null; then
    echo -e "${GREEN}✓ Server is responding${NC}"
else
    echo -e "${RED}✗ Server not responding${NC}"
    exit 1
fi

cd "$PROJECT_ROOT"

###############################################################################
# Step 3: Build APK
###############################################################################
echo -e "\n${YELLOW}[3/7] Building APK...${NC}"

./gradlew clean assembleDebug

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Build failed!${NC}"
    kill $SERVER_PID 2>/dev/null
    exit 1
fi

echo -e "${GREEN}✓ Build successful${NC}"
echo -e "${BLUE}  APK: $APK_PATH${NC}"

###############################################################################
# Step 4: Check for Emulator/Device
###############################################################################
echo -e "\n${YELLOW}[4/7] Checking for connected devices...${NC}"

DEVICE_COUNT=$(adb devices | grep -c "device$")

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo -e "${RED}✗ No devices/emulators found!${NC}"
    echo -e "${YELLOW}  Please start an emulator or connect a device.${NC}"
    echo ""
    echo "  To start an emulator:"
    echo "    emulator -list-avds"
    echo "    emulator -avd <AVD_NAME> &"
    echo ""
    kill $SERVER_PID 2>/dev/null
    exit 1
fi

DEVICE_ID=$(adb devices | grep "device$" | head -n1 | awk '{print $1}')
echo -e "${GREEN}✓ Device found: $DEVICE_ID${NC}"

###############################################################################
# Step 5: Install APK
###############################################################################
echo -e "\n${YELLOW}[5/7] Installing APK...${NC}"

# Uninstall old version (ignore errors if not installed)
adb -s "$DEVICE_ID" uninstall "$PACKAGE_NAME" 2>/dev/null || true

# Install new version
adb -s "$DEVICE_ID" install "$APK_PATH"

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Installation failed!${NC}"
    kill $SERVER_PID 2>/dev/null
    exit 1
fi

echo -e "${GREEN}✓ Installation successful${NC}"

###############################################################################
# Step 6: Launch Application
###############################################################################
echo -e "\n${YELLOW}[6/7] Launching application...${NC}"

adb -s "$DEVICE_ID" shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY"

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Launch failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Application launched${NC}"

# Wait a moment for app to start
sleep 2

# Verify app is running
CURRENT_FOCUS=$(adb -s "$DEVICE_ID" shell "dumpsys window | grep mCurrentFocus")
if echo "$CURRENT_FOCUS" | grep -q "$PACKAGE_NAME"; then
    echo -e "${GREEN}✓ Application is running${NC}"
else
    echo -e "${YELLOW}⚠ Could not verify app is in focus${NC}"
fi

###############################################################################
# Step 7: Display Logs
###############################################################################
echo -e "\n${YELLOW}[7/7] Displaying logs (Ctrl+C to exit)...${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}\n"

# Clear old logs
adb -s "$DEVICE_ID" logcat -c

# Show filtered logs
adb -s "$DEVICE_ID" logcat | grep --color=always -E "HackSecure|AndroidRuntime|FATAL"

# Cleanup on exit (this runs when script exits or Ctrl+C is pressed)
trap "echo -e '\n${YELLOW}Stopping server...${NC}'; kill $SERVER_PID 2>/dev/null; echo -e '${GREEN}Done!${NC}'" EXIT
```

**Make it executable:**
```bash
chmod +x build-and-run.sh
```

**Run it:**
```bash
./build-and-run.sh
```

**What it does:**
1. Checks all prerequisites (Java, Node.js, adb)
2. Starts the relay server in background
3. Builds the debug APK
4. Checks for connected emulator/device
5. Uninstalls old version and installs new APK
6. Launches the application
7. Shows real-time logs filtered for HackSecure

**To stop:**
- Press `Ctrl+C`
- Server will be automatically stopped

---

## Additional Resources

### Documentation
- **Project README:** `README.md` - Architecture and cryptographic details
- **Server Code:** `server/index.js` - Relay server implementation
- **App Code:** `app/src/main/java/com/hacksecure/messenger/`

### Cryptography Details
- **Identity:** Ed25519 keypair (BouncyCastle + Android Keystore)
- **Session Keys:** X25519 ECDH + SHA-256 KDF
- **Ratchet:** SHA-256 hash chain for forward secrecy
- **Encryption:** ChaCha20-Poly1305 AEAD
- **Storage:** SQLCipher + per-message Keystore keys

### Architecture
```
Stage 1: Identity       → Ed25519 keypair
Stage 2: Session Auth   → Server-signed ticket
Stage 3: Key Derivation → X25519 DH + SHA-256
Stage 4: Ratchet        → SHA-256 hash chain
Stage 5: Message        → ChaCha20-Poly1305 + Ed25519 signature
Stage 6: Storage/Expiry → SQLCipher + Keystore
```

### Official Links
- **Android Developers:** https://developer.android.com/
- **Gradle:** https://gradle.org/
- **Node.js:** https://nodejs.org/
- **ADB Documentation:** https://developer.android.com/tools/adb

---

## Changelog

**v1.0.2_fixed (2026-03-08)**
- Complete setup guide from zero
- Platform-specific instructions for Windows/Mac/Linux
- Detailed troubleshooting section
- Automation script for build and deploy
- Two-device testing instructions

**v1.0.0 (Initial Release)**
- End-to-end encrypted messaging
- Self-destructing messages
- QR code identity exchange
- Blind relay server

---

**Last Updated:** 2026-03-08
**Author:** HackSecure Messenger Team
**Version:** 1.0.2_fixed

---

## Support

If you encounter issues not covered in this guide:

1. **Check logs:**
   ```bash
   # App logs
   adb logcat | grep -E "HackSecure|FATAL|AndroidRuntime"

   # Server logs
   cat server.log

   # Build logs
   ./gradlew assembleDebug --stacktrace --info
   ```

2. **Clean rebuild:**
   ```bash
   ./gradlew clean
   rm -rf .gradle app/build
   ./gradlew assembleDebug
   ```

3. **Verify environment:**
   ```bash
   java -version        # Should be 17.x.x
   node --version       # Should be >= 18.x.x
   adb version          # Should work
   echo $ANDROID_HOME   # Should be set
   ```

4. **Check server is running:**
   ```bash
   curl http://localhost:8443/api/v1/health or curl http://10.155.36.9:8443/health
   ```

5. **Test from emulator:**
   ```bash
   adb shell "curl http://10.0.2.2:8443/api/v1/health"
   ```

---

**End of Guide**
