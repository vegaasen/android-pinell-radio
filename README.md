Pinell Android Application
=====================

# Information

Its the missing application for Pinell Supersound related to Android devices. Finally. omfg.

# Based on

Nothing.

# API-Overview

See module <?>

# Adding proxy when debugging Emulator

This is quite simple. Do the following:

* Download Charles or similar
* Add proxy-listner port
* Configure VPN within Emulator
* * Use (local) ip address with (configured) port
* Done :-)

# Ports for Pinell

This is the port-scanning for my own Pinell device.

    80 - is open
    2244 - is open

    ^------------ those were open for 192.168.0.104

# Known errors

* GET_NOTIFIERS is hanging. This should be put in its own thread as it can cause application stop
* The discovery/TelnetUtils does not work as intended on Android. Sometimes it does not discover the devices, which is quite odd.

# Contributors

* Vegard Aasen | vegaasen@gmail.com | Norway | Kongsberg/Odda | Kongsberg ASA | Aasen Websnekkeri