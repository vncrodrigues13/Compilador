package Parser;

import Token.Token;

import java.io.IOException;

import Exception.*;
import Scanner.ScannerCompilador;

public class Parser {
    private ScannerCompilador scan;
    private static Token token_atual;

    public Parser(ScannerCompilador scan) {
        this.scan = scan;
        token_atual = null;
    }

    public void declarar_variavel() throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            EOFemComentarioMultilinha, CaractereInvalidoException, CharMalFormadoException {
        Token tokenTipo, tokenId, tokenEspecial;
        getNextToken();

        switch (token_atual.getValor()) {
            case 6:
            case 7:
            case 8:
        }

        getNextToken();
        if (token_atual.getValor() == 99) {
            // apos o token de tipo de var, o proximo token precisa ser um identificador
            System.exit(1);
        } else {
            // caso nao seja um identificador
            System.exit(0);
        }

        getNextToken();
        if (token_atual.getValor() == 25) {
            // ','
        } else if (token_atual.getValor() == 24) {
            // ';'
        }
    }

    public void bloco() throws IOException,, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        Token tokenAbreChave, tokenFechaChave, declararVariavel, comando;
        getNextToken();

        if (token_atual.getValor() == 22){
            // <declararVariavel>
            // <comando>
            getNextToken();
            if (token_atual.getValor() == 23){
                // caso feche o bloco
            }
        }
    }

    public void comando() throws IOException, EOFemComentarioMultilinha, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        getNextToken();
        if (token_atual.getValor() == 1) {
            // if '(' <exp_relacional> ')' '{' <comando> '}' else '{' <comando> '}'
        } else if (token_atual.getValor() == 3 || token_atual.getValor() == 4) {
            // para ser uma iteracao, o first tem que ser um "while" ou um "do"
            // iteracao
        } else if (token_atual.getValor() == 99 || token_atual.getValor() == 22) {
            // para ser um comando basico, o first tem que ser um <id> ou uma '{'
            // <comandoBasico> ==>> <atribuicao> || <bloco>
            comando_basico();
        } else {
            // erro
        }
    }

    public void comando_basico() {
        // atribuicao ou bloco
        getNextToken();
        if (token_atual.getValor() == 99) {
            // se for um identificador, ele vai formar uma atribuicao;
            getNextToken();
            if (token_atual.getValor() == 16) {
                expr_arit();
                getNextToken();
                if (token_atual.getValor() == 24) {
                    // fizalou o comando basico (atribuicao).
                }
            }
        } else if (token_atual.getValor() == 22) {
            // se houver uma chave, vai ser um bloco
            declarar_variavel(); // {<decl_var>}*
            comando(); // {<comando>}*
            getNextToken();
            if (token_atual.getValor() == 23) {
                // acabou o comando_basico (bloco)

            }

        } else {
            // nao seria um comando básico nem atribuicao

        }
    }

    public void iteracao() {
        // while "("<expr_relacional>")" <comando> | do <comando> while
        // "("<expr_relacional>")"";"
    }

    public void atribuicao() {
        // <id> "=" <expr_arit> ";"
        if (token_atual.getValor() == 99) {
            getNextToken();
            if (token_atual.getValor() == 16) {
                getNextToken();
                expr_arit();
                getNextToken();
                if (token_atual.getValor() == 24) {
                    // finalizar atribuicao
                }
            }
        }
    }

    public void expr_relacional() {
        // <expr_arit> <op_relacional> <expr_arit>
        int contador_operador_relacional;
        expr_arit();
        getNextToken();
        switch (token_atual.getValor()){
            case 10:
                // igual
                getNextToken();
                expr_arit();
            case 11:
                // diferente
                getNextToken();
                expr_arit();
            case 12:
                // maior que
                getNextToken();
                expr_arit();
            case 13:
                // maior igual a
                getNextToken();
                expr_arit();
            case 14:
                // menor que
                getNextToken();
                expr_arit();
            case 15:
                // menor igual a
                getNextToken();
                expr_arit();
        }
    }

    public void expr_arit() {
        // <expr_arit> "+" <termo> | <expr_arit> "-" <termo> | <termo>
        fator();
        getNextToken();
        if (token_atual.getValor() == 40 || token_atual.getValor() == 41){
            // se houver soma ou subtracao
            if (token_atual.getValor() == 40){
                // caso seja uma soma
            }else{
                // caso seja uma subtracao
            }
            getNextToken();
            expr_arit();
        }
    }

    public void termo() {
        // <termo> "*" <fator> | <termo> “/” <fator> | <fator>
        // pego um fator e vejo se o proximo elemento tem uma '*' ou uma '/' se tiver eu gero dou um loop com recursao
        fator();
        getNextToken();
        if (token_atual.getValor() == 42 || token_atual.getValor() == 43){
            getNextToken();
            termo();
        }
    }

    public void fator() {
        // “(“ <expr_arit> “)” | <id> | <float> | <inteiro> | <char>
        if (token_atual.getValor() == 20) {
            getNextToken();
            expr_arit();
            getNextToken();
            if (token_atual.getValor() == 21) {
                // fecha parenteses
            }
        } else {
            switch (token_atual.getValor()) {
                case 6:
                    // caso seja int
                case 7:
                    // caso seja float
                case 8:
                    // caso seja char
                case 99:
                    // caso seja <id>
                default:
                    // caso nao seja <id> | <float> | <inteiro> | <char>
            }
        }
    }

    private void getNextToken() {
        token_atual = scan.getNextToken();
    }

}
