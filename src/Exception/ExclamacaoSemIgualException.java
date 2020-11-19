package Exception;

import Scanner.ScannerCompilador;
import Buffer.Buffer;
public class ExclamacaoSemIgualException extends Exception{
    
    public ExclamacaoSemIgualException(){
        System.out.printf("Exclamação (‘!’) não seguida de ‘=’ na linha: %d e na coluna %d\n",ScannerCompilador.getLinha()+1,Buffer.getColuna()+1);
    }
}
