---
title: Encrypted P2P Messaging Using EID
author: Jüri Gramann, 
        Tambet Kaal, 
        Mattias Lass, 
        Andrew Lei
date: 12th December 2018
---

# Team members
* Jüri Gramann RCE
* Tambet Kaal CS
* Mattias Lass CS (Leader)
* Andrew Lei CS

# Idea description

* Encrypted peer-to-peer messaging
* Uses Estonian ID system for both identification and encryption

# Description of Distributed systems challenges addressed

* Finding peers
* Synchronization of states (message history, public key collection)
* Preserving the anonymity of nodes
* Secure(encrypted) communication between nodes
* Symmetry of peer to peer communication(how to handle the case when two parties try to initiate a connection)
* NAT

# Related work
* Tox - p2p, encrypted, no offline messaging
* Telegram - supports encryption
* Signal - encrypted
* Bitmessage - p2p, encrypted, offline messaging

# Major design and architecture - node network

* The aim is for every node to know the ip of every other node and the list of the most recent public keys(latest signature)
* Therefore nodes advertise periodically(over UDP) a list of known ip addresses and a list of hashes of known public keys
* Other nodes can then ask for missing information

# Major design and architecture - contact

* Nodes do not know the relation of IP addresses to ID-codes of other nodes
* Therefore if a node wants to create a connection with certain ID-code it sends the following contact request to every node
* signed ip-address of the party sending the request encrypted with the public key of the other party + ID-code of other party

# Major design and architecture - Sending messages

* The previous process allows for contacts to know each others IP addresses, therefore they can create a direct TCP connection between each other
* The TCP connection is secured the following way:
* Both parties exchange own signature encrypted with the other party's ID-code - allowing them to verify the other party's identity without letting unauthorized parties know their own identity
* Symmetric key is then exchanged 

# Major design and architecture - relay servers
* It might happen that two peers that wish to communicate are behind NAT
* To solve this we use relay servers
* Any node not behind NAT acts as a relay server, they share the public keys and ip's of other nodes as usual
* Contact requests do not contain an IP addres but an unique identifier that represents the connection id between a client and a relay server
* Relay server acts as a middle man for the direct TCP connection(fully encrypted)


# Demonstration of the system

# Evaluation
* Security
    - Encryption methods used gurantee confidentiality, integrity and identification of other party
* Reliability
    - As long as there is at least 1 non-malicious node in the network, the messages reach their destination
* Performance
    - Any invalid information shared between nodes does not propagate further, therefore attacks are bounded by the attackers bandwith 

# Reflection, Conclusion, lessons learned and future work

* Overall the system is reliable and secure
* Many nodes provide high redundancy but do not scale well - a possible future work would be to set a cap for maximum amount of connected nodes
* Another future task could be to implement offline message sending
