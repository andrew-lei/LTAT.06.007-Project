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
* Tox
    - Peer-to-peer distributed messaging protocol
    - Public-private key associated with device
    - End-to-end encrypted with forward secrecy
    - Can not disable the encryption features
    - Supports voice and video
    - No offline messages

# Related work
* Telegram
    - Cloud-based 
    - Supports audio and video
    - Client open-source, server closed-source
    - Stores contacts, messages and media with decryption keys on servers
    - Uses custom designed encryption protocol
    - Accounts are tied to telephone numbers

# Related work
* Signal
    - Encrypted communcation application
    - Relies on centralized servers
    - Some functions peer-to-peer
    - Supports audio, video and file sharing
    - Telephone numbers used as identifiers
    - Generated keys stored in endpoints (users' devives)
    - Local message databases can be encrypted
    - Allows users to set timers to messages

# Related work
* Bitmessage
    - Peer-to-peer, stores messages on P2P network
    - Encrypts user's inbox using public-key cryptography
    - Replicates users' inboxes inside the peer-to-peer network
    - Messages don't contain address of the recipient
    - Nodes store encrypted messages for two days
    - Requires proof-of-work for anti-spam
    - Proof-of-work less practical for sending messages on mobile devices

# Related work
* Retroshare
    - Instant messaging and file sharing network
    - Uses distributed hash table for address discovery
    - Friend-to-firend network
    - Uses indirect communication through mutual friends and direct connections
    - Supports voice and video

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
* Jüri Gramann
    - Peer finding, (indirect) message sending
* Tambet Kaal
    - EID Integration, GUI
* Mattias Lass
    - Designated servers, GUI
* Andrew Lei
    - Encryption/verification, indirect message sending

# Testing plan
* Security
    - Messages can't be tampered with
* Reliability
    - Messages reach destination
* Performance
    - System must work correctly under load

# Repo

<https://github.com/andrew-lei/LTAT.06.007-Project>
