### Web and Mobile Automated Testing Repository

This repository contains automated tests for both web and mobile applications. With the use of environment variables in IntelliJ IDEA, you can easily switch between running tests for web or mobile platforms. Also is integrated with axe-core tools to easily check web accessibility violations while running an automated web script.

### Getting Started
To set up your local environment and install dependencies for running the tests, follow these steps:

### Pre-requisites
Ensure you have IntelliJ IDEA installed on your system.
Make sure you have Maven installed and Java jdk 11 or superior.

### Setting Up Appium Environment

Step 1: Install Node.js
Appium runs on Node.js. If you don't already have Node.js installed, download and install it from https://nodejs.org/en/download.

Step 2: Install Appium

Install Appium globally using npm (Node Package Manager). Open your terminal or command prompt and run the following command: 

npm install -g appium

Step 3: Install Appium Dependencies

Appium has specific dependencies for different mobile development platforms. Install the necessary dependencies based on the platform you want to automate tests for.

### Android
Install Android Studio following the instructions on https://developer.android.com/studio/install.
Set up ANDROID_HOME and JAVA_HOME environment variables correctly.

Step 4: Set Up Emulators or Physical Devices

Use Virtual Device Manager tool from Android Studio to start a customized Android emulator

Step 5: Start Appium Server

Run on terminal the command: appium to start appium server

### IDE Configuration Steps
Clone the Repository:

git clone https://github.com/raunelgarcia/mobile-framework.git

Open Project in IntelliJ IDEA:

Open IntelliJ IDEA and select File > Open.

Navigate to the directory where you cloned the repository and select it.

### Set Environment Variables:

You can set environment variables in IntelliJ IDEA by going to Run > Edit Configurations > Add JUNIT Configuration > Environment.

### Android Variables
AppActivity=xxx;AppPackage=xxxx;Platform=Android;Udid=xxxx

### Web Variables
Accessibility=true;Platform=Web;Url=https://www.xxxx.com/

### Install Dependencies:

Open the pom.xml file in IntelliJ IDEA. Click on the 'Maven' tab, then click on the 'Reimport' button to install all the required dependencies.

### Configure WebDriver (For Web Tests):

If you're running web tests, make sure you have the appropriate WebDriver installed and configured. You may need to download the WebDriver for your preferred browser and set the path accordingly.

### Configure Mobile Testing Environment (For Mobile Tests):

If you're running mobile tests, ensure you have the necessary emulators or devices set up and configured for testing. You may need to configure the mobile platform-specific settings accordingly.

Run Tests:

You can now run the automated tests either for web or mobile platforms based on the environment variable you've set.

### Test Structure

src/test/java contains all the test classes.

src/test/resources contains apks.

### Using Allure Reports

If you want to generate reports with Allure, the framework is ready for it. Beforehand, some preliminary configurations need to be done.

Install Allure in the project using the command 'npm install --save-dev allure-commandline'

Once it's installed, you can use Allure commands from the project directory. When the test is executed, the results will be saved in target/allure-results

To generate HTML reports, you'll need to execute the command 'npx allure-commandline generate target/allure-results'

And to view the report, run 'npx allure-commandline open allure-report'

### Contributors

Raunel Garcia Quintana

Raul Galera Sancho

### Acknowledgments

Special thanks to our contributors and the open-source community for their valuable contributions and support.
