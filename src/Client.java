package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by lbertoni on 08/10/2017.
 */
public class Client {

    private static Integer gameId;
    private static int[][] tabuleiro = new int[6][7];
    private static Integer jogador;

    public static void main(String[] args) throws Exception {
        String hostname = "localhost";

        Socket theSocket = new Socket(hostname, 12111);
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
                desenhaTabuleiro();
                if (response.contains("yourTurn")) {
                    jogador = 1;
                    System.out.println("É sua vez de jogar!");
                    gameStarted = getColumn(out);
                } else {
                    System.out.println("Aguarde a jogada do seu oponente.");
                    if (response.contains("notYourTurn")) {
                        jogador = 2;
                    }
                }
            } else {
                if (!response.equals("could not find a game") && response.startsWith("col")) {
                    String[] colValues = response.split("=");
                    int col = Integer.valueOf(colValues[1]);
                    inserePeca(Integer.valueOf(col), getOponent(jogador));
                    desenhaTabuleiro();
                    gameStarted = getColumn(out);
                } else {
                    if (response.contains("YOU LOSE")) {
                        String[] lose = response.split("/");
                        int col = Integer.valueOf(lose[1]);
                        inserePeca(col, getOponent(jogador));
                        desenhaTabuleiro();

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
                            desenhaTabuleiro();

                            inicializaTabuleiro();
                            jogador = null;
                            gameId = null;
                            gameStarted = false;
                            System.out.println("Empatou.");
                        } else {
                            if(response.equals("could not find a game")) {
                                System.out.println("Estamos tentando encontrar um oponente, aguarde.");
                            }
                        }
                    }
                }
            }
        }
        networkIn.close();
        out.close();
    }

    private static Integer getOponent(Integer jogador) {
        if (jogador == null) return null;
        if (jogador == 1) return 2;
        return 1;
    }

    private static boolean getColumn(PrintWriter out) {
        System.out.println("Insira o número da coluna onde a peça deve ser inserida.");
        Scanner s = new Scanner(System.in);
        String col = s.nextLine();
        boolean inseriu = false;
        try {
            if (col == null || !(Integer.valueOf(col) >= 0 && Integer.valueOf(col) <= 6)) {
                System.out.println("Insira um número de coluna entre 0 e 6");
                return getColumn(out);
            } else {
                inseriu = inserePeca(Integer.valueOf(col), jogador);
            }
            if (!inseriu) {
                System.out.println("Insira a peça em uma coluna que não esteja cheia");
                return getColumn(out);
            }
        } catch (NumberFormatException e) {
            return getColumn(out);
        }
        desenhaTabuleiro();

        int vencedor = verificaVencedor();
        if (vencedor != 0 && vencedor != -1) {
            out.println(gameId + "/I WON" + "/" + col);
            out.flush();
            System.out.println("Você venceu!");
            inicializaTabuleiro();
            jogador = null;
            gameId = null;
            return false;
        } else {
            if (vencedor == -1) {
                out.println(gameId + "/TIE" + "/" + col);
                out.flush();
                System.out.println("Empatou.");
                inicializaTabuleiro();
                jogador = null;
                gameId = null;
                return false;
            } else {
                out.println(gameId + "/" + col);
                out.flush();
            }
        }

        return true;
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
                }
            }
            sequenciaCliente1 = 0;
            sequenciaCliente2 = 0;
        }

        if (empate) {
            return -1;
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
        return inseriu;
    }

    public static void desenhaTabuleiro() {

        int linha = 0;
        int coluna = 0;

        for (linha = 0; linha < 6; linha++) {
            for (coluna = 0; coluna < 7; coluna++) {
                System.out.print("|");
                System.out.print(desenha(tabuleiro[linha][coluna]));
            }
            System.out.println("|");
        }

        System.out.println();
    }

    public static String desenha(int entrada) {
        if (entrada == 1) return "X";
        if (entrada == 2) return "O";
        else return " ";
    }
}
