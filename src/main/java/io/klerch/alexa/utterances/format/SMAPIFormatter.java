package io.klerch.alexa.utterances.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.Writer;

import io.klerch.alexa.utterances.model.SMAPIModel;

public class SMAPIFormatter implements Formatter {
    private SMAPIModel model;

    public SMAPIFormatter(final String[] args) {
        if (args != null) {
            int index = ArrayUtils.indexOf(args, "-in");
            if (index < args.length - 1) {
                final String invocationName = args[index + 1];
                Validate.notBlank(invocationName, "Please provide an invocation name.");
                Validate.isTrue(!invocationName.startsWith("-"), "Please provide a valid invocation name.");
                this.model = new SMAPIModel(invocationName);
            }
        }
    }

    public SMAPIFormatter(final String invocatioName) {
        this(new String[] { "-in", invocatioName});
    }

    @Override
    public void before() { }

    @Override
    public boolean write(final String sample) {
        model.getModel().addSample(sample);
        return true;
    }

    @Override
    public String getFormat() {
        return "json";
    }

    private static class PrettyPrinter extends DefaultPrettyPrinter {
        static final PrettyPrinter instance = new PrettyPrinter();

        PrettyPrinter() {
            _arrayIndenter = new DefaultIndenter();
        }
    }

    private static class Factory extends JsonFactory {
        @Override
        protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
            return super._createGenerator(out, ctxt).setPrettyPrinter(PrettyPrinter.instance);
        }
    }

    @Override
    public String generateSchema() {
        try {
            return new ObjectMapper(new Factory())
                    .disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .enable(SerializationFeature.WRAP_ROOT_VALUE)
                    .writeValueAsString(model);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
