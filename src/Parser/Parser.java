package Parser;

import Token.Token;

import java.io.BufferedWriter;
import java.io.IOException;
import Exception.*;
import Scanner.ScannerCompilador;
import Simbolo.Simbolo;
import TabelaDeSimbolos.TabelaDeSimbolos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Stack;
import Buffer.Buffer;

public class Parser {
    private ScannerCompilador scan;
    private static Token token_atual;
    private boolean comentarioMultilinhaAberto;
    private TabelaDeSimbolos tabelaDeSimbolos;
    private Stack<Token> pilhaExprArit;
    private Stack<Token> pilhaTermo;
    private int escopo;
    private int contadorRegistradorTemporario;
    private Token resultExpr, resultTermo;
    private Stack<Token> saveOperation;
    private int labelsQuantidade;
    int valorLabelIfElse = 0;
    int valueif;
    int saveGoto = 0, saveElse = 0;

    public Parser(ScannerCompilador scan) {
        this.scan = scan;
        token_atual = null;
        tabelaDeSimbolos = new TabelaDeSimbolos();
        pilhaTermo = new Stack<>();
        pilhaExprArit = new Stack<>();
        pilhaTermo = new Stack<>();
        escopo = 0;
        contadorRegistradorTemporario = 0;
        saveOperation = new Stack<>();
        labelsQuantidade = 0;
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

                        System.out.printf("label%d: \n", labelsQuantidade);
                        labelsQuantidade++;
                        getNextToken();
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
                    if (varDisponivel(id)) { // verifica se a variavel n existe
                        tabelaDeSimbolos.addTabela(new Simbolo(id, escopo, tipo.getTipo()));
                    } else {
                        System.out.printf(
                                "\tERRO na linha: %d e na coluna: %d. Variavel \'%s\' ja existente. Ultimo Token lido foi \'%s\'",
                                ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, id.getLexema(),
                                token_atual.getLexema());
                        System.exit(0);
                    }
                    getNextToken();
                    if (token_atual.getTipo() == 25) {
                        existeVirgula = true;
                        getNextToken();
                    }
                } else {
                    System.out.printf(
                            "\tERRO: Nome de variavel invalido. Na linha: %d, na coluna: %d. O ultimo Token lido foi: %s\n",
                            ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                    System.exit(0);
                }
            } while (existeVirgula);

            if (token_atual.getTipo() == 24) {
            }
        }
    }

    public void comando() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException, OpChareNaoChar {
        // if "("<expr_relacional>")" <comando> {else <comando>}?
        if (token_atual.getTipo() == 1) {
            escopo++;
            getNextToken();
            if (token_atual.getTipo() == 20) {
                // abre parenteses
                getNextToken();
                expr_relacional();
                saveGoto = labelsQuantidade-1;
                saveElse = labelsQuantidade -1;
                if (token_atual.getTipo() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    tabelaDeSimbolos.clearEscopo(escopo);
                    escopo--;
                    if (token_atual.getTipo() == 23){
                        getNextToken();
                    }
                    if (token_atual.getTipo() == 2) {
                        // else
                        System.out.printf("\tgoto label%d\n", saveGoto);
                        System.out.printf("label%d:\n",saveElse);
                        labelsQuantidade++;
                        escopo++;
                        getNextToken();
                        comando();
                        tabelaDeSimbolos.clearEscopo(escopo);
                        escopo--;
                        System.out.printf("label%d:\n", labelsQuantidade);
                        labelsQuantidade++;
                    }else{
                        System.out.printf("label%d:\n",saveGoto);
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
        int valorCompWhile = 0;

        if (token_atual.getTipo() == 3) {
            int labelIteracao = labelsQuantidade;
            System.out.printf("label%d:\n", labelsQuantidade);
            labelsQuantidade++;
            escopo++;
            // 'while'
            getNextToken();
            if (token_atual.getTipo() == 20) {
                // abre parenteses
                getNextToken();
                expr_relacional();
                valorCompWhile = labelsQuantidade-1;
                labelsQuantidade++;
                // expr relacional do while
                if (token_atual.getTipo() == 21) {
                    // fecha parenteses
                    getNextToken();
                    comando();
                    System.out.printf("\tgoto label%d\n", labelIteracao);
                    System.out.printf("label%d:\n",valorCompWhile);
                    // acabou o loop 'while'
                    tabelaDeSimbolos.clearEscopo(escopo);
                    escopo--;
                }
            }
        } else if (token_atual.getTipo() == 4) {
            // do <comando> while
            int labelIteracao = labelsQuantidade;
            System.out.printf("label%d:\n", labelIteracao);
            labelsQuantidade++;
            escopo++;
            getNextToken();
            comando();
            if (token_atual.getTipo() == 23){
                getNextToken();
            }
            if (token_atual.getTipo() == 3) {
                getNextToken();
                if (token_atual.getTipo() == 20) {
                    getNextToken();
                    expr_relacional();
                    if (token_atual.getTipo() == 21) {
                        getNextToken();
                        if (token_atual.getTipo() == 24) {
                            // acabou o loop. 'do while'
                            System.out.printf("\tgoto label%d\n", labelIteracao);
                            //esse label quantidade de que deveria ser visto

                            //esse valor de labelsQuantidade ta alterando
                            System.out.printf("label%d: \n", labelsQuantidade-1);
                            tabelaDeSimbolos.clearEscopo(escopo);
                            escopo--;
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
                if (checarTipoAtribuicao(idToken, exprAritToken)) {
                    prepararString(saveOperation);
                    System.out.printf("\t%s = _t%d\n", idToken.getLexema(), contadorRegistradorTemporario - 1);
                    exprAritToken.setLexema("_t" + (contadorRegistradorTemporario - 1));
                    atribuirValor(idToken, exprAritToken);

                } else {
                    System.out.printf(
                            "\tATRIBUICAO INVALIDA na linha: %d e na coluna: %d. O ultimo Token lido foi: \'%s\'\n",
                            ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                    System.exit(0);
                }
                if (token_atual.getTipo() == 24) {
                    // finalizar atribuicao
                }
            }
        }
    }

    public void expr_relacional() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <expr_arit> <op_relacional> <expr_arit>
        Stack<Token> pilhaExprRelacional = new Stack<>();
        Token primeiroFator, segundoFator, operando;
        primeiroFator = expr_arit();
        prepararString(saveOperation);

        segundoFator = null;
        operando = token_atual;

        switch (token_atual.getTipo()) {
            case 10:
                // igual
                getNextToken();
                segundoFator = expr_arit();
                break;
            case 11:
                // diferente
                getNextToken();
                segundoFator = expr_arit();
                break;
            case 12:
                // maior que
                getNextToken();
                segundoFator = expr_arit();
                break;
            case 13:
                // maior igual a
                getNextToken();
                segundoFator = expr_arit();
                break;
            case 14:
                // menor que
                getNextToken();
                segundoFator = expr_arit();
                break;
            case 15:
                // menor igual a
                getNextToken();
                segundoFator = expr_arit();
                break;
        }
        prepararString(saveOperation);

        pilhaExprRelacional.push(primeiroFator);
        pilhaExprRelacional.push(operando);
        pilhaExprRelacional.push(segundoFator);
        if (checarTipo(primeiroFator, segundoFator)) {
            // se for compatível...
            preparar_string_expr_relacional(pilhaExprRelacional);
        } else {
            System.out.printf(
                    "\tERRO na linha: %d e na coluna: %d. Comparacao com tipos incompativeis. O ultimo token lido foi: \'%s\'",
                    ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
            System.exit(0);
        }

    }

    public Token expr_arit() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <expr_arit> "+" <termo> | <expr_arit> "-" <termo> | <termo>

        pilhaExprArit.push(termo());

        if (token_atual.getTipo() == 40 || token_atual.getTipo() == 41) {
            // se houver soma ou subtracao
            saveOperation.push(token_atual);
            pilhaExprArit.push(token_atual);
            getNextToken();
            expr_arit();
        }

        if (!pilhaExprArit.empty()) {

            resultExpr = definirTipoOperacao(pilhaExprArit);
            if (resultExpr.getLexema() != null && !resultExpr.isIdentificador(resultExpr.getLexema())) {
                resultExpr.setName("_t" + contadorRegistradorTemporario);
            } else if (resultExpr.getLexema() == null) {
                resultExpr.setLexema("_t" + contadorRegistradorTemporario);
            }
        }

        return resultExpr;

    }

    public Token termo() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        // <termo> "*" <fator> | <termo> '/' <fator> | <fator>
        // pego um fator e vejo se o proximo elemento tem uma '*' ou uma '/' se tiver eu
        // gero dou um loop com recursao
        pilhaTermo.push(fator());

        // pega o primeiro fator
        try {
            getNextToken();
        } catch (EOF e) {
        }
        // pega o token para validar se existe alguma multiplicacao ou divisao
        if (token_atual.getTipo() == 42 || token_atual.getTipo() == 43) {
            saveOperation.push(token_atual);
            pilhaTermo.push(token_atual);
            getNextToken();
            termo();
        }
        if (!pilhaTermo.empty()) {
            resultTermo = definirTipoOperacao(pilhaTermo);
        }
        return resultTermo;
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
                if (simbolToken == null) {
                    System.out.printf(
                            "\tERRO SIMBOLO INEXISTENTE, na linha: %d e na coluna: %d. O ultimo Token lido foi: \'%s\'\n",
                            ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                    System.exit(0);
                }
                try {
                    saveOperation.push(simbolToken.getValor());
                } catch (NullPointerException e) {

                }
                if (simbolToken.getValor() != null) {
                    return simbolToken.getValor();
                } else {
                    System.out.printf(
                            "\tERRO: Simbolo sem atribuicao. na linha: %d e na coluna: %d. O ultimo token lido foi: \'%s\'\n",
                            ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                    System.exit(0);
                    return null;
                }

            }
            // se for um | <id> | <float> | <inteiro> | <char>
            try {
                Token t = token_atual;
                saveOperation.push(token_atual);
            } catch (NullPointerException e) {
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

    public boolean varDisponivel(Token id) {
        // se o simbolo não existir, então ele está disponivel -> true
        // se o simbolo existir, então ele não está disponivel -> false
        return !tabelaDeSimbolos.existeSimbolo(id, escopo);
    }

    private boolean checarTipo(Token primeiroToken, Token segundoToken) {
        if (primeiroToken.getTipo() == 99 && segundoToken.getTipo() == 99) {
            // se ambos os valores forem identificadores, precisamos checar os tipos dos
            // identificadores
            Simbolo primeiroSimbolo = tabelaDeSimbolos.getSimbolo(primeiroToken);
            Simbolo segundoSimbolo = tabelaDeSimbolos.getSimbolo(segundoToken);
            try {
                if (primeiroSimbolo.getTipo() == 6 || primeiroSimbolo.getTipo() == 7) {
                    return segundoSimbolo.getTipo() == 6 || segundoSimbolo.getTipo() == 7;
                } else {
                    return segundoSimbolo.getTipo() == 8;
                }
            } catch (NullPointerException e) {
                System.out.printf(
                        "\tERRO: Simbolo sem atribuicao. na linha: %d e na coluna: %d. O ultimo token lido foi: %s",
                        ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                System.exit(0);
                return false;
            }
        } else if (primeiroToken.getTipo() == 99 && segundoToken.getTipo() != 99) {

            // verifica se o tipo do identificador e o tipo do Token são iguais
            Simbolo primeiroSimbolo = tabelaDeSimbolos.getSimbolo(primeiroToken);
            try {
                if (segundoToken.getTipo() == 90 || segundoToken.getTipo() == 91) {
                    return primeiroSimbolo.getTipo() == 6 || primeiroSimbolo.getTipo() == 7;
                } else {
                    return primeiroSimbolo.getTipo() == 8;
                }
            } catch (NullPointerException e) {
                System.out.printf(
                        "\tERRO: Simbolo sem atribuicao. na linha: %d e na coluna: %d. O ultimo token lido foi: %s",
                        ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                System.exit(0);
                return false;
            }

        } else if (primeiroToken.getTipo() != 99 && segundoToken.getTipo() == 99) {
            // verifica se o tipo do identificador e o tipo do Token são iguais
            Simbolo simbolo = tabelaDeSimbolos.getSimbolo(segundoToken);
            try {
                if (primeiroToken.getTipo() == 90 || primeiroToken.getTipo() == 91) {
                    return simbolo.getTipo() == 6 || simbolo.getTipo() == 7;
                } else {
                    return simbolo.getTipo() == 8;
                }
            } catch (NullPointerException e) {
                System.out.printf(
                        "\tERRO: Simbolo sem atribuicao. na linha: %d e na coluna: %d. O ultimo token lido foi: %s",
                        ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, token_atual.getLexema());
                System.exit(0);
                return false;
            }
        } else {
            // caso nenhum dos dois tokens sejam identificadores

            if (primeiroToken.getTipo() == 90 || primeiroToken.getTipo() == 91) {
                return segundoToken.getTipo() == 90 || segundoToken.getTipo() == 91;
            } else {
                // se n for um int ou float só pode ser char
                return segundoToken.getTipo() == 92;
            }
        }
    }

    public boolean checarTipoAtribuicao(Token tokenIdentificador, Token valor) {
        Simbolo simboloIdentificador = tabelaDeSimbolos.getSimbolo(tokenIdentificador);
        if (simboloIdentificador != null) { // se o simbolo existir
            if (valor.getTipo() == 91) {
                // se for um float ou um char, ele só pode ser atribuido a um símbolo do mesmo
                // tipo

                return simboloIdentificador.getTipo() == 7; // se o identificador for do mesmo tipo do valor

            } else if (valor.getTipo() == 92) {

                return simboloIdentificador.getTipo() == 8;

            } else if (valor.getTipo() == 90) {
                // se for um inteiro, ele pode ser atribuido a um Simbolo inteiro ou um Simbolo
                // float
                return simboloIdentificador.getTipo() == 6 || simboloIdentificador.getTipo() == 7; // se o identificador
                                                                                                   // for um float

            } else if (valor.getTipo() == 99) {
                // caso queira atribuir o valor de uma variavel a outra

                Simbolo simboloValor = tabelaDeSimbolos.getSimbolo(valor);
                return checarTipoAtribuicao(tokenIdentificador, simboloValor.getValor());
            }
        } else {
            System.out.printf("\tERRO: SIMBOLO INEXISTENTE na linha: %d e coluna %d. O ultimo token foi: \'%s\'",
                    ScannerCompilador.getLinha() + 1, Buffer.getColuna() + 1, tokenIdentificador.getLexema());
            System.exit(0);
        }
        return false;
    }

    public void atribuirValor(Token tokenIdentificador, Token valor) {
        Simbolo identf = tabelaDeSimbolos.getSimbolo(tokenIdentificador);
        identf.setValor(valor);
        tabelaDeSimbolos.atualizarValor(identf);
    }

    public Token definirTipoOperacao(Stack<Token> pilha) throws OpChareNaoChar {
        Token topo = null;
        int contador = 0;
        Integer tipo = null;

        while (!pilha.empty()) {
            topo = pilha.pop();

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
                } else if (tipo == 92) {
                    throw new OpChareNaoChar();
                } else if (tipo == 90) {
                    tipo = 91;
                }
            } else if (topo.getTipo() == 92) {
                // se for um char
                if (tipo == null) {
                    tipo = 92;
                } else if (tipo != 92) {
                    throw new OpChareNaoChar();
                }
            } else if (topo.getTipo() == 43) {
                // caso tenha uma divisao, return vai ser do tipo float
                if (tipo == 90) {
                    tipo = 91;
                }
            }
            contador++;
        }

        if (contador == 1) {
            return topo;
        }
        return new Token(tipo);
    }

    public void printar(ArrayList<Token> list) {
        ArrayList<Token> lista = list;
        Iterator itList = lista.iterator();
        int contador = 0;
        Token tkAuxiliar;
        while (itList.hasNext()) {
            try {
                tkAuxiliar = (Token) itList.next();
                if (tkAuxiliar.getTipo() == 42 || tkAuxiliar.getTipo() == 43) {
                    Token primeiroOperando = lista.get(contador - 1);
                    Token sinal = tkAuxiliar;
                    Token segundoOperando = lista.get(contador + 1);
                    System.out.printf("\t_t%d = %s %s %s\n", contadorRegistradorTemporario,
                            primeiroOperando.getLexema(), sinal.getLexema(), segundoOperando.getLexema());
                    lista.remove(contador - 1);
                    lista.remove(contador - 1);
                    lista.remove(contador - 1);
                    try {
                        lista.add(contador - 1, new Token("_t" + contadorRegistradorTemporario));
                    } catch (CharMalFormadoException | FloatMalFormadoException e) {
                        e.printStackTrace();
                    }
                    contadorRegistradorTemporario++;
                    contador++;
                    printar(lista);
                }
                contador++;
            } catch (ConcurrentModificationException e) {

                printar(lista);
                break;
            }
        }
        itList = list.iterator();
        contador = 0;
        while (itList.hasNext()) {
            try {
                tkAuxiliar = (Token) itList.next();
                if (tkAuxiliar.getTipo() == 40 || tkAuxiliar.getTipo() == 41) {
                    Token primeiroOperando = lista.get(contador - 1);
                    Token sinal = tkAuxiliar;
                    Token segundoOperando = lista.get(contador + 1);
                    System.out.printf("\t_t%d = %s %s %s\n", contadorRegistradorTemporario,
                            primeiroOperando.getLexema(), sinal.getLexema(), segundoOperando.getLexema());
                    lista.remove(contador - 1);
                    lista.remove(contador - 1);
                    lista.remove(contador - 1);
                    try {
                        lista.add(contador - 1, new Token("_t" + contadorRegistradorTemporario));
                    } catch (CharMalFormadoException | FloatMalFormadoException e) {
                        e.printStackTrace();
                    }
                    contadorRegistradorTemporario++;
                    contador++;
                    printar(lista);
                }
                contador++;
            } catch (ConcurrentModificationException e) {
                printar(lista);
                break;
            }
        }

    }

    public void prepararString(Stack<Token> pilha) {
        ArrayList<Token> lista = new ArrayList<>();
        while (!pilha.empty()) {
            lista.add(0, pilha.pop());
        }
        if (lista.size() == 1) {

            System.out.printf("\t_t%d = %s\n", contadorRegistradorTemporario, lista.get(0).getLexema());
            contadorRegistradorTemporario++;
        }
        printar(lista);
    }

    public void prepararString(Token token) {
    }

    public void preparar_string_expr_relacional(Stack<Token> pilha) {
        Token primeiroOperando, segundoOperando, operacao;

        segundoOperando = pilha.pop();
        operacao = pilha.pop();
        primeiroOperando = pilha.pop();
        String primeiroOperandoName, segundoOperandoName;

        if (primeiroOperando.isIdentificador(primeiroOperando.getLexema())) {
            primeiroOperandoName = primeiroOperando.getLexema();
        } else {
            primeiroOperandoName = primeiroOperando.getName();
        }

        if (segundoOperando.isIdentificador(segundoOperando.getLexema())) {
            segundoOperandoName = segundoOperando.getLexema();
        } else {
            segundoOperandoName = segundoOperando.getName();
        }
        System.out.printf("\t_t%d = %s %s %s\n", contadorRegistradorTemporario, primeiroOperandoName,
                operacao.getLexema(), segundoOperandoName);
        // System.out.printf("if %s %s
        // %s\n",primeiroOperando.getLexema(),operacao.getLexema(),segundoOperando.getLexema());
        System.out.printf("\tif _t%d == 0 goto label%d\n", contadorRegistradorTemporario, labelsQuantidade);
        contadorRegistradorTemporario++;
        labelsQuantidade++;
    }

}
