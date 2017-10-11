package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;

/**
 * Created by lbertoni on 08/10/2017.
 */
public class Client implements Runnable {

    private static Integer gameId;
    private static int[][] tabuleiro = new int[6][7];
    private static Integer jogador;
    private static TesteInterface testeInterface;
    private static Socket theSocket;
    private static PrintWriter out;

    public Client(TesteInterface testeInterface) {
        this.testeInterface = testeInterface;
    }

    private static Integer getOponent(Integer jogador) {
        if (jogador == null) return null;
        if (jogador == 1) return 2;
        return 1;
    }

    public void colocouPeca(int col) {
        inserePeca(Integer.valueOf(col), jogador);
        int vencedor = verificaVencedor();

        if (vencedor != 0 && vencedor != -1) {
            out.println(gameId + "/I WON" + "/" + col);
            out.flush();
            System.out.println("Você venceu!");
            inicializaTabuleiro();
            jogador = null;
            gameId = null;
        } else {
            if (vencedor == -1) {
                out.println(gameId + "/TIE" + "/" + col);
                out.flush();
                System.out.println("Empatou.");
                inicializaTabuleiro();
                jogador = null;
                gameId = null;
            } else {
                out.println(gameId + "/" + col);
                out.flush();
                System.out.println("Aguarde a jogada do seu oponente");
            }
        }
    }

    private static void play() {
        System.out.println("Clique na coluna onde a peça deve ser inserida.");
        testeInterface.setWaitingOponent(false);
    }

    public static int verificaVencedor() {

        int sequenciaCliente1 = 0;
        int sequenciaCliente2 = 0;
        boolean empate = true;
    /*
     * Verifica ganhador horizontal
     */

        for (int linha = 5; linha >= 0; linha--) {
            for (int coluna = 6; coluna >= 0; coluna--) {
                if (tabuleiro[linha][coluna] == 1) {
                    sequenciaCliente1++;
                    sequenciaCliente2 = 0;
                    if (sequenciaCliente1 == 4) {
                        return 1;
                    }
                }

                if (tabuleiro[linha][coluna] == 2) {
                    sequenciaCliente2++;
                    sequenciaCliente1 = 0;
                    if (sequenciaCliente2 == 4) {
                        return 2;
                    }
                }

                if (tabuleiro[linha][coluna] == 0) {
                    empate = false;
                    sequenciaCliente1 = 0;
                    sequenciaCliente2 = 0;
                }
            }
            sequenciaCliente1 = 0;
            sequenciaCliente2 = 0;
        }


    /*
     * Verifica ganhador horizontal
     */

        for (int coluna = 6; coluna >= 0; coluna--) {
            for (int linha = 5; linha >= 0; linha--) {
                if (tabuleiro[linha][coluna] == 1) {
                    sequenciaCliente1++;
                    sequenciaCliente2 = 0;
                    if (sequenciaCliente1 == 4) {
                        return 1;
                    }
                }

                if (tabuleiro[linha][coluna] == 2) {
                    sequenciaCliente2++;
                    sequenciaCliente1 = 0;
                    if (sequenciaCliente2 == 4) {
                        return 2;
                    }
                }

                if (tabuleiro[linha][coluna] == 0) {
                    empate = false;
                    sequenciaCliente1 = 0;
                    sequenciaCliente2 = 0;
                }
            }
            sequenciaCliente1 = 0;
            sequenciaCliente2 = 0;
        }

        /**
         * Verifica ganhador diagonal
         */

        int vencedorDiagonal;

        for (int coluna = 0; coluna < 7; coluna++) {
            vencedorDiagonal = verificaDiagonal1(coluna, 0);

            if (vencedorDiagonal != 0) {
                return vencedorDiagonal;
            }
        }

        for (int linha = 1; linha < 6; linha++) {
            vencedorDiagonal = verificaDiagonal1(0, linha);

            if (vencedorDiagonal != 0) {
                return vencedorDiagonal;
            }

            vencedorDiagonal = verificaDiagonal2(0, linha);

            if (vencedorDiagonal != 0) {
                return vencedorDiagonal;
            }
        }

        for (int coluna = 1; coluna < 7; coluna++) {

            vencedorDiagonal = verificaDiagonal2(coluna, 5);

            if (vencedorDiagonal != 0) {
                return vencedorDiagonal;
            }
        }

        if (empate) {
            return -1;
        }

        return 0;

    }

    private static int verificaDiagonal1(int coluna, int linha) {

        int sequenciaCliente1 = 0;
        int sequenciaCliente2 = 0;
        while (coluna <= 6 && linha <= 5) {

            if (tabuleiro[linha][coluna] == 1) {
                sequenciaCliente1++;
                sequenciaCliente2 = 0;
                if (sequenciaCliente1 == 4) {
                    return 1;
                }
            }

            if (tabuleiro[linha][coluna] == 2) {
                sequenciaCliente2++;
                sequenciaCliente1 = 0;
                if (sequenciaCliente2 == 4) {
                    return 2;
                }
            }

            if (tabuleiro[linha][coluna] == 0) {
                sequenciaCliente1 = 0;
                sequenciaCliente2 = 0;
            }

            coluna++;
            linha++;
        }

        return 0;
    }

    private static int verificaDiagonal2(int coluna, int linha) {

        int sequenciaCliente1 = 0;
        int sequenciaCliente2 = 0;
        while (coluna <= 6 && linha <= 5 && linha >= 0 && coluna >= 0) {

            if (tabuleiro[linha][coluna] == 1) {
                sequenciaCliente1++;
                sequenciaCliente2 = 0;
                if (sequenciaCliente1 == 4) {
                    return 1;
                }
            }

            if (tabuleiro[linha][coluna] == 2) {
                sequenciaCliente2++;
                sequenciaCliente1 = 0;
                if (sequenciaCliente2 == 4) {
                    return 2;
                }
            }

            if (tabuleiro[linha][coluna] == 0) {
                sequenciaCliente1 = 0;
                sequenciaCliente2 = 0;
            }

            coluna++;
            linha--;
        }

        return 0;
    }

    public static void inicializaTabuleiro() {
        for (int linha = 0; linha < 6; linha++) {
            for (int coluna = 0; coluna < 7; coluna++) {
                tabuleiro[linha][coluna] = 0;
            }
        }
    }

    public static boolean inserePeca(int coluna, int valor) {
        boolean inseriu = false;
        for (int linha = 5; linha >= 0; linha--) {
            if ((int) tabuleiro[linha][coluna] == 0) {
                tabuleiro[linha][coluna] = valor;
                inseriu = true;
                break;
            }
        }
        if (inseriu) {
            testeInterface.setWaitingOponent(true);
        }
        return inseriu;
    }


    @Override
    public void run() {
        try {
            theSocket = new Socket("localhost", 12111);
            out = new PrintWriter(theSocket.getOutputStream());

            BufferedReader networkIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(theSocket.getOutputStream());
            System.out.println("Connected to connect4 server");

            boolean gameStarted = false;

            while (true) {
                if (!gameStarted) {
                    String theLine = userIn.readLine();
                    if (theLine.equals(".")) {
                        break;
                    } else {
                        if (theLine.equals("start")) {
                            gameStarted = true;
                            out.println(theLine);
                            out.flush();
                        }
                    }
                }

                String response = networkIn.readLine();

                if (!response.startsWith("col") && !response.equals("could not find a game") && !response.contains("YOU LOSE") && !response.contains("TIE")) {
                    String[] r = response.split("/");
                    gameId = Integer.valueOf(r[0]);
                    if (response.contains("yourTurn")) {
                        jogador = 1;
                        testeInterface.setRedMove(jogador == 1);
                        System.out.println("É sua vez de jogar!");
                        play();
                    } else {
                        System.out.println("Aguarde a jogada do seu oponente.");
                        if (response.contains("notYourTurn")) {
                            jogador = 2;
                            testeInterface.setRedMove(jogador == 1);
                        }
                    }
                } else {
                    if (!response.equals("could not find a game") && response.startsWith("col")) {
                        String[] colValues = response.split("=");
                        int col = Integer.valueOf(colValues[1]);
                        inserePeca(Integer.valueOf(col), getOponent(jogador));
                        play();
                        Platform.runLater(() -> testeInterface.placeDisc(new TesteInterface.Disc(getOponent(jogador) == 1), col, false));
                        Thread.sleep(1000);
                    } else {
                        if (response.contains("YOU LOSE")) {
                            String[] lose = response.split("/");
                            int col = Integer.valueOf(lose[1]);
                            inserePeca(col, getOponent(jogador));

                            play();
                            try {
                                Platform.runLater(() -> testeInterface.placeDisc(new TesteInterface.Disc(getOponent(jogador) == 1), col, false));
                                Thread.sleep(1000);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            testeInterface.setWaitingOponent(true);
                            inicializaTabuleiro();
                            jogador = null;
                            gameId = null;
                            gameStarted = false;
                            System.out.println("Você perdeu :(");
                        } else {
                            if (response.contains("TIE")) {
                                String[] tie = response.split("/");
                                int col = Integer.valueOf(tie[1]);
                                inserePeca(col, getOponent(jogador));
                                play();
                                Platform.runLater(() -> testeInterface.placeDisc(new TesteInterface.Disc(getOponent(jogador) == 1), col, false));
                                Thread.sleep(1000);
                                testeInterface.setWaitingOponent(true);
                                inicializaTabuleiro();
                                jogador = null;
                                gameId = null;
                                gameStarted = false;
                                System.out.println("Empatou.");
                            } else {
                                if (response.equals("could not find a game")) {
                                    System.out.println("Estamos tentando encontrar um oponente, aguarde.");
                                }
                            }
                        }
                    }
                }
            }
            networkIn.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
