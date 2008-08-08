/*
 * Copyright 2006 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis2.jaxws.client;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.client.async.AsyncResponse;
import org.apache.axis2.jaxws.description.EndpointDescription;
import org.apache.axis2.jaxws.message.Block;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.Protocol;
import org.apache.axis2.jaxws.message.databinding.JAXBBlockContext;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.factory.JAXBBlockFactory;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.message.factory.XMLStringBlockFactory;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.axis2.jaxws.spi.ServiceDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JAXBDispatch<T> extends BaseDispatch<T> {
    private static final Log log = LogFactory.getLog(JAXBDispatch.class);
    private JAXBContext jaxbContext;
    
    public JAXBDispatch(ServiceDelegate svcDelegate, EndpointDescription epDesc) {
        super(svcDelegate, epDesc);
    }
    
    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }
    
    public void setJAXBContext(JAXBContext jbc) {
        jaxbContext = jbc;
    }
    
    public AsyncResponse createAsyncResponseListener() {
        JAXBDispatchAsyncListener listener = new JAXBDispatchAsyncListener();
        listener.setJAXBContext(jaxbContext);
        listener.setMode(mode);
        return listener;
    }
    
    public Message createMessageFromValue(Object value) {
        Message message = null;
        try {
            JAXBBlockFactory factory = (JAXBBlockFactory) FactoryRegistry.getFactory(JAXBBlockFactory.class);
            
            Class clazz = value.getClass();
            JAXBBlockContext context = null;
            if (jaxbContext != null) {
                context = new JAXBBlockContext(jaxbContext);
            } else {
                context = new JAXBBlockContext(clazz.getPackage().getName());
            }
            // Create a block from the value
            Block block = factory.createFrom(value, context, null);
            MessageFactory mf = (MessageFactory) FactoryRegistry.getFactory(MessageFactory.class);
            
            if (mode.equals(Mode.PAYLOAD)) {
                // Normal case
                
                // The protocol of the Message that is created should be based
                // on the binding information available.
                Protocol proto = Protocol.getProtocolForBinding(endpointDesc.getClientBindingID());
                message = mf.create(proto);
                message.setBodyBlock(0, block);
            } else {
                // Message mode..rare case
                
                // Create Message from block
                message = mf.createFrom(block, null);
            }
            
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
        
        return message;
    }

    public Object getValueFromMessage(Message message) {
        return getValue(message, mode, jaxbContext);
    }
    
    /**
     * Common code to get the value for JAXBDispatch and JAXBDispatchAsyncListener
     * @param message
     * @param mode
     * @param jaxbContext
     * @return
     */
    static Object getValue(Message message, Mode mode, JAXBContext jaxbContext) {
        Object value = null;
        try {
            if (mode.equals(Mode.PAYLOAD)) {
                // Normal Case
                JAXBBlockFactory factory = (JAXBBlockFactory) FactoryRegistry.getFactory(JAXBBlockFactory.class);
                JAXBBlockContext context = new JAXBBlockContext(jaxbContext);
                Block block = message.getBodyBlock(0, context, factory);
                
                if (block != null) {
                    value = block.getBusinessObject(true);
                } else {
                    // REVIEW This seems like the correct behavior.  If the body is empty, return a null
                    // Any changes here should also be made to XMLDispatch.getValue
                    if (log.isDebugEnabled()) {
                        log.debug("There are no elements in the body to unmarshal.  JAXBDispatch returns a null value");
                    }
                    value = null;
                }
            } else {
                // This is a very strange case, the user would need
                // to have a JAXB object that represents the message (i.e. SOAPEnvelope)
                
                // TODO: This doesn't seem right to me. We should not have an intermediate StringBlock.  
                // This is not performant. Scheu 
                OMElement messageOM = message.getAsOMElement();
                String stringValue = messageOM.toString();  
                QName soapEnvQname = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
                
                XMLStringBlockFactory stringFactory = (XMLStringBlockFactory) FactoryRegistry.getFactory(XMLStringBlockFactory.class);
                Block stringBlock = stringFactory.createFrom(stringValue, null, soapEnvQname);   
                BlockFactory factory = (BlockFactory) FactoryRegistry.getFactory(JAXBBlockFactory.class);
                JAXBBlockContext context = new JAXBBlockContext(jaxbContext);
                Block block = factory.createFrom(stringBlock, context);
                value = block.getBusinessObject(true);   
            }
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
        
        return value;
    }
}