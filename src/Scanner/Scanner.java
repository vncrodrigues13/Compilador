package Scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Buffer.Buffer;
import Exception.IdentificadorMalFormado;
import Exception.exceptionFloatMalFormado;
import Exception.exclamacaoNaoSeguidaDeIgual;
import Exception.inteiroInvalido;
import Token.Token;

public class Scanner {
    private Buffer buffer;
    private BufferedReader leitor;
    private static int linha;

    public Scanner(String pathToFile) throws FileNotFoundException {
        leitor = new BufferedReader(new FileReader(pathToFile));
        buffer = new Buffer();
    }

    public void lerCodigo() throws IOException, inteiroInvalido, exceptionFloatMalFormado, IdentificadorMalFormado,
            exclamacaoNaoSeguidaDeIgual {
        boolean acabouLinha = false;
        // this.linha = 0;
        Token tokenBuffer;
        String line = leitor.readLine();
        while (line != null){
            // ler o arquivo
            // buffer.scan(line);
            acabouLinha = false;
            while (!acabouLinha){
                tokenBuffer = buffer.scan(line);
                if (tokenBuffer != null){
                    System.out.print(tokenBuffer.getValor() + " - ");
                }
                if (tokenBuffer == null){
                    System.out.println("Finish");
                    acabouLinha = true;
                }
            }
            // linha++;
            // System.out.println(line);
            line = leitor.readLine();
        }
        
    }

    public static int getLinha(){
        return linha;
    }
    
}
