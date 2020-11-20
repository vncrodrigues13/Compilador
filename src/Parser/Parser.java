package Parser;

import Token.Token;
import java.io.IOException;
import Exception.*;
import Scanner.ScannerCompilador;
import Simbolo.Simbolo;
import TabelaDeSimbolos.TabelaDeSimbolos;

public class Parser {
    private ScannerCompilador scan;
    private static Token token_atual;
    private boolean comentarioMultilinhaAberto;
    private TabelaDeSimbolos tabelaDeSimbolos;

    public Parser(ScannerCompilador scan) {
        this.scan = scan;
        token_atual = null;
        tabelaDeSimbolos = new TabelaDeSimbolos();
    }

    public void programa() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        getNextToken();
        if (token_atual.getValor() == 6) {
            getNextToken();
            if (token_atual.getValor() == 0) {
                getNextToken();
                if (token_atual.getValor() == 20) {
                    getNextToken();
                    if (token_atual.getValor() == 21) {
                        bloco();
                    }
                }
            }
        }
    }

    public void bloco() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        // “{“ {<decl_var>}* {<comando>}* '}'
        boolean createVar, createCommand;
        getNextToken();
        if (token_atual.getValor() == 22) {
            do {
                createVar = false;
                getNextToken();
                if (token_atual.getValor() >= 6 && token_atual.getValor() <= 8) {
                    createVar = true;
                    declararVariavel();
                }
            } while (createVar);
            // o ponteiro ja vem posicionado pelo parte de declaracao de var ^
            do {
                createCommand = false;
                if (token_atual.getValor() == 99 || token_atual.getValor() == 22 || token_atual.getValor() == 3
                        || token_atual.getValor() == 4 || token_atual.getValor() == 1) {
                    comando();
                    createCommand = true;
                    try{
                        getNextToken();
                    }catch (Exception e){
                        //quando ele busca algum token além do EOF
                    }
                }
            } while (createCommand);
            // <comando>
            if (token_atual.getValor() == 23) {
                // caso feche o bloco
                if (comentarioMultilinhaAberto){
                    throw new EOFemComentarioMultilinha();
                }
            }
        }
    }

    public void declararVariavel() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException {
        boolean existeVirgula = false;

        if (token_atual.getValor() >= 6 && token_atual.getValor() <= 8) {

            getNextToken();
            do {
                existeVirgula = false;
                if (token_atual.getValor() == 99) {
                    getNextToken();
                    if (token_atual.getValor() == 25) {
                        existeVirgula = true;
                        getNextToken();
                    }
                }
            } while (existeVirgula);

            if (token_atual.getValor() == 24) {
                System.out.println("End declarar var");
            }
        }
    }

    public void comando() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        if (token_atual.getValor() == 1) {
            // if '(' <exp_relacional> ')' '{' <comando> '}' else '{' <comando> '}'
            getNextToken();
            if (token_atual.getValor() == 20) {
                expr_relacional();
                if (token_atual.getValor() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    System.out.println("end if");
                    if (token_atual.getValor() == 2) {
                        // else
                        getNextToken();
                        comando();
                        System.out.println("end else");
                    }
                }
            }
        } else if (token_atual.getValor() == 3 || token_atual.getValor() == 4) {
            // para ser uma iteracao, o first tem que ser um "while" ou um "do"
            iteracao();
        } else if (token_atual.getValor() == 99 || token_atual.getValor() == 22) {
            // para ser um comando basico, o first tem que ser um <id> ou uma '{'
            // <comandoBasico> ==>> <atribuicao> || <bloco>
            comando_basico();
        }
    }

    public void comando_basico() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException {
        // atribuicao ou bloco
        if (token_atual.getValor() == 99) {
            // se for um identificador, ele vai formar uma atribuicao;
            atribuicao();
        } else if (token_atual.getValor() == 22) {
            // se houver uma chave, vai ser um bloco
            bloco();
        }
    }

    public void iteracao() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        // while "("<expr_relacional>")" <comando> | do <comando> while
        // "("<expr_relacional>")"";"
        if (token_atual.getValor() == 3) {
            // 'while'
            getNextToken();
            if (token_atual.getValor() == 20) {
                // abre parenteses
                getNextToken();
                expr_relacional();
                // expr relacional do while
                if (token_atual.getValor() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    // acabou o loop 'while'
                    System.out.println("End iteração while");
                }
            }
        } else if (token_atual.getValor() == 4) {
            getNextToken();
            comando();
            getNextToken();
            if (token_atual.getValor() == 3) {
                System.out.println("while");
                getNextToken();
                if (token_atual.getValor() == 20) {
                    getNextToken();
                    expr_relacional();
                    getNextToken();
                    if (token_atual.getValor() == 21) {
                        getNextToken();
                        if (token_atual.getValor() == 24) {
                            // acabou o loop. 'do while'
                            System.out.println("End iteração do while");
                        }
                    }
                }
            }
        }

    }

    public void atribuicao() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <id> "=" <expr_arit> ";"
        Token idToken, exprAritToken;
        if (token_atual.getValor() == 99) {
            idToken = token_atual;
            getNextToken();
            if (token_atual.getValor() == 16) {
                expr_arit();
                getNextToken();
                if (token_atual.getValor() == 24) {
                    System.out.println("end atribuicao");
                    // finalizar atribuicao
                }
            }
        }
    }

    public void expr_relacional() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <expr_arit> <op_relacional> <expr_arit>
        expr_arit();
        switch (token_atual.getValor()) {
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

    public void expr_arit() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <expr_arit> "+" <termo> | <expr_arit> "-" <termo> | <termo>
        termo();
        if (token_atual.getValor() == 40 || token_atual.getValor() == 41) {
            // se houver soma ou subtracao
            if (token_atual.getValor() == 40) {
                // caso seja uma soma
                System.out.println("SOMA");
            } else {
                // caso seja uma subtracao
                System.out.println("SUBTRACAO");
            }
            getNextToken();
            expr_arit();
        }

        System.out.println("end expr aritmetica");
    }

    public void termo() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <termo> "*" <fator> | <termo> '/' <fator> | <fator>
        // pego um fator e vejo se o proximo elemento tem uma '*' ou uma '/' se tiver eu
        // gero dou um loop com recursao
        fator();
        // pega o primeiro fator
        try {
            getNextToken();
        } catch (EOF e) {

        }
        // pega o token para validar se existe alguma multiplicacao ou divisao
        if (token_atual.getValor() == 42 || token_atual.getValor() == 43) {
            termo();
        }
        System.out.println("end termo");
    }

    public void fator() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // '(' <expr_arit> ')' | <id> | <float> | <inteiro> | <char>
        if (token_atual.getValor() == 20) {
            // abre parenteses
            getNextToken();
            expr_arit();
            getNextToken();
            if (token_atual.getValor() == 21) {
                // fecha parenteses
            }
        } else {
            switch (token_atual.getValor()) {
                case 90:
                    // caso seja int
                    break;
                case 91:
                    // caso seja float
                    break;
                case 92:
                    // caso seja char
                    break;
                case 99:
                    // caso seja <id>
                    break;
                default:
                    // caso nao seja <id> | <float> | <inteiro> | <char> | nem uma express reg
                    break;
            }
        }
    }

    private void getNextToken() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        token_atual = scan.getNextToken();

        if (token_atual.getValor() == 31){
            this.comentarioMultilinhaAberto = true;
            token_atual = scan.getNextToken();//ta dando EOF comentário multilinha
        }

        if (token_atual.getValor() == 32){
            this.comentarioMultilinhaAberto = false;
            token_atual = scan.getNextToken();
        }
    }


    private boolean checarTipo(Token primeiroToken, Token segundoToken){
        if (tabelaDeSimbolos.getSimbolo(primeiroToken) == null){
            // caso o primeiro simbolo seja null; 
        }
    }

}
