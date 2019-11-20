# ThingPlayground
An experimental playground for creating, storing, and accessing semantic Things

Quite simple, there is the device interface, which will be used by all those billion devices to send their descriptions. In this tiny story, we will use MQTT as the device-to-cloud saviour. Then there are the naive web clients which will get their cute little JSONs from the client interface. In between, is this box which is not all that clever. It offshores its smartness to an external graph database.
