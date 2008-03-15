/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis2.jaxws.client.config;

import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.binding.SOAPBinding;
import org.apache.axis2.jaxws.core.MessageContext;
import org.apache.axis2.jaxws.feature.ClientConfigurator;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.spi.Binding;
import org.apache.axis2.jaxws.spi.BindingProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.ws.soap.MTOMFeature;
import java.io.InputStream;
import java.util.List;

/**
 *
 */
public class MTOMConfigurator implements ClientConfigurator {

    private static Log log = LogFactory.getLog(MTOMConfigurator.class);
    
    /*
     * (non-Javadoc)
     * @see org.apache.axis2.jaxws.feature.util.WebServiceFeatureConfigurator#performConfiguration(org.apache.axis2.jaxws.core.MessageContext, org.apache.axis2.jaxws.spi.BindingProvider)
     */
    public void configure(MessageContext messageContext, BindingProvider provider) {
        Binding bnd = (Binding) provider.getBinding();
        MTOMFeature mtomFeature = (MTOMFeature) bnd.getFeature(MTOMFeature.ID);
        Message requestMsg = messageContext.getMessage();
        
        //Disable MTOM.
        requestMsg.setMTOMEnabled(false);
                
        //TODO NLS enable.
        if (mtomFeature == null)
            throw ExceptionFactory.makeWebServiceException("The MTOM features was unspecified.");

        //Enable MTOM if specified.
        if (mtomFeature.isEnabled()) {
            int threshold = mtomFeature.getThreshold();
            List<String> attachmentIDs = requestMsg.getAttachmentIDs();
            
            // If a threshold wasn't configured, enable MTOM for all cases.
            if (threshold <= 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Enabling MTOM with no threshold.");
                }
                requestMsg.setMTOMEnabled(true);
            }
            else if (attachmentIDs != null) {
            	long size = 0L;
            	
            	for (String attachmentID : attachmentIDs) {
            	    DataHandler dh = requestMsg.getDataHandler(attachmentID);
        			
            	    if (dh != null) {
            	        DataSource ds = dh.getDataSource();
            	        InputStream is = null;
                    	
            	        try {
            	            is = ds.getInputStream();
            	            size += is.available();
            	        }
            	        catch (Exception e) {
            	            // TODO NLS enable.
            	            throw ExceptionFactory.makeWebServiceException("Unable to determine the size of the attachment(s).", e);
                    	}
                    	finally {
                    	    try {
                    	        if (is != null)
                    	            is.close();
                    	    }
                    	    catch (Exception e) {
                    	        //Nothing to do.
                    	    }
                    	}
            	    }
            	}
            	
            	if (size > threshold)
            	    requestMsg.setMTOMEnabled(true);
            }
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("The MTOMFeature was found, but not enabled.");
            }
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.axis2.jaxws.feature.ClientConfigurator#supports(org.apache.axis2.jaxws.spi.Binding)
     */
    public boolean supports(Binding binding) {
        return binding instanceof SOAPBinding;
    }
}
