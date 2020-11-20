package Parser;

import Token.Token;
import java.io.IOException;
import Exception.*;
import Scanner.ScannerCompilador;
import Simbolo.Simbolo;
import TabelaDeSimbolos.TabelaDeSimbolos;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class Parser {
    private ScannerCompilador scan;
    private static Token token_atual;
    private boolean comentarioMultilinhaAberto;
    private TabelaDeSimbolos tabelaDeSimbolos;
    private ArrayList<Token> listCalcExprArit;
    private Token tokenCalcExprArit, tokenCalcTermo;
    private Stack<Token> pilhaExprArit;
    private Stack<Token> pilhaCalcTermo;
    private Stack<Token> pilhaTermo;
    private int escopo;

    public Parser(ScannerCompilador scan) {
        this.scan = scan;
        token_atual = null;
        tabelaDeSimbolos = new TabelaDeSimbolos();
        listCalcExprArit = new ArrayList<>();
        tokenCalcExprArit = null;
        pilhaCalcTermo = new Stack<>();
        pilhaExprArit = new Stack<>();
        pilhaTermo = new Stack<>();
        escopo = 0;
    }

    public void programa() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        getNextToken();
        if (token_atual.getTipo() == 6) {
            getNextToken();
            if (token_atual.getTipo() == 0) {
                getNextToken();
                if (token_atual.getTipo() == 20) {
                    getNextToken();
                    if (token_atual.getTipo() == 21) {
                        bloco();
                    }
                }
            }
        }
    }

    public void bloco() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException, OpChareNaoChar {
        // “{“ {<decl_var>}* {<comando>}* '}'
        boolean createVar, createCommand;
        getNextToken();
        if (token_atual.getTipo() == 22) {
            do {
                createVar = false;
                getNextToken();
                if (token_atual.getTipo() >= 6 && token_atual.getTipo() <= 8) {
                    createVar = true;
                    declararVariavel();
                }
            } while (createVar);
            // o ponteiro ja vem posicionado pelo parte de declaracao de var ^
            do {
                createCommand = false;
                if (token_atual.getTipo() == 99 || token_atual.getTipo() == 22 || token_atual.getTipo() == 3
                        || token_atual.getTipo() == 4 || token_atual.getTipo() == 1) {
                    comando();
                    createCommand = true;
                    try {
                        getNextToken();
                    } catch (Exception e) {
                        // quando ele busca algum token além do EOF
                    }
                }
            } while (createCommand);
            // <comando>
            if (token_atual.getTipo() == 23) {
                // caso feche o bloco
                if (comentarioMultilinhaAberto) {
                    throw new EOFemComentarioMultilinha();
                }
            }
        }
    }

    public void declararVariavel() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException {
        boolean existeVirgula = false;
        Token id, tipo;

        if (token_atual.getTipo() >= 6 && token_atual.getTipo() <= 8) {
            tipo = token_atual;
            getNextToken();
            do {
                existeVirgula = false;
                if (token_atual.getTipo() == 99) {
                    id = token_atual;
                    if (varDisponivel(id)){ //verifica se a variavel n existe
                        tabelaDeSimbolos.addTabela(new Simbolo(id, escopo, tipo.getTipo()));
                    }
                    getNextToken();
                    if (token_atual.getTipo() == 25) {
                        existeVirgula = true;
                        getNextToken();
                    }
                }
            } while (existeVirgula);

            if (token_atual.getTipo() == 24) {
                System.out.println("End declarar var");
            }
        }
    }

    public void comando() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException, OpChareNaoChar {
        if (token_atual.getTipo() == 1) {
            // if '(' <exp_relacional> ')' '{' <comando> '}' else '{' <comando> '}'
            getNextToken();
            if (token_atual.getTipo() == 20) {
                expr_relacional();
                if (token_atual.getTipo() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    System.out.println("end if");
                    if (token_atual.getTipo() == 2) {
                        // else
                        getNextToken();
                        comando();
                        System.out.println("end else");
                    }
                }
            }
        } else if (token_atual.getTipo() == 3 || token_atual.getTipo() == 4) {
            // para ser uma iteracao, o first tem que ser um "while" ou um "do"
            iteracao();
        } else if (token_atual.getTipo() == 99 || token_atual.getTipo() == 22) {
            // para ser um comando basico, o first tem que ser um <id> ou uma '{'
            // <comandoBasico> ==>> <atribuicao> || <bloco>
            comando_basico();
        }
    }

    public void comando_basico() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException, OpChareNaoChar {
        // atribuicao ou bloco
        if (token_atual.getTipo() == 99) {
            // se for um identificador, ele vai formar uma atribuicao;
            atribuicao();
        } else if (token_atual.getTipo() == 22) {
            // se houver uma chave, vai ser um bloco
            bloco();
        }
    }

    public void iteracao() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException, OpChareNaoChar {
        // while "("<expr_relacional>")" <comando> | do <comando> while
        // "("<expr_relacional>")"";"
        if (token_atual.getTipo() == 3) {
            // 'while'
            getNextToken();
            if (token_atual.getTipo() == 20) {
                // abre parenteses
                getNextToken();
                expr_relacional();
                // expr relacional do while
                if (token_atual.getTipo() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    // acabou o loop 'while'
                    System.out.println("End iteração while");
                }
            }
        } else if (token_atual.getTipo() == 4) {
            getNextToken();
            comando();
            getNextToken();
            if (token_atual.getTipo() == 3) {
                System.out.println("while");
                getNextToken();
                if (token_atual.getTipo() == 20) {
                    getNextToken();
                    expr_relacional();
                    getNextToken();
                    if (token_atual.getTipo() == 21) {
                        getNextToken();
                        if (token_atual.getTipo() == 24) {
                            // acabou o loop. 'do while'
                            System.out.println("End iteração do while");
                        }
                    }
                }
            }
        }

    }

    public void atribuicao() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <id> "=" <expr_arit> ";"
        Token idToken, exprAritToken;
        if (token_atual.getTipo() == 99) {
            idToken = token_atual;
            getNextToken();
            if (token_atual.getTipo() == 16) {
                getNextToken();
                exprAritToken = expr_arit();
                System.exit(0);
                if (checarTipoAtribuicao(idToken, exprAritToken)) {
                    atribuirValor(idToken, exprAritToken);
                }else{
                    System.out.println("ATRIBUICAO INVALIDA");
                    System.exit(0);
                }
                getNextToken();
                if (token_atual.getTipo() == 24) {
                    System.out.println("end atribuicao");
                    // finalizar atribuicao
                }
            }
        }
    }

    public void expr_relacional() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <expr_arit> <op_relacional> <expr_arit>
        expr_arit();
        switch (token_atual.getTipo()) {
            case 10:
                // igual
                getNextToken();
                expr_arit();
                break;
            case 11:
                // diferente
                getNextToken();
                expr_arit();
                break;
            case 12:
                // maior que
                getNextToken();
                expr_arit();
                break;
            case 13:
                // maior igual a
                getNextToken();
                expr_arit();
                break;
            case 14:
                // menor que
                getNextToken();
                expr_arit();
                break;
            case 15:
                // menor igual a
                getNextToken();
                expr_arit();
                break;
        }
        System.out.println("end expr relacional");
    }

    public Token expr_arit() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <expr_arit> "+" <termo> | <expr_arit> "-" <termo> | <termo>
        Token termoToken;
        termoToken = termo();
        pilhaExprArit.push(termoToken);
        
        if (token_atual.getTipo() == 40 || token_atual.getTipo() == 41) {
            // se houver soma ou subtracao
            
            pilhaExprArit.push(token_atual);
            getNextToken();
            expr_arit();
        }
        return new Token (definirTipoOperacao(pilhaExprArit));
    }

    public Token termo() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <termo> "*" <fator> | <termo> '/' <fator> | <fator>
        // pego um fator e vejo se o proximo elemento tem uma '*' ou uma '/' se tiver eu
        // gero dou um loop com recursao
        pilhaCalcTermo.push(fator());
        // pega o primeiro fator
        try {
            getNextToken();
        } catch (EOF e) {
        }
        // pega o token para validar se existe alguma multiplicacao ou divisao
        if (token_atual.getTipo() == 42 || token_atual.getTipo() == 43) {
            pilhaCalcTermo.push(token_atual);
            getNextToken();
            termo();
        }
        System.out.println("end termo");
        return new Token(definirTipoOperacao(pilhaCalcTermo));
    }

    public Token fator() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // '(' <expr_arit> ')' | <id> | <float> | <inteiro> | <char>
        Token exprArit;
        if (token_atual.getTipo() == 20) {
            // abre parenteses
            getNextToken();
            exprArit = expr_arit();
            getNextToken();
            if (token_atual.getTipo() == 21) {
                // fecha parenteses
                return exprArit;
            } else {
                return null;
            }
        } else {
            if (token_atual.getTipo() == 99) {
                Simbolo simbolToken = tabelaDeSimbolos.getSimbolo(token_atual);
                return simbolToken.getValor();
            }
            return token_atual;
        }
    }

    private void getNextToken() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        token_atual = scan.getNextToken();

        if (token_atual.getTipo() == 31) {
            this.comentarioMultilinhaAberto = true;
            token_atual = scan.getNextToken();// ta dando EOF comentário multilinha
        }

        if (token_atual.getTipo() == 32) {
            this.comentarioMultilinhaAberto = false;
            token_atual = scan.getNextToken();
        }
    }

    public boolean varDisponivel(Token id){
        //se o simbolo não existir, então ele está disponivel -> true
        //se o simbolo existir, então ele não está disponivel -> false
        return !tabelaDeSimbolos.existeSimbolo(id,escopo);
    }

    private boolean checarTipo(Token primeiroToken, Token segundoToken) {
        if (primeiroToken.getTipo() == 99 && segundoToken.getTipo() == 99) {
            // se ambos os valores forem identificadores, precisamos checar os tipos dos
            // identificadores
            Simbolo primeiroSimbolo = tabelaDeSimbolos.getSimbolo(primeiroToken);
            Simbolo segundoSimbolo = tabelaDeSimbolos.getSimbolo(segundoToken);
            return primeiroSimbolo.getTipo() == segundoSimbolo.getTipo();
        } else if (primeiroToken.getTipo() == 99 && segundoToken.getTipo() != 99) {

            // verifica se o tipo do identificador e o tipo do Token são iguais

            Simbolo primeiroSimbolo = tabelaDeSimbolos.getSimbolo(primeiroToken);

            return primeiroSimbolo.getTipo() == segundoToken.getTipo();

        } else if (primeiroToken.getTipo() != 99 && segundoToken.getTipo() == 99) {
            // verifica se o tipo do identificador e o tipo do Token são iguais
            Simbolo segundSimbolo = tabelaDeSimbolos.getSimbolo(segundoToken);
            return primeiroToken.getTipo() == segundSimbolo.getTipo();
        } else {
            // caso nenhum dos dois tokens sejam identificadores

            return primeiroToken.getTipo() == segundoToken.getTipo();

        }
    }

    public boolean checarTipoAtribuicao(Token tokenIdentificador, Token valor){
        Simbolo simboloIdentificador = tabelaDeSimbolos.getSimbolo(tokenIdentificador);
        if (simboloIdentificador != null){  //se o simbolo existir
            if (valor.getTipo() == 91){
                 //se for um float ou um char, ele só pode ser atribuido a um símbolo do mesmo tipo

                return simboloIdentificador.getTipo() == 7; //se o identificador for do mesmo tipo do valor

            }else if (valor.getTipo() == 92){

                return simboloIdentificador.getTipo() == 8;

            }else if (valor.getTipo() == 90){
                // se for um inteiro, ele pode ser atribuido a um Simbolo inteiro ou um Simbolo float
                
                return simboloIdentificador.getTipo() == 6 || 
                simboloIdentificador.getTipo() == 7; //se o identificador for um float

            }else if (valor.getTipo() == 99){
                // caso queira atribuir o valor de uma variavel a outra

                Simbolo simboloValor = tabelaDeSimbolos.getSimbolo(valor);
                return checarTipoAtribuicao(tokenIdentificador, simboloValor.getValor());

            }
        }
        return false;
    }

    public void atribuirValor(Token tokenIdentificador, Token valor) {
        Simbolo identf = tabelaDeSimbolos.getSimbolo(tokenIdentificador);
        tabelaDeSimbolos.atualizarValor(identf);
    }

    public Integer definirTipoOperacao(Stack<Token> pilha) throws OpChareNaoChar {
        Integer tipo = null;
        while (!pilha.empty()) {
            Token topo = (Token)pilha.pop();

            if (topo.getTipo() == 99) {
                Simbolo topoSimbolo = tabelaDeSimbolos.getSimbolo(topo);
                topo = topoSimbolo.getValor();
            }
            
            if (topo.getTipo() == 90) {

                if (tipo == null) {
                    tipo = 90;
                }
                if (tipo == 92) {
                    throw new OpChareNaoChar();
                }
            } else if (topo.getTipo() == 91) {
                if (tipo == null) {
                    tipo = 91;
                }else if (tipo == 92) {
                    throw new OpChareNaoChar();
                }else if (tipo == 90){
                    tipo = 91;
                }
            } else if (topo.getTipo() == 92) {
                // se for um char
                if (tipo == null) {
                    tipo = 92;
                }else if (tipo != 92) {
                    throw new OpChareNaoChar();
                }
            }else if (topo.getTipo() == 43){
                // caso tenha uma divisao, return vai ser do tipo float
                if (tipo == 90){
                    tipo = 91;
                }
            }
            
        }
        
        System.out.println(tipo);
        return tipo;
    }

    

}
