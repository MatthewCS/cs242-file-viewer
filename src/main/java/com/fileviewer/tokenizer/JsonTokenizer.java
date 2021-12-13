package com.fileviewer.tokenizer;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Class to tokenize a JSON file
 */
public final class JsonTokenizer {
    /**
     * The position of a given colorized token
     */
    public static final class TokenPos
    {
        /**
         * Start position of this token
         */
        public int start;
        /**
         * Length of this token
         */
        public int length;
        /**
         * ColoredToken found at the given position and spanning the given length
         */
        public ColoredToken token;

        /**
         * Construct a token and its position
         *
         * @param s start index
         * @param len length of the token
         * @param ct the ColoredToken itself
         */
        public TokenPos(int s, int len, ColoredToken ct)
        {
            this.start = s;
            this.length = len;
            this.token = ct;
        }
    }

    /**
     * A list of JSON tokens with attached colors
     */
    // https://www.json.org/json-en.html
    private static final ColoredToken[] jsonTokens = {
            // Open curly brace
            new ColoredToken("^\\{", 1, Color.LIGHTGREY),
            // Close curly brace
            new ColoredToken("^\\}", 2, Color.LIGHTGREY),
            // String value
            new ColoredToken("^\"[^'\\n''\\r''\\r\\n'\"]+\"", 3, Color.LIGHTCORAL),
            // Floating point value
            new ColoredToken("^(\\-)?\\d+.{1}\\d+", 4, Color.LIGHTSALMON),
            // Integer value
            new ColoredToken("^(\\-)?\\d+", 5, Color.LIGHTSALMON),
            // Boolean value
            new ColoredToken("^true|^false", 6, Color.LIGHTSTEELBLUE),
            // Open bracket
            new ColoredToken("^\\[", 7, Color.LIGHTGREY),
            // Closed bracket
            new ColoredToken("^\\]", 8, Color.LIGHTGREY),
            // Comma
            new ColoredToken("^\\,",9, Color.LIGHTGREY),
            // Colon
            new ColoredToken("^\\:", 10, Color.LIGHTGREY)
    };
    /**
     * A token representing a math we aren't sure how to categorize
     */
    private static final ColoredToken unknownMatch = new ColoredToken("", 11, Color.TRANSPARENT);

    /**
     * Test function for JsonTokenizer
     * @param args Command-line arguments (ignored)
     */
    public static void main(String [] args)
    {
        String jsonInput = "{\n";
        jsonInput += "  \"Integer\": 1,\n";
        jsonInput += "  \"Decimal\": 3.14,\n";
        jsonInput += "  \"String\": \"delectus aut autem\",\n";
        jsonInput += "  \"Bool1\": true,\n";
        jsonInput += "  \"Bool2\": false,\n";
        jsonInput += "  \"List\": [\n";
        jsonInput += "    100,\n";
        jsonInput += "    200,\n";
        jsonInput += "    300\n";
        jsonInput += "  ],\n";
        jsonInput += "  \"Object\": {\n";
        jsonInput += "    \"ElemA\": \"Hello, \",\n";
        jsonInput += "    \"ElemB\": \"World!\"\n";
        jsonInput += "  },\n";
        jsonInput += "  \"All done!\": true\n";
        jsonInput += "}";

        for(TokenPos tp: tokenize(jsonInput))
        {
            System.out.print(tp.start);
            System.out.print("  ");
            System.out.print(tp.length);
            System.out.print("  ");
            System.out.print(tp.token.getPattern().toString());
            System.out.print("\n");
            System.out.println("    " + jsonInput.substring(tp.start, tp.start+tp.length));
        }
    }

    /**
     * Static function to return a list of tokens and their positions
     *
     * @param input The JSON string we want to parse
     * @return A list of colored JSON tokens attached to their positions and lengths
     */
    public static ArrayList<TokenPos> tokenize(String input)
    {
        ArrayList<TokenPos> positions = new ArrayList<TokenPos>();
        int index = 0;

        while(! input.equals(""))
        {
            boolean matchFound = false;
            for(ColoredToken ct : jsonTokens)
            {
                Matcher m = ct.getPattern().matcher(input);

                if(m.find())
                {
                    String contents = m.group().trim();
                    int length = contents.length();

                    positions.add(new TokenPos(index, length, ct));

                    index += length;
                    input = m.replaceFirst("");
                    matchFound = true;
                    break;
                }
            }

            // No match found --- skip this input
            if(! matchFound)
            {
                String unknownStr = input.substring(0, 1);
                positions.add(new TokenPos(index, 1, unknownMatch));
                input = input.substring(1);
                index += 1;
            }
        }

        return positions;
    }
}
