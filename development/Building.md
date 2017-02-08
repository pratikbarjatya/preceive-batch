# Building the App

## Dependencies

Building this software requires Java JDK 1.8 or later which you can download from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

The app project is built using [Gradle](https://docs.gradle.org/current/userguide/installation.html) (tested on version 2.14.1).

## Build Steps

Having installed Gradle, first run:

	$ gradle wrapper

Subsequently, to generate a clean build:

	$ ./gradlew clean build

To build a zip file of the installation as ```./preceive-cli/build/distributions/preceive-cli.zip```:

	$ ./gradlew distZip

To build a tar file of the installation as ```./preceive-cli/build/distributions/preceive-cli.tar```:

	$ ./gradlew distTar

To install the app to ```./preceive-cli/build/install/preceive-cli/```:

	$ ./gradlew installDist

Following the above build steps, scripts for running the distribution on *nix and Windows can be found at:

	./preceive-cli/build/install/preceive-cli/bin/preceive-cli

	./preceive-cli/build/install/preceive-cli/bin/preceive-cli.bat

To compile the app's html documentation:

	$ ./gradlew generateDocs

To jacoco code coverage (results will be build/reports/jacoco/jacocoRootReport/html/index.html)

	$ ./gradlew jacocoRootReport
