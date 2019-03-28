# Teaching-HEIGVD-RES-2019-Exercise-Calculator
### Author Bruno Legrand

* What transport protocol do we use?


    We should use TCP-IP protocole transport to guarantee  request and responce to be send


* How does the client find the server (addresses and ports)?

        server address could be static as well as the port. For example server address 10.192.105.200, port 3333


* Who speaks first?

        Client speaks first to establish connection and send request.


* What is the sequence of messages exchanged by the client and the server?

        * Client send resquest calculus
        * Server send answer calculus

```sequence
participant client
participant server

Note over server : Server listening on port 3333

client->server: Open connection
Note over server : Server accept connection
server->client: Greetings with command instructions

client->server: send calculus
Note over server : Calculate

server->client: Send result
Note over client,server : ...
client-> server : close connection
server->client: good bye message



```


* What happens when a message is received from the other party?

        Server recieve message from client :
        * client message is well formed, server send answer calculus
        * client message is not well formed, server send error message
        Client recieve message from server : 
        * if message is a responce well formed for a previous request, client accept message
        * client send error message


* What is the syntax of the messages? How we generate and parse them?

        The communication from trhe client is a string : "<operand_1> <operator> <operand_2>"
        The delimiter is the space char ' ' and the end of transmission is the break line char '\n'
        The response from the server is string : "<result>"
        The greeting and good bye message are in string format : "<message>"

* Who closes the connection and when?

        * Client can close connection on user demand: on bye command

