# Web and Mobile Framework Repository for Automatic Testing

This repository contains all necessary tools and features for the automation of front end
applications. With the use of
environment variables in
IntelliJ IDEA, you can easily switch between running tests for web or mobile platforms and choose a
cloud testing provider as SauceLabs or keep running in Local. Also is
integrated with axe-core
tools to easily check web accessibility violations while running an automated web script.

## Getting Started

To set up your local environment and install dependencies for running the tests, follow these steps:

## Pre-requisites

Ensure you have IntelliJ IDEA installed on your system.
Make sure you have Maven installed, Git and Java jdk 11 or superior.
Have a GitHub account with a Personal Access Token.

IntelliJ Idea Community: https://www.jetbrains.com/idea/download/?section=windows
(The one at the bottom of the page, not the Ultimade)

Maven: https://maven.apache.org/download.cgi
(Download the Binary zip archive)

Java: https://www.java.com/es/download/ie_manual.jsp

Git: https://www.git-scm.com/downloads

## Setting Up GitHub Packages

To download the required Maven packages from GitHub Packages, follow these steps:

Step 1: Generate a new Personal Access Token

Navigate to your personal GitHub account and go to Settings.
Once the page fully loads, scroll down to the Developer settings section.
Click on Personal Access tokens, then select Tokens (classic).
Follow the prompts to generate a new token, ensuring that you grant it the read
permission.
Once the token is generated, make sure to save the key securely, as you won't be able to see it again.

Step 2: Configure GitHub authentication

Create or update your `settings.xml` file in the `.m2` directory (usually located in your home
directory (path: C:/Users/UserName/.m2)) with the following content:

```xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 http://maven.apache.org/xsd/settings-1.2.0.xsd">

    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>

</settings>
```

Replace YOUR_GITHUB_USERNAME with your GitHub username and YOUR_GITHUB_TOKEN with a personal access
token(the one you safe before) generated from your GitHub account with read:packages permissions.

### Install User Certificate in Git

Open the following
link: https://everisgroup.sharepoint.com/sites/Zscaler/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FZscaler%2FShared%20Documents%2FKnown%20Issues%2FFixes%2FWindows&viewid=a2c69b76%2Dc9ca%2D4e54%2D987b%2D3c3835a729e3
Locate the file named "Zscaler_USER_EnvironmentVariables.exe", download it to your computer and execute it.
Ensure you execute the process with Administrator privileges to guarantee all necessary permissions are granted.
## Setting Up Appium Environment

Step 1: Install Node.js
Appium runs on Node.js. If you don't already have Node.js installed, download and install it
from https://nodejs.org/en/download.

Step 2: Install Appium

Install Appium globally using npm (Node Package Manager). Open your terminal or command prompt and
run the following
command:

`npm install -g appium`

You will also need the uiautomator2 driver if you're using Android, run the following command just as before:

`appium driver install uiautomator2`

In case you are using iOS you should run the following command instead:

`appium driver install xcuitest`

Step 3: Install Appium Dependencies

Appium has specific dependencies for different mobile development platforms. Install the necessary
dependencies based on the platform you want to automate tests for.
In the next lines you will install this different dependencies.

### Android

Install Android Studio following the instructions on https://developer.android.com/studio/install.
Set up `ANDROID_HOME` and `JAVA_HOME` environment variables correctly in user variables.You must also do the same for maven.

If you are using Windows you should have: `C:\Users\your_username\AppData\Local\Android\Sdk` as `ANDROID_HOME`,
`C:\Users\your_username\.jdks\your_installedJDK` as `JAVA_HOME`,'C:\Program Files\apache-maven-3.9.9' as 'M2_HOME' and inside the route in the variable 'Path' you must also add `%ANDROID_HOME%\platform-tools` and '%M2_HOME%\bin'.

Step 4: Set Up Emulators or Physical Devices

Create a new project, select "No activity" option and follow the default configuration.  
Once you have Android Studio configured, go to *Settings*, search for *Android SDK Command-line
Tools* and apply changes.  
Use Virtual Device Manager tool from Android Studio to start a customized Android emulator.

### Install User Certificate for Android Studio

Open the following
link: https://everisgroup.sharepoint.com/sites/Zscaler/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FZscaler%2FShared%20Documents%2FKnown%20Issues%2FCA%5FCertificates&viewid=a2c69b76%2Dc9ca%2D4e54%2D987b%2D3c3835a729e3
Locate the file named "Zscaler_Root_CA.exe " and download it to your computer.

Launch Android Studio in your computer and open the Device File Explorer. Navigate to the sdcard/Download Folder (
Right-click in the Download folder, select Upload) and navigate to the location where you saved the Zscaler_Root_CA.cer
file on your computer. Click open to upload it to the device.

Step 5: Start Appium Server

Run on terminal the command: `appium` to start appium server

## IDE Configuration

### Install User Certificate for IntelliJ

Open the following
link: https://everisgroup.sharepoint.com/sites/Zscaler/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FZscaler%2FShared%20Documents%2FKnown%20Issues%2FFixes%2FWindows&viewid=a2c69b76%2Dc9ca%2D4e54%2D987b%2D3c3835a729e3
Locate the file named "Zscaler_ADMIN_Update_Java_CACERTS.exe ", download it to your computer and execute it.
Be sure to execute it with Administrator privileges again, as you did before, to avoid any potential issues in the future.

### Configuration Steps

Create a new folder and clone the Repository:

`git clone https://github.com/raunelgarcia/front-end-automation.git`

Open Project in IntelliJ IDEA:

Open IntelliJ IDEA and select File > Open.

Navigate to the directory where you cloned the repository and select it.

### Configure SDK

Go to File > Project Structure
Click on the project SDK field. If the Amazon Correto SDK is not listed, select Add SDK > Download JDK. Select Amazon
Corretto, version 11, if you can’t find the exact one, use at least the same version to prevent any issues.
Apply and confirm the configuration

### Add google java plugin

Go to File > Settings > Plugins > Marketplace and search the google-java-format plugin and install it, you will probably
need
to restart de IDE after installing it. Once it is installed you should enable the plugin in File > Settings >
google-java-format Settings.
In order to be able to Reformat Code with the Optimize import option on you should go to Help > Edit Custom VM Options,
paste the following lines and restart the IDE afterward:

`--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED`

## Set Environment Variables:

You can set environment variables in IntelliJ IDEA by going to Run > Edit Configurations > Add JUNIT
Configuration >
Environment.

#### Environment Variables:

- Platform:
    - Possible values: Android, IOS, Web.
    - Description: Specifies the platform for testing, whether it is Android, iOS or Web. Must be
      provided with valid values for the tests to function correctly.
- Accessibility:
    - Possible values: true, false.
    - Description: indicates whether accessibility features are enabled during testing. The variable
      can take the value of null or blank, in which case the Accessibility report will not be shown.
- Browser:
    - Possible values: Chrome, Firefox, Edge, Safari.
    - Description: Defines the browser to be used for web testing. The 'Browser' variable can take the value of 'null',
      in which case the default browser used for testing is 'chrome'.
      If you are using Safari for MacOS, write this command for enable the automatization: `safaridriver --enable`
- Application:
    - Description: Should be the name of the web app being tested. Application cannot be
      null and must be provided with valid values for the tests to function correctly.
- Resolution:
    - Possibles values: the possibles values of the environment variable resolution are established
      in the 'allowedResolution.yaml'.
    - Description: Specifies the screen resolution for web testing. If null or blank, a default
      resolution of 1024x768
      is set.
- Language:
    - Possibles values: en-GB, en-US, es-ES, fr-FR and others.
    - Description: Specifies the language of the test execution. If null or blank, the default
      language is es-ES.
- App:
    - Description: Specifies the path or name of the APK or .IPA file to be installed and tested on
      an Mobile device.
      The tests work if either the App field is set, or both AppPackage and AppActivity fields are
      provided (only
      Android).
- AppActivity:
    - Description: Represents the app activity for Android of the app being tested for mobile
      testing. The tests work if
      both AppPackage and AppActivity fields are provided.
- AppIdentifier:
    - Description: For Android it represents the app package and for IOS it represents the bundleId
      of the app being tested for mobile testing. The Android tests work if both AppPackage and AppActivity fields
      are provided.
- AppVersion:
    - Description: Represent the version of the app installed in SauceLabs specified in "AppIdentifier". If left blank,
      the default value is `latest`, which means it will run the latest version of the app. You can also specify the
      version if wanted. This variable will be validated with a test execution on SauceLabs only.
- Udid:
    - Description: Represent the unique device identifier (UDID) of the device being tested for
      mobile testing.It can be obtained running on terminal `adb devices`. Must be provided with valid values for the
      tests to function correctly. For Android, in the variable takes the value of 'null', the default value is the
      current emulator Udid. For IOS, the Udid cannot be 'null'.
- Provider:
    - Possible values: Local and SauceLabs
    - Description: Specifies the provider or environment to launch any test execution. Could be made
      on SauceLabs or Local Environment.
- User:
    - Description: Represents the username of your SauceLabs Account.
- AccessToken:
    - Description: Represents the API access token of your SauceLabs Account.
- DeviceName:
    - Description: Represents the name of the device being tested for mobile testing on SauceLabs. If left blank, the
      default value is `.*`, which means any available device. If you want to use an emulator, you must
      write `emulator` for Android or `simulator` for iOS, and it will build up automatically.
- PlatformVersion:
    - Description: Represents the version of the device being tested for mobile testing on SauceLabs. If left blank, the
      default value is `.*`, which means the latest version of any available device. If left blank while using an
      emulator, the default value is `current_major`, which means it will use the latest version. You can also specify
      the version if needed, having in mind that you should write the exact version, for example the shortest versions
      you should use are '8.0' for Android and '14.0' for iOS.

Ensure to set these variables according to your testing requirements before executing the tests.

### Android Variables Example

Case 1: With AppActivity and AppPackage, when you have installed the app.
AppActivity=.activities.MainContainerActivity;AppIdentifier=com.iphonedroid.marca;Platform=Android;Udid=;Provider=;

| Campo         | Valor                             |
|---------------|-----------------------------------|
| AppActivity   | .activities.MainContainerActivity |
| AppIdentifier | com.iphonedroid.marca             |
| Platform      | Android                           |
| Udid          |                                   |

Case 2: With App, when you don't have the app installed.  
App=marca-com-7-0-20.apk;Platform=Android;Udid=;

| Campo    | Valor                     |
|----------|---------------------------| 
| App      | com.iphonedroid.marca.apk | 
| Platform | Android                   |
| Udid     |                           |

Case 3: With SauceLabs, physical device.
Platform=Android;Provider=SauceLabs;DeviceName=Samsung Galaxy
S9;AppIdentifier=com.saucelabs.mydemoapp.android;User=;AccessToken=;AppVersion=latest;

| Campo         | Valor                           |
|---------------|---------------------------------|
| Platform      | Android                         |
| Provider      | SauceLabs                       |
| DeviceName    | Samsung Galaxy S9               |
| AppIdentifier | com.saucelabs.mydemoapp.android |
| User          |                                 |
| AccessToken   |                                 |
| AppVersion    | latest                          |

Case 4: With SauceLabs, emulator.
Platform=Android;Provider=SauceLabs;DeviceName=emulator;AppIdentifier=com.saucelabs.mydemoapp.android;User=;AccessToken=;AppVersion=latest;PlatformVersion=;

| Campo           | Valor                           |
|-----------------|---------------------------------|
| Platform        | Android                         |
| Provider        | SauceLabs                       |
| DeviceName      | emulator                        |
| AppIdentifier   | com.saucelabs.mydemoapp.android |
| User            |                                 |
| AccessToken     |                                 |
| AppVersion      | latest                          |
| PlatformVersion |                                 |

### IOS Variables Example

Case 1: With AppIdentifier.

AppIdentifier=com.marca.marcador;Platform=IOS;Udid=A308507F-99BB-47A2-9A2D-06005CAAD428;Provider=;

| Campo         | Valor                                |
|---------------|--------------------------------------|
| AppIdentifier | com.marca.marcador                   |
| Platform      | IOS                                  |
| Udid          | A308507F-99BB-47A2-9A2D-06005CAAD428 |

Case 2: With SauceLabs, physical device.

Platform=IOS;Provider=SauceLabs;DeviceName=iPhone
12;AppIdentifier=com.saucelabs.mydemoapp.ios;User=;AccessToken=;AppVersion=latest;

| Campo         | Valor                       |
|---------------|-----------------------------|
| Platform      | IOS                         |
| Provider      | SauceLabs                   |
| DeviceName    | iPhone 12                   |
| AppIdentifier | com.saucelabs.mydemoapp.ios |
| User          |                             |
| AccessToken   |                             |
| AppVersion    | latest                      |

Case 3: With SauceLabs, simulator.
Platform=IOS;Provider=SauceLabs;DeviceName=simulator;AppIdentifier=com.saucelabs.mydemoapp.ios;User=;AccessToken=;AppVersion=latest;PlatformVersion=;

| Campo           | Valor                       |
|-----------------|-----------------------------|
| Platform        | IOS                         |
| Provider        | SauceLabs                   |
| DeviceName      | simulator                   |
| AppIdentifier   | com.saucelabs.mydemoapp.ios |
| User            |                             |
| AccessToken     |                             |
| AppVersion      | latest                      |
| PlatformVersion |                             |

### Web Variables

Accessibility=true;Platform=Web;Application=mrc;Browser=chrome;Resolution=1920x1200;Provider=SauceLabs;User=;AccessToken=;

| Campo         | Valor     |
|---------------|-----------|
| Accessibility | true      |
| Platform      | Web       |
| Application   | mrc       |
| Browser       | chrome    |
| Resolution    | 1920x1200 |
| Provider      | SauceLabs |
| User          |           |
| AccessToken   |           |

### Install Dependencies:

Open the pom.xml file in IntelliJ IDEA. Click on the 'Maven' tab, then click on the 'Reimport'
button to install all the
required dependencies.

### Configure Mobile Testing Environment (For Mobile Tests):

Before running mobile tests, ensure that you have:

- Mobile Platform Setup: Make sure you have the appropriate emulators, simulators, or physical
  devices configured and
  available for testing.

- Environment Configuration: Configure the mobile platform-specific settings in the
  mobileConfiguration.yaml file
  located in the project directory (resources/yaml/mobileConfiguration.yaml). Ensure that the
  required capabilities are
  correctly specified for Android and iOS platforms.

You can now run the automated tests either for web or mobile platforms based on the environment
variable you've set.

### Test Structure

src/test/java contains all the test classes.

src/test/resources contains apks, ipas and properties files for handle multilanguage texts.

### Using Allure Reports

If you want to generate reports with Allure, the framework is ready for it. Beforehand, some
preliminary configurations need to be done.

Install Allure in the project using on terminal the
command `npm install --save-dev allure-commandline`

Once it's installed and when the test is executed, the results will be saved in
target/allure-results. A browser window
will open automatically after few seconds, displaying the Allure report.
Tests that encountered errors will have an error message displayed in the description section along
with a screenshot
pointing the location of the error. Also, Network logs will be attached to the report with all https
API requests made by the front end application and will be saved in network-logs directory

### Accessibility Reports

When the "Accessibility" variable is set to "true", accessibility reports will be generated and
saved in a folder
named "target/java-a11y".These reports provide insights into the accessibility status of your
application, helping to
ensure compliance with accessibility standards.

#### Report Formats:

The accessibility reports are available in two formats:

1. HTML Reports: These reports are human-readable and can ve opened in any browser. They provide
   detailed information
   about accessibility issues found during testing.
2. JSON Reports: These reports contain machine-readable data about accessibility issues detected in
   the application.

To access these report navigate to the "target/java-a11y" folder. You can open the HTML reports in
your preferred web
browser to review the findings.

### Contributors

Raunel Garcia Quintana

Raul Galera Sancho

Alejandra Alvarado Tirado

Leire Fuchun Jimenez Gonzalez

Ciro Alonso Aquino

### Acknowledgments

Special thanks to our contributors and the open-source community for their valuable contributions
and support.

