package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static String name;
    private ArrayList<String> topics = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {


        System.out.println("Client started.\nYour name:");
        name = scanner.nextLine();
        System.out.println("Enter your command :");
        String[] command = scanner.nextLine().split(" ");


        switch (command[2]){
            case "publish":
                handlePublishCommand(command);
                break;
            case "subscribe":
                for (int i = 3; i < command.length ; i++) {
                    handleSubscribeCommand(command, i);
                }
                break;
            case "ping":
                ping(command);
                break;
            default:
                System.out.println("bad command");

        }


    }

    private static void ping(String[] command) {
        String Host = command[0];
        int port = Integer.parseInt(command[1]);

        Thread t = new Thread(){
            public void run() {
                Socket socket = null;
                PrintWriter out = null;
                Scanner in = null;
                try {
                    socket = new Socket(Host, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new Scanner(socket.getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                long time = System.currentTimeMillis();

                out.println("ping "+name);
                //It is not necessary to wait
                while (System.currentTimeMillis() - time < 20000 );
                if(in.hasNextLine() && in.nextLine().equals("Pong"))
                    System.out.println("Pong from server received!");
                else
                    System.out.println("Server did not answer to ping");
            }


        };
        t.start();
    }


    private static void handleSubscribeCommand(String[] command, int i) {
        String Host = command[0];
        int port = Integer.parseInt(command[1]);



        Thread t = new Thread(){
            public void run() {
                Socket socket = null;
                PrintWriter out = null;
                Scanner in = null;
                try {
                     socket = new Socket(Host, port);
                     out = new PrintWriter(socket.getOutputStream(), true);
                     in = new Scanner(socket.getInputStream());

                } catch (IOException e) {
                        e.printStackTrace();
                }
                out.println("subscribe " + name + " " + command[i]);
                long time = System.currentTimeMillis();

                while ((System.currentTimeMillis() - time) < 10000 && !in.hasNextLine()) ;

                if (in.hasNextLine() && in.nextLine().equals("SubAck")) {
                    System.out.println("You subscribed on topic ( " + command[i] + " ) successfully !");

                }
                else {
                    System.out.println("Your subscribing on topic ( \"+ command[i] +\" ) failed !");
                    try {
                        socket.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while(true){
                  if(in.hasNextLine()) {
                      String input = in.nextLine();
                      if(input.equals("Ping")) {
                         // out.println("Pong");
                      }
                      else
                      System.out.println(input);
                  }

                }

            }


        };
        t.start();

      }


    private static void handlePublishCommand(String command[]) {
        String Host = command[0];
        int port = Integer.parseInt(command[1]);


        Socket socket = null;
        PrintWriter out = null;
        Scanner in = null;
        try {
            socket = new Socket(Host, port);
             out = new PrintWriter(socket.getOutputStream(), true);
             in = new Scanner(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder res = new StringBuilder();
        for (int j = 4 ; j < command.length ; j++){
            res.append(command[j]+ " ");
        }
        out.println("publish "+ command[3] + " " + res);
        long time = System.currentTimeMillis();

        while ((System.currentTimeMillis() - time) < 10000 && !in.hasNextLine());

        if(in.hasNextLine() && in.nextLine().equals("PubAck"))
            System.out.println("Your message published successfully!");
        else {
            System.out.println("Your message publishing failed!");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}