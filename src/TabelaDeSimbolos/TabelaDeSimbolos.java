package TabelaDeSimbolos;

import Token.Token;
import Simbolo.Simbolo;
import java.util.LinkedList;
import java.util.Iterator;



public class TabelaDeSimbolos {

    private LinkedList<Simbolo> tabela;


    public TabelaDeSimbolos(){
        tabela = new LinkedList<>();
    }

    public void addTabela(Simbolo simboloToAdd){
        if (!tabela.contains(simboloToAdd)){
            tabela.add(simboloToAdd);
        }
    }

    public void atualizarValor(Simbolo simboloToChange){
        if (tabela.contains(simboloToChange)){
            //atualizamos o Simbolo que precisamos;
            tabela.set(tabela.indexOf(getSimbolo(simboloToChange.getToken())), simboloToChange);
        }
    }


    public Simbolo getSimbolo(Token token){
        Iterator it = tabela.iterator();

        while (it.hasNext()){
            Simbolo simbolo = (Simbolo)it.next();
            if (simbolo.getToken().equals(token))
                return simbolo;
        }
        return null;
    }

    public boolean existeSimbolo(Token lexemaTest, int escopo){
        Iterator it = tabela.iterator();
        while (it.hasNext()){
            Simbolo simb = (Simbolo) it.next();
            if (simb.getToken().equals(lexemaTest) && escopo == simb.getEscopo()){
                return true;
            }
        }
        return false;
    }


    public void clearEscopo(int valorEscopo){
        Iterator it = tabela.iterator();

        while (it.hasNext()){
            Simbolo simbol = (Simbolo)it.next();
            if (simbol.getEscopo() == valorEscopo)
                tabela.remove(simbol);
        }
    }
}
