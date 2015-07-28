Pinell Android Application
=====================

# Information

Its the missing application for Pinell Supersound related to Android devices. Finally. omfg.

# Based on

..

# API-Overview

See module <?>

# Ports for Pinell

This is the port-scanning for my own Pinell device.

    80 - is open
    2244 - is open

    ^------------ those were open for 192.168.0.104

# Known errors

* GET_NOTIFIERS is hanging. This should be put in its own thread as it can cause application stop
* The discovery/TelnetUtils does not work as intended on Android. Sometimes it does not discover the devices, which is quite odd.

# Contributors