package com.example.four_ways_learn.ui;

import java.text.Normalizer;
import java.util.Locale;

/** Splits the existing single database field; no schema migration is needed. */
public final class LessonSections {
    public final String content, example, practice, explanation;
    private final String exampleHeading, practiceHeading, explanationHeading;

    private LessonSections(String[] parts, String[] headings) {
        content = parts[0].trim(); example = parts[1].trim();
        practice = parts[2].trim(); explanation = parts[3].trim();
        exampleHeading = headings[1]; practiceHeading = headings[2]; explanationHeading = headings[3];
    }
    public static LessonSections parse(String source) {
        String[] headings = {"", "EJEMPLO", "PRÁCTICA", "EXPLICACIÓN"};
        StringBuilder[] parts = {new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder()};
        int current = 0;
        for (String line : (source == null ? "" : source).replace("\r\n", "\n").split("\n", -1)) {
            String title = line.trim();
            String key = Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
            int section = -1;
            // Only stand-alone uppercase headings are separators, never a sentence or code fragment.
            if (title.equals(title.toUpperCase(Locale.ROOT))) {
                if (key.matches("EJEMPLOS?(?:\\s+(?:EN\\s+.+|SQL|KOTLIN|PYTHON|HTML|CSS|JAVA)|\\s*:\\s*.+)?") || key.equals("EN TU PROYECTO")) section = 1;
                else if (key.matches("PRACTICA:?") || key.matches("PRACTICA\\s+.+")) section = 2;
                else if (key.matches("EXPLICACION:?")) section = 3;
                else if (key.equals("CONTENIDO")) section = 0;
            }
            // Do not drop a repeated heading: retain its text in the current section.
            if (section >= 0 && section != current && parts[section].length() == 0) {
                current = section; headings[section] = title;
            } else parts[current].append(line).append('\n');
        }
        return new LessonSections(new String[]{parts[0].toString(), parts[1].toString(), parts[2].toString(), parts[3].toString()}, headings);
    }
    public String compose(String content, String example, String practice, String explanation) {
        String[] values = {content, example, practice, explanation};
        String[] headings = {"", exampleHeading, practiceHeading, explanationHeading};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i] == null ? "" : values[i].trim();
            if (value.isEmpty()) continue;
            if (result.length() > 0) result.append("\n\n");
            if (i > 0) result.append(headings[i]).append('\n');
            result.append(value);
        }
        return result.toString();
    }
}
