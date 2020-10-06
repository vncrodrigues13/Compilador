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
        try{
            while (line != null) {
                // caso esteja na linha que estava sendo lida anteriormente
                tokenBuffer = buffer.scan(line);
                System.out.println(tokenBuffer);
                if (tokenBuffer != null) {
                    return tokenBuffer;
                } else {
                    // se acabar a linha (tokenBuffer == null)
                    System.out.println("read line");
                    line = leitor.readLine();
                    if (line != null) {
                        linha++;
                    }
                }
            }
        }catch (StringIndexOutOfBoundsException e){
            if (Buffer.isComentarioMultiLinha()) {
                throw new EOFemComentarioMultilinha();
            }
            throw new EOF();
        }
        // Caso nao exista nenhuma linha de codigo ele retorna EOF;
        if (Buffer.isComentarioMultiLinha()) {
            throw new EOFemComentarioMultilinha();
        }
        throw new EOF();

    }

    public Token getNextToken() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
                System.out.println("Proximo token");
        return lerCodigo();
    }

    public static int getLinha() {
        return linha;
    }

}
