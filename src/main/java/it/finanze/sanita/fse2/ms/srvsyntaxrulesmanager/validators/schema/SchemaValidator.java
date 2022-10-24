package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.schema;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.SchemaValidatorException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.*;

public class SchemaValidator implements LSResourceResolver {

    private static final String LS_FEATURE = "LS";

    private final String root;
    private Map<String, byte[]> mapping;

    public SchemaValidator(String root) {
        this.root = root;
        this.mapping = new HashMap<>();
    }

    public void verify(Map<String, byte[]> current, MultipartFile[] newest) throws SchemaValidatorException {
        // Map key-value
        for (MultipartFile file: newest) {
            // Prevent duplicated key exception
            try {
                current.replace(FilenameUtils.getName(file.getOriginalFilename()), file.getBytes());
            } catch (IOException e) {
                throw new SchemaValidatorException(ERR_VAL_INVALID_SCHEMA_IO, e);
            }
        }
        // Assign
        this.mapping = current;
        // Now validate
        validate();
    }

    public void verify(MultipartFile[] files) throws SchemaValidatorException {
        // Map key-value
        for (MultipartFile file: files) {
            // Prevent duplicated key exception
            try {
                mapping.putIfAbsent(FilenameUtils.getName(file.getOriginalFilename()), file.getBytes());
            } catch (IOException e) {
                throw new SchemaValidatorException(ERR_VAL_INVALID_SCHEMA_IO, e);
            }
        }
        // Now validate
        validate();
    }

    private void validate() throws SchemaValidatorException {
        // Create XML schema validator
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Create handler
        factory.setResourceResolver(this);
        // For some reason it emits just a warning for a missing include file
        factory.setErrorHandler(new SchemaErrorHandler());
        // Compile & resolve
        try {
            factory.newSchema(getRoot());
        } catch (IOException | SAXException e) {
            throw new SchemaValidatorException(
                String.format(
                    ERR_VAL_INVALID_SCHEMA,
                    e.getMessage()
                ), e);
        }
    }

    private StreamSource getRoot() throws IOException {
        return new StreamSource(new ByteArrayInputStream(mapping.get(root)));
    }

    private LSInput createResource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation(LS_FEATURE);
        return impl.createLSInput();
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        // It is an ACCEPTED value, default implementation returns 'null'
        LSInput resource = null;
        // Retrieve systemId identifier (base-name)
        String identifier = FilenameUtils.getName(FilenameUtils.getName(systemId));
        // Uses it as a key
        if(mapping.containsKey(identifier)) {
            try {
                // Invoke XML registry to find the default implementation to generate an LSInput node
                resource = createResource();
                // Open data stream
                resource.setByteStream(new ByteArrayInputStream(mapping.get(identifier)));
                // SystemId must be the same on every reference (unique-key)
                resource.setPublicId(publicId);
                resource.setBaseURI(baseURI);
                resource.setSystemId(identifier);
            } catch (
                ClassNotFoundException |
                InstantiationException |
                IllegalAccessException e
            ) {
                // Maybe log
            }
        }
        return resource;
    }
}
