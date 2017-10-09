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
    private static int jogador;

    public static void main(String[] args) throws Exception {
        String hostname = "localhost";

        Socket theSocket = new Socket(hostname, 12111);
        BufferedReader networkIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(theSocket.getOutputStream());
        System.out.println("Connected to echo server");

        while (true) {
            String theLine = userIn.readLine();
            if (theLine.equals(".")) {
                break;
            } else {
                if (theLine.equals("start")) {
                    out.println(theLine);
                    out.flush();
                }
            }

            String response = networkIn.readLine();
            if(!response.startsWith("col") && !response.equals("cound not find a game")) {
                String[] r = response.split("/");
                gameId = Integer.valueOf(r[0]);
                System.out.println(response);
                desenhaTabuleiro();
                if(response.contains("yourTurn")) {
                    jogador = 1;
                    System.out.println("HEY, IT'S MY TURN!!!");
                    getColumn(out);
                } else {
                    if(response.contains("notYourTurn")) {
                        jogador = 2;
                    }
                }
            } else {
                if(!response.equals("cound not find a game") && response.startsWith("col")) {
                    String[] colValues = response.split("=");
                    int col = Integer.valueOf(colValues[1]);
                    inserePeca(Integer.valueOf(col), jogador);
                    desenhaTabuleiro();
                    getColumn(out);
                } else {
                    System.out.println(response);
                }
            }
        }
        networkIn.close();
        out.close();
    }

    private static void getColumn(PrintWriter out) {
        System.out.println("Insert the column you want to put your piece in");
        Scanner s = new Scanner(System.in);
        String col = s.nextLine();
        out.println(gameId + "/" + col);
        out.flush();
        inserePeca(Integer.valueOf(col), jogador);
        desenhaTabuleiro();
    }

    public static int verificaVencedor(){

        int sequenciaCliente1 = 0;
        int sequenciaCliente2 = 0;

    /*
     * Verifica ganhador horizontal
     */

        for(int linha = 5; linha >= 0; linha--){
            for(int coluna = 6; coluna >= 0; coluna--){
                if(tabuleiro[linha][coluna] == 1){
                    sequenciaCliente1++;
                    sequenciaCliente2 = 0;
                    if(sequenciaCliente1 == 4){
                        return 1;
                    }
                }

                if(tabuleiro[linha][coluna] == 2){
                    sequenciaCliente2++;
                    sequenciaCliente1 = 0;
                    if(sequenciaCliente2 == 4){
                        return 2;
                    }
                }
            }
            sequenciaCliente1 = 0;
            sequenciaCliente2 = 0;
        }


    /*
     * Verifica ganhador horizontal
     */

        for(int coluna = 6; coluna >= 0; coluna--){
            for(int linha = 5; linha >= 0; linha--){
                if(tabuleiro[linha][coluna] == 1){
                    sequenciaCliente1++;
                    sequenciaCliente2 = 0;
                    if(sequenciaCliente1 == 4){
                        return 1;
                    }
                }

                if(tabuleiro[linha][coluna] == 2){
                    sequenciaCliente2++;
                    sequenciaCliente1 = 0;
                    if(sequenciaCliente2 == 4){
                        return 2;
                    }
                }
            }
            sequenciaCliente1 = 0;
            sequenciaCliente2 = 0;
        }

        return 0;

    }

    public static void inicializaTabuleiro(){
        for(int linha = 0; linha < 6; linha++){
            for(int coluna = 0; coluna < 7; coluna++){
                tabuleiro[linha][coluna] = 0;
            }
        }
    }

    public static void inserePeca(int coluna, int valor){

        for(int linha = 5; linha >= 0; linha--){
            if((int) tabuleiro[linha][coluna] == 0){
                tabuleiro[linha][coluna] = valor;
                break;
            }
        }

    }

    public static void desenhaTabuleiro(){

        int linha = 0;
        int coluna = 0;

        for(linha = 0; linha < 6; linha++){
            for(coluna = 0; coluna < 7; coluna++){
                System.out.print("|");
                System.out.print(desenha(tabuleiro[linha][coluna]));
            }
            System.out.println("|");
        }

    }

    public static String desenha(int entrada){
        if(entrada == 1) return "X";
        if(entrada == 2) return "O";
        else return " ";
    }
}
