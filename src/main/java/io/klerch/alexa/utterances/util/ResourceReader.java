package io.klerch.alexa.utterances.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ResourceReader {
    public static Optional<List<String>> getPlaceholderValueList(final String valueResource) {
        final List<String> lines = getList(String.format("/slots/%s.values", valueResource));
        return lines.isEmpty() ? Optional.empty() : Optional.of(lines);
    }

    public static List<String> getUtteranceList(final String utteranceResource) {
        return getList(utteranceResource);//String.format("/utterances/%s.grammar", utteranceResource));
    }

    private static List<String> getList(final String fileName) {
        final List<String> lines = new ArrayList<>();

        final File file = new File(fileName);
        if(file.exists()) {
            try (final Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                scanner.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        // eliminate empty lines
        lines.removeIf(StringUtils::isBlank);
        // eliminate commentary
        lines.removeIf(line -> line.startsWith("//"));
        return lines;
    }
}
