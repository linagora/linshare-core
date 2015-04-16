/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
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

    private final static QName _GetDocuments_QNAME = new QName("http://webservice.linshare.linagora.org/", "getDocuments");
    private final static QName _GetDocumentsResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "getDocumentsResponse");
    private final static QName _BusinessException_QNAME = new QName("http://webservice.linshare.linagora.org/", "BusinessException");
    private final static QName _Sharedocument_QNAME = new QName("http://webservice.linshare.linagora.org/", "sharedocument");
    private final static QName _GetAvailableSize_QNAME = new QName("http://webservice.linshare.linagora.org/", "getAvailableSize");
    private final static QName _GetUserMaxFileSize_QNAME = new QName("http://webservice.linshare.linagora.org/", "getUserMaxFileSize");
    private final static QName _GetUserMaxFileSizeResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "getUserMaxFileSizeResponse");
    private final static QName _AddDocumentXopResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "addDocumentXopResponse");
    private final static QName _GetInformationResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "getInformationResponse");
    private final static QName _Document_QNAME = new QName("http://webservice.linshare.linagora.org/", "Document");
    private final static QName _SharedocumentResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "sharedocumentResponse");
    private final static QName _GetAvailableSizeResponse_QNAME = new QName("http://webservice.linshare.linagora.org/", "getAvailableSizeResponse");
    private final static QName _Return_QNAME = new QName("http://webservice.linshare.linagora.org/", "return");
    private final static QName _GetInformation_QNAME = new QName("http://webservice.linshare.linagora.org/", "getInformation");
    private final static QName _AddDocumentXop_QNAME = new QName("http://webservice.linshare.linagora.org/", "addDocumentXop");

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
     * Create an instance of {@link GetAvailableSizeResponse }
     * 
     */
    public GetAvailableSizeResponse createGetAvailableSizeResponse() {
        return new GetAvailableSizeResponse();
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link GetInformation }
     * 
     */
    public GetInformation createGetInformation() {
        return new GetInformation();
    }

    /**
     * Create an instance of {@link GetUserMaxFileSizeResponse }
     * 
     */
    public GetUserMaxFileSizeResponse createGetUserMaxFileSizeResponse() {
        return new GetUserMaxFileSizeResponse();
    }

    /**
     * Create an instance of {@link AddDocumentXopResponse }
     * 
     */
    public AddDocumentXopResponse createAddDocumentXopResponse() {
        return new AddDocumentXopResponse();
    }

    /**
     * Create an instance of {@link SimpleLongValue }
     * 
     */
    public SimpleLongValue createSimpleLongValue() {
        return new SimpleLongValue();
    }

    /**
     * Create an instance of {@link AddDocumentXop }
     * 
     */
    public AddDocumentXop createAddDocumentXop() {
        return new AddDocumentXop();
    }

    /**
     * Create an instance of {@link DocumentAttachement }
     * 
     */
    public DocumentAttachement createDocumentAttachement() {
        return new DocumentAttachement();
    }

    /**
     * Create an instance of {@link GetDocuments }
     * 
     */
    public GetDocuments createGetDocuments() {
        return new GetDocuments();
    }

    /**
     * Create an instance of {@link Sharedocument }
     * 
     */
    public Sharedocument createSharedocument() {
        return new Sharedocument();
    }

    /**
     * Create an instance of {@link GetInformationResponse }
     * 
     */
    public GetInformationResponse createGetInformationResponse() {
        return new GetInformationResponse();
    }

    /**
     * Create an instance of {@link GetAvailableSize }
     * 
     */
    public GetAvailableSize createGetAvailableSize() {
        return new GetAvailableSize();
    }

    /**
     * Create an instance of {@link SharedocumentResponse }
     * 
     */
    public SharedocumentResponse createSharedocumentResponse() {
        return new SharedocumentResponse();
    }

    /**
     * Create an instance of {@link GetDocumentsResponse }
     * 
     */
    public GetDocumentsResponse createGetDocumentsResponse() {
        return new GetDocumentsResponse();
    }

    /**
     * Create an instance of {@link GetUserMaxFileSize }
     * 
     */
    public GetUserMaxFileSize createGetUserMaxFileSize() {
        return new GetUserMaxFileSize();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocuments }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getDocuments")
    public JAXBElement<GetDocuments> createGetDocuments(GetDocuments value) {
        return new JAXBElement<GetDocuments>(_GetDocuments_QNAME, GetDocuments.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getDocumentsResponse")
    public JAXBElement<GetDocumentsResponse> createGetDocumentsResponse(GetDocumentsResponse value) {
        return new JAXBElement<GetDocumentsResponse>(_GetDocumentsResponse_QNAME, GetDocumentsResponse.class, null, value);
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

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableSize }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getAvailableSize")
    public JAXBElement<GetAvailableSize> createGetAvailableSize(GetAvailableSize value) {
        return new JAXBElement<GetAvailableSize>(_GetAvailableSize_QNAME, GetAvailableSize.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserMaxFileSize }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getUserMaxFileSize")
    public JAXBElement<GetUserMaxFileSize> createGetUserMaxFileSize(GetUserMaxFileSize value) {
        return new JAXBElement<GetUserMaxFileSize>(_GetUserMaxFileSize_QNAME, GetUserMaxFileSize.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserMaxFileSizeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getUserMaxFileSizeResponse")
    public JAXBElement<GetUserMaxFileSizeResponse> createGetUserMaxFileSizeResponse(GetUserMaxFileSizeResponse value) {
        return new JAXBElement<GetUserMaxFileSizeResponse>(_GetUserMaxFileSizeResponse_QNAME, GetUserMaxFileSizeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDocumentXopResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "addDocumentXopResponse")
    public JAXBElement<AddDocumentXopResponse> createAddDocumentXopResponse(AddDocumentXopResponse value) {
        return new JAXBElement<AddDocumentXopResponse>(_AddDocumentXopResponse_QNAME, AddDocumentXopResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInformationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getInformationResponse")
    public JAXBElement<GetInformationResponse> createGetInformationResponse(GetInformationResponse value) {
        return new JAXBElement<GetInformationResponse>(_GetInformationResponse_QNAME, GetInformationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Document }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "Document")
    public JAXBElement<Document> createDocument(Document value) {
        return new JAXBElement<Document>(_Document_QNAME, Document.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableSizeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getAvailableSizeResponse")
    public JAXBElement<GetAvailableSizeResponse> createGetAvailableSizeResponse(GetAvailableSizeResponse value) {
        return new JAXBElement<GetAvailableSizeResponse>(_GetAvailableSizeResponse_QNAME, GetAvailableSizeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLongValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "return")
    public JAXBElement<SimpleLongValue> createReturn(SimpleLongValue value) {
        return new JAXBElement<SimpleLongValue>(_Return_QNAME, SimpleLongValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInformation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "getInformation")
    public JAXBElement<GetInformation> createGetInformation(GetInformation value) {
        return new JAXBElement<GetInformation>(_GetInformation_QNAME, GetInformation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDocumentXop }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.linshare.linagora.org/", name = "addDocumentXop")
    public JAXBElement<AddDocumentXop> createAddDocumentXop(AddDocumentXop value) {
        return new JAXBElement<AddDocumentXop>(_AddDocumentXop_QNAME, AddDocumentXop.class, null, value);
    }

}
