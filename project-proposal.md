---
title: Encrypted P2P Messaging Using EID
author: Jüri Gramann, 
        Tambet Kaal, 
        Mattias Lass, 
        Andrew Lei
date: 20th September 2018
---

# Team members
* Jüri Gramann RCE
* Tambet Kaal CS
* Mattias Lass CS (Leader)
* Andrew Lei CS

# Idea description

* Encrypted peer-to-peer messaging
* Uses Estonian ID system for both identification and encryption
* Pass messages to offline users by holding them with mutual friends
* Designated servers that pass messages to offline peers

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
    - Basic GUI
    - Sending messages over TCP + finding peers
* November 1
    - Functional GUI
    - Encryption using EID
* November 22
    - Holding unsent messages for friendly peers
    - Designated message holding servers
    - GUI design
* December 13
    - Additional features (E.g group chat)
    - Testing/Polishing/Bug fixes

# Workload distribution

* Andrew Lei
    - Encryption and verification

# Testing plan

# Repo

<https://github.com/andrew-lei/LTAT.06.007-Project>
