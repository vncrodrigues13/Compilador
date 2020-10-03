package Scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Buffer.Buffer;
import Exception.*;
import Token.Token;

public class ScannerCompilador {
    private Buffer buffer;
    private BufferedReader leitor;
    private static int linha;
    private static String line;

    public ScannerCompilador(String pathToFile) throws IOException {
        leitor = new BufferedReader(new FileReader(pathToFile));
        buffer = new Buffer();
        this.linha = 0;
        line = leitor.readLine();
    }

    private Token lerCodigo() throws IOException, InteiroMalFormadoException, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        boolean acabouLinha = false;
        Token tokenBuffer;
        int contador = 0;
        while (this.line != null) {
            if (contador != this.linha) {
                // vai pular do começo do programa ate a linha em que estava sendo lida
                contador++;
                continue;
            } else {
                // caso esteja na linha que estava sendo lida anteriormente
                tokenBuffer = buffer.scan(line);
                
                if (tokenBuffer != null) {
                    return tokenBuffer;
                } else {
                    // se acabar a linha  (tokenBuffer == null)
                    line = leitor.readLine();
                    if (line != null){
                        this.linha++;
                    }
                }
            }
        }
        // Caso nao exista nenhuma linha de codigo ele retorna EOF;
        throw new EOF();

    }

    public Token getNextToken() throws IOException, InteiroMalFormadoException, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        return lerCodigo();
    }

    public static int getLinha() {
        return linha;
    }
}
