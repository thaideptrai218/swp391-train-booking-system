package vn.vnrailway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // For LocalDateTime support
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonUtils {

    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Configure ObjectMapper as needed
        // Register JavaTimeModule for proper serialization/deserialization of
        // LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // Pretty print for debugging (optional, can be performance hit in production)
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Don't fail on unknown properties during deserialization
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Configure date formats if not using ISO standard and JavaTimeModule isn't
        // enough
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        // mapper.setDateFormat(sdf);
        return mapper;
    }

    /**
     * Parses JSON from a Reader into an object of the specified class.
     * 
     * @param reader   The reader providing the JSON input.
     * @param classOfT The class of the object to parse into.
     * @return An object of type T.
     * @throws IOException             if an input/output exception occurs
     * @throws JsonProcessingException if JSON is malformed or reader is null.
     */
    public static <T> T parse(Reader reader, Class<T> classOfT) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null for JSON parsing.");
        }
        return objectMapper.readValue(reader, classOfT);
    }

    /**
     * Parses JSON from a Reader into an object of the specified generic type (e.g.,
     * List<MyClass>).
     * 
     * @param reader        The reader providing the JSON input.
     * @param typeReference The TypeReference representing the generic type. (e.g.,
     *                      new TypeReference<List<MyClass>>() {})
     * @return An object of type T.
     * @throws IOException             if an input/output exception occurs
     * @throws JsonProcessingException if JSON is malformed or reader is null.
     */
    public static <T> T parse(Reader reader, TypeReference<T> typeReference) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null for JSON parsing.");
        }
        return objectMapper.readValue(reader, typeReference);
    }

    /**
     * Serializes an object into its JSON representation and writes it to a Writer.
     * 
     * @param object The object to serialize.
     * @param writer The writer to output the JSON to.
     * @throws IOException if an input/output exception occurs
     */
    public static void toJson(Object object, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null for toJson operation.");
        }
        objectMapper.writeValue(writer, object);
    }

    /**
     * Serializes an object into its JSON representation as a String.
     * 
     * @param object The object to serialize.
     * @return JSON string representation of the object.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
