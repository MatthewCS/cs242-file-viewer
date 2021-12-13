package com.fileviewer.tokenizer;

import java.util.regex.Pattern;
import javafx.scene.paint.Color;

/**
 * A token with an associated RegEx pattern and a JavaFX color
 */
public final class ColoredToken {
    private final Pattern pattern;
    private final int id;
    private final Color color;

    /**
     * Construct a ColoredToken
     *
     * @param p A RegEx pattern we want to match
     * @param id The ID of this token
     * @param c The color of this token
     */
    public ColoredToken(String p, int id, Color c)
    {
        this.pattern = Pattern.compile(p, id);
        this.id = id;
        this.color = c;
    }

    /**
     * Getter function for a token's RegEx pattern
     * @return This token's RegEx pattern
     */
    public Pattern getPattern() { return this.pattern; }

    /**
     * Getter function for a token's ID
     * @return This token's ID
     */
    public int getID()          { return this.id; }

    /**
     * Getter function for a token's color
     * @return This token's color
     */
    public Color getColor()     { return this.color; }
}
