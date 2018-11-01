---
title: Encrypted P2P Messaging Using EID - report \#1
author: Jüri Gramann, 
        Tambet Kaal, 
        Mattias Lass, 
        Andrew Lei
date: 1st November 2018
---

# Milestone 2
## Goals 

- Functional GUI
- EID signing
- Message encryption

## Actual work done 

- Functional encryption utilities

# Mattias

# Jüri

- Research NAT traversial
- Different types of NAT
- ICE 
    - Ice4j (UDP)
    - JSTUN (UDP)
- Recieve public IP and port

---
![](https://upload.wikimedia.org/wikipedia/commons/6/63/STUN_Algorithm3.svg)
Source: https://commons.wikimedia.org/wiki/File:STUN_Algorithm3.svg

# Tambet

# Andrew

- Generate RSA key pair
- Use EID to sign public key
- Verify signed public key
- Generate and encrypt AES key with public key
- Decrypt AES key with private key
- Encrypt/decrypt AES messages
