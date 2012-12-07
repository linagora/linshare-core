
package org.linagora.linshare.webservice.jaxrs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.linagora.linshare.webservice.test.soap.DocumentAttachement;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.linagora.linshare.webservice.jaxrs package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DocumentAttachement_QNAME = new QName("http://org/linagora/linshare/webservice/jaxrs", "documentAttachement");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.linagora.linshare.webservice.jaxrs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentAttachement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org/linagora/linshare/webservice/jaxrs", name = "documentAttachement")
    public JAXBElement<DocumentAttachement> createDocumentAttachement(DocumentAttachement value) {
        return new JAXBElement<DocumentAttachement>(_DocumentAttachement_QNAME, DocumentAttachement.class, null, value);
    }

}
