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
        linha = 0;
        line = leitor.readLine();
    }

    private Token lerCodigo() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        Token tokenBuffer;

        while (line != null) {
            // caso esteja na linha que estava sendo lida anteriormente
            tokenBuffer = buffer.scan(line);
            if (tokenBuffer != null) {
                return tokenBuffer;
            } else {
                // se acabar a linha (tokenBuffer == null)
                line = leitor.readLine();
                if (line != null) {
                    linha++;
                }
            }
        }

        if (Buffer.isComentarioMultiLinha()) {
            throw new EOFemComentarioMultilinha();
        }else{
            throw new EOF();
        }
        // Caso nao exista nenhuma linha de codigo ele retorna EOF;

    }

    public Token getNextToken() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        return lerCodigo();
    }

    public static int getLinha() {
        return linha;
    }

}
