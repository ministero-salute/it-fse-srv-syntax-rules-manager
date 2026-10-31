package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

final class Fixtures {
    static final String ID = "507f1f77bcf86cd799439011";
    static final String XSD = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"sample\" type=\"xs:string\"/></xs:schema>";
    static MockMultipartFile file(String name, String content) {
        return new MockMultipartFile("files", name, "application/xml", content.getBytes(StandardCharsets.UTF_8));
    }
    static MultipartFile[] files() { return new MultipartFile[]{file("root.xsd", XSD)}; }
    static SchemaETY entity() throws Exception {
        SchemaETY e = SchemaETY.fromMultipart(files()[0], "ext", true);
        e.setId(ID);
        return e;
    }
}
