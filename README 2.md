# Preface #

This document describes the functionality provided by the XL Release Flowdock plugin.

See the [XL Release Reference Manual](https://docs.xebialabs.com/xl-release/4.5.x/reference_manual.html) for background information on XL Release and release orchestration concepts.

# Overview #

The XL Release Flowdock plugin is a XL Release plugin that adds capability for sending message to a Flowdock inbox.

# Requirements #

* **Requirements**
	* **XL Release** 4.5.0

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Usage #

1. Go to `Settings - Configuration - Flowdock: Configuration`
   ![Flowdock configuration part 1](/src/doc/resources/images/Flowdock_configuration_1.png?raw=true "Flowdock configuration part 1")
2. Add a new configuration
   ![Flowdock configuration part 2](/src/doc/resources/images/Flowdock_configuration_2.png?raw=true "Flowdock configuration part 2")
3. Provide Title (Can be anything), API url (eg: https://api.flowdock.com), Flow token (See your Flow configuration in Flowdock) and enable or disable the Flowdock configuration.
   ![Flowdock configuration part 3](/src/doc/resources/images/Flowdock_configuration_3.png?raw=true "Flowdock configuration part 3")
