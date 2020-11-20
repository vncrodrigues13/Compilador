package Exception;
import Buffer.Buffer;
import Scanner.ScannerCompilador;
public class OpChareNaoChar extends Exception{
    public OpChareNaoChar(){
        System.out.printf("ERRO: você tentou fazer uma operação utilizando CHAR e um"
        +"NÃO-CHAR na linha: %d e na coluna: %d",ScannerCompilador.getLinha()+1,Buffer.getColuna()+1);
    }
}
