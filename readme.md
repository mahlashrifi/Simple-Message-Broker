## Simple Message Broker

This is practical project of "Computer Network" course at Amirkabir University of technology, implemented in Spring 2022

In this message broker, clients can publish messages under topics of their choice. The server (broker) then ensures the delivery of these messages to the clients which previously subscribed to that topic. interaction between broker and clients are like below picture:



![Broker and Client Interaction](https://github.com/mahlashrifi/Simple-Message-Broker/blob/master/mb.png)



## Features

- **Publish/Subscribe Mechanism**: Clients can publish messages under specific topics, which are then relayed by the broker to interested subscribers.

- **Dynamic Client Interaction**: Clients can subscribe to topics of interest to receive updates from the broker when new messages are published.

- **Network Resilience**: Implements a ping/pong interaction for connection health checks between clients and the broker.

- **Acknowledgments**: The system sends acknowledgments (PubAck and SubAck) to confirm the successful operation of publish and subscribe actions.

  

## Components

- **Server (Broker)**: Manages message distribution and maintains a list of topics and subscriber interest.
- **Client**: Initiates publish or subscribe commands, interacts with the server to exchange messages, and performs network connectivity checks.



