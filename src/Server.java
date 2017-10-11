package src;

import static src.Server.listClientesAtivos;
import static src.Server.listJogosAtivos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lbertoni on 08/10/2017.
 */
public class Server {

    // Variáveis para o jogo //
    static List<Socket> listClientesAtivos = new LinkedList<>();
    static Map<String, Integer> listClientesLastAck = new HashMap<String, Integer>();
    static List<Jogo> listJogosAtivos = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket m_ServerSocket = new ServerSocket(12111);
        int id = 0;
        while (true) {
            Socket clientSocket = m_ServerSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
            cliThread.start();
        }
    }
}

class ClientServiceThread extends Thread {
    Socket clientSocket;
    int clientID = -1;
    boolean running = true;

    ClientServiceThread(Socket s, int i) {
        clientSocket = s;
        clientID = i;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            while (running) {
                String clientCommand = in.readLine();
                System.out.println("Client Says :" + clientCommand);
                if (clientCommand.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.print("Stopping client thread for client : " + clientID);
                } else {
                    if (clientCommand.equals("start")) {
                        listClientesAtivos.add(clientSocket);
                        if (listClientesAtivos.size() > 1) {
                            for (Socket client : listClientesAtivos) {
                                if (!client.equals(clientSocket)) {
                                    startGame(client, clientSocket);
                                } else {
                                    out.println("could not find a game");
                                    out.flush();
                                }
                            }
                        } else {
                            out.println("could not find a game");
                            out.flush();
                        }
                    } else {
                        if (clientCommand.contains("I WON")) {
                            String[] columnInput = clientCommand.split("/");
                            String gameId = columnInput[0];
                            Integer col = Integer.valueOf(columnInput[2]);
                            Socket loser = findMyOponent(gameId, clientSocket);
                            PrintWriter oponentOut = new PrintWriter(new OutputStreamWriter(loser.getOutputStream()));
                            listJogosAtivos.remove(findGameById(gameId));
                            oponentOut.println("YOU LOSE/" + col);
                            oponentOut.flush();
                        } else {
                            if (clientCommand.contains("TIE")) {
                                String[] columnInput = clientCommand.split("/");
                                String gameId = columnInput[0];
                                Integer col = Integer.valueOf(columnInput[2]);
                                Socket tie = findMyOponent(gameId, clientSocket);
                                PrintWriter oponentOut = new PrintWriter(new OutputStreamWriter(tie.getOutputStream()));
                                listJogosAtivos.remove(findGameById(gameId));
                                oponentOut.println("TIE/" + col);
                                oponentOut.flush();
                            } else {
                                String[] columnInput = clientCommand.split("/");
                                String gameId = columnInput[0];
                                String column = columnInput[1];
                                Socket oponent = findMyOponent(gameId, clientSocket);
                                PrintWriter oponentOut = new PrintWriter(new OutputStreamWriter(oponent.getOutputStream()));
                                System.out.println("col=" + column);
                                Thread.sleep(500);
                                oponentOut.println("col=" + column);
                                oponentOut.flush();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Socket findMyOponent(String gameId, Socket clientSocket) {
        Jogo j = findGameById(gameId);
        if (j.getPlayer1().equals(clientSocket)) {
            return j.getPlayer2();
        } else {
            return j.getPlayer1();
        }
    }

    private Jogo findGameById(String gameId) {
        for (Jogo jogo : listJogosAtivos) {
            if (jogo.getId().equals(Integer.valueOf(gameId))) {
               return jogo;
            }
        }
        return null;
    }

    private void startGame(Socket player1, Socket player2) throws IOException {
        Jogo jogo = new Jogo(player1, player2);
        listJogosAtivos.add(jogo);
        listClientesAtivos.remove(player1);
        listClientesAtivos.remove(player2);
        PrintWriter player1Out = new PrintWriter(new OutputStreamWriter(player1.getOutputStream()));
        PrintWriter player2Out = new PrintWriter(new OutputStreamWriter(player2.getOutputStream()));
        player1Out.println(jogo.getId() + "/" + "yourTurn");
        player2Out.println(jogo.getId() + "/" + "notYourTurn");
        player1Out.flush();
        player2Out.flush();
    }
}

class Jogo {
    private Socket player1;
    private Socket player2;
    private static Integer id = 0;

    public Jogo(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.id++;
    }

    public Socket getPlayer1() {
        return player1;
    }

    public void setPlayer1(Socket player1) {
        this.player1 = player1;
    }

    public Socket getPlayer2() {
        return player2;
    }

    public void setPlayer2(Socket player2) {
        this.player2 = player2;
    }

    public static Integer getId() {
        return id;
    }
}

