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
    private Token tokenCalcExprArit,tokenCalcTermo;
    private Stack<Token> pilhaCalcTermo;

    public Parser(ScannerCompilador scan) {
        this.scan = scan;
        token_atual = null;
        tabelaDeSimbolos = new TabelaDeSimbolos();
        listCalcExprArit = new ArrayList<>();
        tokenCalcExprArit = null;
        pilhaCalcTermo = new Stack<>();
    }

    public void programa() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
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
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
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

        if (token_atual.getTipo() >= 6 && token_atual.getTipo() <= 8) {

            getNextToken();
            do {
                existeVirgula = false;
                if (token_atual.getTipo() == 99) {
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
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
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
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException {
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
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
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
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <id> "=" <expr_arit> ";"
        Token idToken, exprAritToken;
        if (token_atual.getTipo() == 99) {
            idToken = token_atual;
            getNextToken();
            if (token_atual.getTipo() == 16) {
                exprAritToken = expr_arit();
                if (checarTipo(idToken, exprAritToken)) {
                    atribuirValor(idToken, exprAritToken);
                }
                getNextToken();
                if (token_atual.getTipo() == 24) {
                    System.out.println("end atribuicao");
                    // finalizar atribuicao
                }
            }
        }
    }

    public Token expr_relacional() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
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
        return calculoExpressaoArit();
    }

    public Token expr_arit() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        // <expr_arit> "+" <termo> | <expr_arit> "-" <termo> | <termo>
        
        listCalcExprArit.add(termo());

        if (token_atual.getTipo() == 40 || token_atual.getTipo() == 41) {
            // se houver soma ou subtracao
            listCalcExprArit.add(token_atual);
            if (token_atual.getTipo() == 40) {
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
        return calculoExpressaoArit();
        
    }

    public Token termo() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
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
        return calculoTermo();

    }

    public Token fator() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
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
            }else{
                return null;
            }
        } else {
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

    public void atribuirValor(Token tokenIdentificador, Token valor) {
        Simbolo identf = tabelaDeSimbolos.getSimbolo(tokenIdentificador);
        if (identf != null) {
            identf.setValor(valor);
        }
    }

    public Token calculoExpressaoArit() throws CharMalFormadoException, FloatMalFormadoException {
        Iterator iter = this.listCalcExprArit.iterator();
        Token operandSign = null;
        Token result = null;
        while (iter.hasNext()) {
            Token tk = (Token) iter.next();
            switch (tk.getTipo()) {
                case 6:
                    // caso seja inteiro
                    if (operandSign != null) {
                        if (tk.getTipo() == 6) {
                            int resultInt = Integer.parseInt(result.getLexema());
                            if (operandSign.getTipo() == 40) {
                                resultInt += Integer.parseInt(result.getLexema());
                            } else {
                                resultInt -= Integer.parseInt(result.getLexema());
                            }
                            operandSign = null;
                            result.setLexema(Integer.toString(resultInt));
                        } else {
                            Float resultFloat = Float.parseFloat(result.getLexema());
                            if (operandSign.getTipo() == 40) {
                                resultFloat += Float.parseFloat(tk.getLexema());
                            } else {
                                resultFloat -= Float.parseFloat(tk.getLexema());
                            }
                            operandSign = null;
                            result.setLexema(Float.toString(resultFloat));
                        }
                    } else {
                        result = new Token(tk.getLexema());
                    }
                    break;
                case 7:
                    // caso seja float
                    if (operandSign != null) {
                        if (result.getTipo() == 6) {
                            // se o result for do tipo int, passa a ser do tipo float
                            result.setTipo(7);
                        }
                        Float resultFloat = Float.parseFloat(result.getLexema());
                        if (operandSign.getTipo() == 40) {
                            resultFloat += Float.parseFloat(tk.getLexema());
                        } else {
                            resultFloat -= Float.parseFloat(tk.getLexema());
                        }
                        operandSign = null;
                        result.setLexema(Float.toString(resultFloat));
                    } else {
                        result = new Token(tk.getLexema());
                    }
                    break;
                default:
                    // caso seja um sinal
                    operandSign = tk;
                    break;
            }
        }
        return result;
    }

    public Token calculoTermo() throws CharMalFormadoException, FloatMalFormadoException {
        Token operand = null;
        Token result = null;
        
        while(pilhaCalcTermo.empty()){
            Token topoPilha = (Token)pilhaCalcTermo.pop();
            if (result == null){
                result = new Token(topoPilha.getLexema());
            }else{
                if (topoPilha.getTipo() == 42 || topoPilha.getTipo() == 43){
                    // caso seja um sinal -> '/' ou '*'
                    operand = topoPilha;
                }else{
                    //realizar operacoes
                    if (topoPilha.getTipo() == 6){
                        if (result.getTipo() == 6){
                            // caso a pilha seja int
                            int stackResult = Integer.parseInt(result.getLexema());
                            if (operand.getTipo() == 42){
                                // caso seja multiplicacao
                                stackResult *= Integer.parseInt(topoPilha.getLexema());
                            }else{
                                // caso seja divisao
                                stackResult /= Integer.parseInt(topoPilha.getLexema());
                            }
                            result.setLexema(Integer.toString(stackResult));
                        }else{
                            // caso a pilha seja float
                            float stackResult = Float.parseFloat(result.getLexema());
                            if (operand.getTipo() == 42){
                                // caso seja multiplicacao
                                stackResult *= Integer.parseInt(topoPilha.getLexema());
                            }else{
                                // caso seja divisao
                                stackResult /= Integer.parseInt(topoPilha.getLexema());
                            }
                            result.setLexema(Float.toString(stackResult));
                        }
                        
                    }else{
                        result.setTipo(7);
                        float stackResult = Float.parseFloat(result.getLexema());
                        if (topoPilha.getTipo() == 6){
                            if (operand.getTipo() == 42){
                                // caso seja '*'
                                stackResult *= Integer.parseInt(topoPilha.getLexema());
                            }else{
                                // caso seja '/'
                                stackResult /= Integer.parseInt(topoPilha.getLexema());
                            }
                        }else{
                            if (operand.getTipo() == 42){
                                // caso seja '*'
                                stackResult *= Float.parseFloat(topoPilha.getLexema());
                            }else{
                                // caso seja '/'
                                stackResult /= Float.parseFloat(topoPilha.getLexema());
                            }
                        }
                        result.setLexema(Float.toString(stackResult));
                    }
                    operand = null;
                }
            }
        }
        return result;
    }

}
