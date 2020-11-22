package Exception;
import Buffer.Buffer;
import Scanner.ScannerCompilador;
public class OpChareNaoChar extends Exception{
    public OpChareNaoChar(){
        System.out.printf("ERRO: voce tentou fazer uma operacao utilizando CHAR e um "
        +"NAO-CHAR na linha: %d e na coluna: %d\n",ScannerCompilador.getLinha()+1,Buffer.getColuna());
        System.exit(0);
    }
}
