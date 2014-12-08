package fi.nls.oskari.fe.input.jackson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CalendarDeserializer extends JsonDeserializer<Calendar> {

    SimpleDateFormat sdf = new SimpleDateFormat();

    @Override
    public Calendar deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

                
        DatatypeFactory factory;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IOException(e);
        }
        XMLGregorianCalendar xmlCal = factory
                .newXMLGregorianCalendar(jp.getText());

        return xmlCal.toGregorianCalendar();
    }

}
