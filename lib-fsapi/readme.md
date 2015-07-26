Library for FSAPI-enable Radios
===============================

# Introduction

FS API = Frontier Silicon API

This library is actually meant to target Pinell-radios, however it may also be used with other devices out there supporting the standard FSAPI.

Please note that the Pinell devices is not using all functionalities that is exposed/supported by the Frontier Silicon API itself.

# Usage

Not finished yet.

# Todos

- The final bit regarding FM is not completed, as there is some logic regarding searching and selecting which actually remains.
- The verification of IPs in a net must be very much improved. It may mean that multi-threading must be involved in order to find the end-points quicker then now. E.g if someone has the IP of 1.1.1.255 \>.\> .... That would take a while.