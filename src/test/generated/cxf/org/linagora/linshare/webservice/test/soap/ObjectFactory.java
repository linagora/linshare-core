
package org.linagora.linshare.webservice.test.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.linagora.linshare.webservice.test.soap package. 
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

    private final static QName _SharedocumentResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "sharedocumentResponse");
    private final static QName _BusinessException_QNAME = new QName("http://webservice.linshare.linagora.org/", "BusinessException");
    private final static QName _Sharedocument_QNAME = new QName("http://webservice.linshare.linagora.org/", "sharedocument");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.linagora.linshare.webservice.test.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BusinessException }
     * 
     */
    public BusinessException createBusinessException() {
        return new BusinessException();
    }

    /**
     * Create an instance of {@link Sharedocument }
     * 
     */
    public Sharedocument createSharedocument() {
        return new Sharedocument();
    }

    /**
     * Create an instance of {@link SharedocumentResponse }
     * 
     */
    public SharedocumentResponse createSharedocumentResponse() {
        return new SharedocumentResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SharedocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "sharedocumentResponse")
    public JAXBElement<SharedocumentResponse> createSharedocumentResponse(SharedocumentResponse value) {
        return new JAXBElement<SharedocumentResponse>(_SharedocumentResponse_QNAME, SharedocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BusinessException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "BusinessException")
    public JAXBElement<BusinessException> createBusinessException(BusinessException value) {
        return new JAXBElement<BusinessException>(_BusinessException_QNAME, BusinessException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Sharedocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "sharedocument")
    public JAXBElement<Sharedocument> createSharedocument(Sharedocument value) {
        return new JAXBElement<Sharedocument>(_Sharedocument_QNAME, Sharedocument.class, null, value);
    }

}
