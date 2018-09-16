---
title: Encrypted P2P Messaging Using EID
author: Mattias Lass CS (Leader),
        Tambet Kaal CS,
        Andrew Lei CS,
        Jüri Gramann RCE
date: 2018 September 20
---

# Team members
* Mattias Lass CS (Leader)
* Tambet Kaal CS
* Andrew Lei CS
* Jüri Gramann RCE

# Idea description

* Peer-to-peer messaging
* Use Estonian ID system for both identification and encryption
* Pass messages to offline users by holding them with mutual friends

# Related work

* Signal
    - End-to-end message encryption instead of peer-to-peer
* Bitmessage
    - Peer-to-peer, stores messages on P2P network
    - Requires proof-of-work for anti-spam
    - Proof-of-work less practical for sending messages on mobile devices

# Workflow

* October 11
    - MVC
    - Send/receive messages
    - Encrypt/decrypt
    - Verify identity
* November 1
    - Hold messages for peers
    - Resend messages when user comes back online
* November 22
    - Make GUI for application
* December 13
    - Additional features
    - Bug fixes
    - Finishing touches

# Workload distribution

* Andrew Lei
    - Encryption and verification

# Testing plan

# Repo

<https://github.com/andrew-lei/LTAT.06.007-Project>
