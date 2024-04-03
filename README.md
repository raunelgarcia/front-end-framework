## Web and Mobile Automated Testing Repository

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

`npm install -g appium`

Step 3: Install Appium Dependencies

Appium has specific dependencies for different mobile development platforms. Install the necessary dependencies based on the platform you want to automate tests for.

### Android
Install Android Studio following the instructions on https://developer.android.com/studio/install.
Set up `ANDROID_HOME` and `JAVA_HOME` environment variables correctly.

Step 4: Set Up Emulators or Physical Devices

Use Virtual Device Manager tool from Android Studio to start a customized Android emulator

Step 5: Start Appium Server

Run on terminal the command: `appium` to start appium server

### IDE Configuration Steps
Clone the Repository:

`git clone https://github.com/raunelgarcia/mobile-framework.git`

Open Project in IntelliJ IDEA:

Open IntelliJ IDEA and select File > Open.

Navigate to the directory where you cloned the repository and select it.

### Set Environment Variables:

You can set environment variables in IntelliJ IDEA by going to Run > Edit Configurations > Add JUNIT Configuration > Environment.

#### Environment Variables:
- Platform:
  - Possible values: Android, IOS, Web.
  - Description: Specifies the platform for testing, whether it is Android, iOS or Web. Must be provided with valid values for the tests to function correctly.
- Accessibility:
  - Possible values: true, false, 
  - Description: indicates whether accessibility features are enabled during testing.The variable can take the value of 'null', in which case the Accessibility report will not be shown.
- Browser:
  - Possible values: Chrome, Firefox, Edge.
  - Description: Defines the browser to be used for web testing. The 'Browser' variable can take the value of 'null', in which case the default browser used for testing is 'chrome'.
- Application:
  - Description: Should be the name of the web or mobile app being tested. Application cannot be null and must be provided with valid values for the tests to function correctly.
- Resolution: 
  - Description: Specifies the screen resolution for web testing. If null or blank, a default resolution of 1024x768 is set.
- Apk:
  - Description: Specifies the path or name of the APK file to be installed and tested on an Android device. The tests work if either the Apk field is set, or both AppPackage and AppActivity fields are provided.
- AppActivity: 
  - Description: represents the app activity of the app being tested for mobile testing. The tests work if both AppPackage and AppActivity fields are provided.
- AppPackage: 
  - Description: Represents the app package of the app being tested for mobile testing. The tests work if both AppPackage and AppActivity fields are provided.
- Udid: 
  - Description: Represent the unique device identifier (UDID) of the device being tested for mobile testing. Must be provided with valid values for the tests to function correctly.

Ensure to set these variables according to your testing requirements before executing the tests.

### Android Variables

AppActivity=xxxx;AppPackage=xxxx;Apk=xxxx;Platform=Android;Udid=xxxx

| Campo       | Valor   |
|-------------|---------|
| AppActivity | xxxx    |
| AppPackage  | xxxx    |
| Apk         | xxxx    | 
| Platform    | Android |
| Udid        | xxxx    |


### Web Variables

Accessibility=xxxx;Platform=Web;Url=xxxx;Browser=xxxx;Resolution=xxxx;

| Campo         | Valor  |
|---------------|--------|
| Accessibility | true   |
| Platform      | Web    |
| Application   | xxxx   |
| Browser       | xxxx   |
| Resolution    | xxxx   |


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

src/test/resources contains apks and features for Cucumber.

### Using Allure Reports

If you want to generate reports with Allure, the framework is ready for it. Beforehand, some preliminary configurations need to be done.

Install Allure in the project using the command `npm install --save-dev allure-commandline`

Once it's installed and when the test is executed, the results will be saved in target/allure-results. A browser window will open automatically, displaying the Allure report.
Tests that encountered errors will have an error message displayed in the description section along with a screenshot pointing the location of the error.

### Accessibility Reports

When the "Accessibility" variable is set to "true", accessibility reports will be generated and saved in a folder named "java-a11y".These reports provide insights into the accessibility status of your application, helping to ensure compliance with accessibility standards.

#### Report Formats:

The accessibility reports are available in two formats: 

1. HTML Reports: These reports are human-readable and can ve opened in any browser. They provide detailed information about accessibility issues found during testing.
2. JSON Reports: These reports contain machine-readable data about accessibility issues detected in the application.

To access these report navigate to the "java-a11y" folder. You can open the HTML reports in your preferred web browser to review the findings.
### Contributors

Raunel Garcia Quintana

Raul Galera Sancho

Alejandra Alvarado Tirado

### Acknowledgments

Special thanks to our contributors and the open-source community for their valuable contributions and support.
