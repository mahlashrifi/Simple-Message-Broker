package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;



public class Server {
    //topic -> List of <Client name, Socket > entry
    static HashMap <String, ArrayList<AbstractMap.SimpleEntry<String, Socket>>> topicsOfClients = new HashMap<>();



    public static void main(String[] args) throws IOException {
        final int PORT = 4040;
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Server started...");
        System.out.println("Waiting for clients...");



        while (true) {

            Socket clientSocket = serverSocket.accept();

            Thread t = new Thread() {
                public void run() {

                    Socket sock = null;
                    PrintWriter out = null;
                    Scanner in = null;
                    try {
                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new Scanner(clientSocket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    {
                        String[] input = in.nextLine().split(" ");

                            switch (input[0]) {
                                case "publish":

                                    publish(out, input);
                                    break;
                                case "subscribe":
                                    subscribe(clientSocket, input);
                                    break;
                                case "ping":
                                    answerPing(clientSocket, input);
                                    break;
                                default:
                                    System.out.println("bad command");
                            }

                    }


                }
            };
            t.start();
        }
    }

    private static void answerPing(Socket clientSocket, String[] input) {
        PrintWriter out = null;
        Scanner in = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("Pong");
    }

    public static void publish(PrintWriter printerOfClient, String[] input){
        String topic = input[1];
        if (topicsOfClients.containsKey(topic)){
            for (AbstractMap.SimpleEntry<String, Socket> clientNameToSocket : topicsOfClients.get(topic)) {
                Socket socket = clientNameToSocket.getValue();
                if (!socket.isClosed()) {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        StringBuilder res = new StringBuilder();
                        for (int j = 2 ; j < input.length ; j++){
                            res.append(input[j]+ " ");
                        }

                        out.println("< " + topic + " > : " + res);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        printerOfClient.println("PubAck");
    }
    public static void subscribe(Socket socket, String[] input){
        String topic = input[2];
        String name = input[1];

        PrintWriter printer = null;
        try {
            printer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!topicsOfClients.containsKey(input[2])){
           topicsOfClients.put(topic, new ArrayList<>());
        }

        AbstractMap.SimpleEntry<String, Socket> pairOfNameAndSocket = new AbstractMap.SimpleEntry<>(name, socket);
        topicsOfClients.get(input[2]).add(pairOfNameAndSocket);
        printer.println("SubAck");
        System.out.println(name +" subscribed on topic ( " + topic + " ) successfully !");
        periodicPing(topic, name, socket);

    }
    public static void periodicPing(String topic, String name, Socket socket){

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            int i = 0 ;
            @Override
            public void run() {
                PrintWriter out = null;
                Scanner scanner = null;
                long time = System.currentTimeMillis();
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    scanner = new Scanner(socket.getInputStream());
                    out.println("Ping");
                    while (System.currentTimeMillis()- time < 2000);

                    if(!socket.isClosed()  && socket.getInputStream().available() != 0 && scanner.nextLine().equals("Pong"))
                        System.out.println("Pong from " + name + " / Topic Of Connection = " + topic );

                    else {
                        i++;
                        System.out.println("Client (" + name + ") not answered to ping (ip = " + socket.getInetAddress() + " and port = " + socket.getLocalPort() + " )");
                    }

                    time = System.currentTimeMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(i >= 3) {
                    timer.cancel();
                    deleteClientConnections(name);
                    System.out.println("Connections of ( " + name +" ) deleted successfully! (Client did not send Pong 3 times)");
                }

            }
        }, 0, 10000);

    }

    private static void deleteClientConnections(String name) {
        for (String topic : topicsOfClients.keySet()) {
            for (AbstractMap.SimpleEntry<String, Socket> entry : topicsOfClients.get(topic)) {
                if(entry.getKey().equals(name)) {
                    try {
                        entry.getValue().close();
                        topicsOfClients.get(topic).remove(topic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}