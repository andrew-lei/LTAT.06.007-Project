---
title: Encrypted P2P Messaging Using EID
author: Jüri Gramann, 
        Tambet Kaal, 
        Mattias Lass, 
        Andrew Lei
date: 20th September 2018
---

# Cryptography Model

- Generate public/private key pair
- Use EID to sign public key
- Distribute public key - others can verify it against your ID
- Others use key to encrypt messages to send you

# Pros/Cons
## Advantages

- Don't need cardreader on device all the time, only when generating key
  - Smartphone app
- Could allow non-EID-holders to use, but without the guarantee of identity verification

## Disadvantages

- Key could be stolen from device

# Implemented so far

- Generate key pair
- Sign public key with DigiDoc in test mode
- Encrypt string with public key
