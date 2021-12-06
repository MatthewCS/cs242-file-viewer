package com.fileviewer.tokenizer;

import java.util.regex.Pattern;
import javafx.scene.paint.Color;

public final class ColoredToken {
    private final Pattern pattern;
    private final int id;
    private final Color color;

    public ColoredToken(String p, int id, Color c)
    {
        this.pattern = Pattern.compile(p, id);
        this.id = id;
        this.color = c;
    }

    public Pattern getPattern() { return this.pattern; }
    public int getID()          { return this.id; }
    public Color getColor()     { return this.color; }
}
