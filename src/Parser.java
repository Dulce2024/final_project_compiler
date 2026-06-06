import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos;
    private ValidationResult result;

    public SelectStatement parse(List<Token> tokens, ValidationResult result) {
        this.tokens = tokens;
        this.pos = 0;
        this.result = result;
        SelectStatement statement = new SelectStatement();
        expect(TokenType.SELECT, "SYNTACTIC_EXPECTED_SELECT");
        parseColumns(statement);
        expect(TokenType.FROM, "SYNTACTIC_EXPECTED_FROM");
        Token table = expect(TokenType.IDENTIFIER, "SYNTACTIC_EXPECTED_TABLE");
        if (table != null) statement.table = table.lexeme;

        if (match(TokenType.WHERE)) {
            statement.where = new ConditionChain();
            parseWhereCondition(statement);
            while (check(TokenType.AND) || check(TokenType.OR)) {
                if (match(TokenType.AND)) {
                    statement.where.connectors.add("AND");
                } else {
                    match(TokenType.OR);
                    statement.where.connectors.add("OR");
                }
                parseWhereCondition(statement);
            }
        }

        if (check(TokenType.SEMICOLON)) advance();
        if (!check(TokenType.EOF)) {
            result.diagnostics.add(new Diagnostic("SYNTACTIC_UNEXPECTED_TOKEN", "Token inesperado: " + current().lexeme, current().span));
        }
        return statement;
    }

    private void parseColumns(SelectStatement statement) {
        if (match(TokenType.STAR)) {
            statement.columns.add("*");
            return;
        }
        Token first = expect(TokenType.IDENTIFIER, "SYNTACTIC_EXPECTED_COLUMN");
        if (first != null) statement.columns.add(first.lexeme);
        while (match(TokenType.COMMA)) {
            Token next = expect(TokenType.IDENTIFIER, "SYNTACTIC_EXPECTED_COLUMN");
            if (next != null) statement.columns.add(next.lexeme);
        }
    }

    private Token expect(TokenType type, String code) {
        if (check(type)) return advance();
        result.diagnostics.add(new Diagnostic(code, "Se esperaba " + type + " y se encontró " + current().type, current().span));
        return null;
    }

    private void parseWhereCondition(SelectStatement statement) {
        Token column = expect(TokenType.IDENTIFIER, "SYNTACTIC_EXPECTED_WHERE_OPERAND");
        if (column == null) {
            while (!check(TokenType.EOF) && !check(TokenType.SEMICOLON) && !check(TokenType.AND) && !check(TokenType.OR)) advance();
            return;
        }
        Token operator = parseComparisonOperator();
        if (operator == null) {
            result.diagnostics.add(new Diagnostic("SYNTACTIC_EXPECTED_WHERE_OPERAND", "Se esperaba operador de comparacion", current().span));
            return;
        }
        Token literal;
        LiteralType literalType;
        if (match(TokenType.NUMBER)) {
            literal = previous();
            literalType = LiteralType.NUMBER;
        } else if (match(TokenType.STRING)) {
            literal = previous();
            literalType = LiteralType.STRING;
        } else if (match(TokenType.TRUE) || match(TokenType.FALSE)) {
            literal = previous();
            literalType = LiteralType.BOOLEAN;
        } else {
            result.diagnostics.add(new Diagnostic("SYNTACTIC_EXPECTED_WHERE_OPERAND", "Se esperaba un literal", current().span));
            return;
        }
        statement.where.conditions.add(new WhereCondition(
            column.lexeme, operator.lexeme, literal.lexeme, literalType,
            column.span, operator.span, literal.span));
    }

    private Token parseComparisonOperator() {
        if (match(TokenType.EQUAL)) return previous();
        if (match(TokenType.GREATER)) return previous();
        if (match(TokenType.LESS)) return previous();
        if (match(TokenType.GREATER_EQUAL)) return previous();
        if (match(TokenType.LESS_EQUAL)) return previous();
        if (match(TokenType.NOT_EQUAL)) return previous();
        return null;
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private boolean match(TokenType type) { if (check(type)) { advance(); return true; } return false; }
    private boolean check(TokenType type) { return current().type == type; }
    private Token current() { return tokens.get(pos); }
    private Token advance() { return tokens.get(pos++); }
}
