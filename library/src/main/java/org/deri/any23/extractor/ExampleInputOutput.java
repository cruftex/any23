/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.deri.any23.extractor;

import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A reporter for example input and output of an extractor. Example
 * input is part of every extractor's metadata; example output is
 * obtained by running the extractor on its own example input. This
 * is useful as a documentation device.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExampleInputOutput {
    private final ExtractorFactory<?> factory;

    public ExampleInputOutput(String extractorName) {
        this(ExtractorRegistry.get().getFactory(extractorName));
    }

    public ExampleInputOutput(ExtractorFactory<?> factory) {
        this.factory = factory;
    }

    public String getExampleInput() throws IOException {
        if (factory.getExampleInput() == null) {
            return null;
        }
        if (isBlindExtractor()) {
            return null;
        }
        InputStream in = factory.createExtractor().getClass().getResourceAsStream(
                factory.getExampleInput());
        if (in == null) {
            throw new IllegalArgumentException(
                    "Example input resource not found for extractor " +
                            factory.getExtractorName() + ": " +
                            factory.getExampleInput());
        }
        return new String(MemCopyFactory.toByteArray(in), "utf-8");
    }

    public String getExampleURI() {
        if (factory.getExampleInput() == null) {
            return null;
        }
        if (isBlindExtractor()) {
            return factory.getExampleInput();    // Should be a URI
        }
        return "http://example.com/";
    }

    public String getExampleOutput() throws IOException, ExtractionException {
        if (factory.getExampleInput() == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TripleHandler writer = new TurtleWriter(out);
        new SingleDocumentExtraction(
                new StringDocumentSource(getExampleInput(), getExampleURI()),
                factory,
                writer).run();
        writer.close();
        return out.toString("utf-8");
    }

    private boolean isBlindExtractor() {
        return factory.createExtractor() instanceof BlindExtractor;
    }

}
