Library for FSAPI-enable Radios
===============================

# Introduction

FS API = Frontier Silicon API

This library is actually meant to target Pinell-radios, however it may also be used with other devices out there supporting the standard FSAPI.

Please note that the Pinell devices is not using all functionalities that is exposed/supported by the Frontier Silicon API itself.

# Extras

/fsapi/SET/netRemote.sys.info.friendlyName?pin=1234&value=Pinell%20Supersound%20002261bf1170&_=1449270945079
/fsapi/GET/netRemote.sys.info.friendlyName?pin=1234&_=1449270762342
/fsapi/GET/netRemote.sys.net.wlan.connectedSSID?pin=1234&_=1449270762343
/fsapi/GET/netRemote.sys.net.wlan.rssi?pin=1234&_=1449270762343
/fsapi/GET/netRemote.sys.net.ipConfig.dhcp?pin=1234&_=1449270762344
/fsapi/GET/netRemote.sys.net.ipConfig.address?pin=1234&_=1449270762345
/fsapi/GET/netRemote.sys.net.ipConfig.gateway?pin=1234&_=1449270762346
/fsapi/GET/netRemote.sys.net.ipConfig.dnsPrimary?pin=1234&_=1449270762346
/fsapi/GET/netRemote.sys.net.ipConfig.dnsSecondary?p

# Usage

Not finished yet.

# Todos

- The final bit regarding FM is not completed, as there is some logic regarding searching and selecting which actually remains.
- The verification of IPs in a net must be very much improved. It may mean that multi-threading must be involved in order to find the end-points quicker then now. E.g if someone has the IP of 1.1.1.255 \>.\> .... That would take a while.