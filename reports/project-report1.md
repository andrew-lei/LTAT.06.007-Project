---
title: Encrypted P2P Messaging Using EID - report \#1
author: Jüri Gramann, 
        Tambet Kaal, 
        Mattias Lass, 
        Andrew Lei
date: 11th October 2018
---

# Milestone 1
## Goals 
- MVC
- Basic GUI
- Sending messages over TCP + finding peers

## Actual work done 
- MVC
- Basic GUI
- Sending messages over TCP + finding peers in LAN
- Basic cryptography module

# Mattias
- Set up spring project
- Minimal UI
- Sqllite as database
- LAN peer finding

# Jüri
- Sending and recieving messages over TCP
- Peer handling

# Tambet
- MVC
- UI

# Andrew
## Cryptography Model

- Generate public/private key pair
- Use EID to sign public key
- Distribute public key - others can verify it against your ID
- Others use key to encrypt messages to send you

## Advantages

- Don't need cardreader on device all the time, only when generating key
  - Smartphone app
- Could allow non-EID-holders to use, but without the guarantee of identity verification

## Disadvantages

- Key could be stolen from device

# Andrew
## Implemented so far

- Generate key pair
- Sign public key with DigiDoc in test mode
- Encrypt string with public key
