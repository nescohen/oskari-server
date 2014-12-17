package fi.nls.oskari.eu.elf.addresses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Ignore;
import org.junit.Test;

import fi.nls.oskari.eu.elf.recipe.addresses.ELF_MasterLoD0_Address_Parser;
import fi.nls.oskari.fe.engine.BasicFeatureEngine;
import fi.nls.oskari.fe.input.XMLInputProcessor;
import fi.nls.oskari.fe.input.format.gml.StaxGMLInputProcessor;
import fi.nls.oskari.fe.input.format.gml.recipe.JacksonParserRecipe;
import fi.nls.oskari.fe.output.OutputStreamProcessor;
import fi.nls.oskari.fe.output.format.json.JsonOutputProcessor;

public class TestJacksonParser {

    /**
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws XMLStreamException
     */
    @Ignore("Not ready")
    @Test
    public void test_ELF_Master_LoD0_Address_ign_fr_wfs_GMLtoJSON()
            throws InstantiationException, IllegalAccessException, IOException,
            XMLStreamException {

        BasicFeatureEngine engine = new BasicFeatureEngine();

        XMLInputProcessor inputProcessor = new StaxGMLInputProcessor();

        OutputStreamProcessor outputProcessor = new JsonOutputProcessor();

        InputStream inp = getClass().getResourceAsStream(
                "/fi/nls/oskari/eu/elf/addresses/ign_fr-ELF-AD-wfs.xml");

        try {
            inputProcessor.setInput(inp);

            OutputStream fouts = System.out;
            try {
                outputProcessor.setOutput(fouts);

                JacksonParserRecipe recipe = new ELF_MasterLoD0_Address_Parser();
                recipe.setLenient(true);
                //recipe.getGeometryDeserializer().setIgnoreProps(true);

                engine.setRecipe(recipe);

                engine.setInputProcessor(inputProcessor);
                engine.setOutputProcessor(outputProcessor);

                engine.process();

            } finally {
                // fouts.close();
            }

        } finally {
            inp.close();
        }

    }
}
