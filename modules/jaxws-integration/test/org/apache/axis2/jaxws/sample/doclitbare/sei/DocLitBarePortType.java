
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

package org.apache.axis2.jaxws.sample.doclitbare.sei;

import org.test.sample.doclitbare.Composite;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.Holder;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b15-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "DocLitBarePortType", targetNamespace = "http://doclitbare.sample.test.org")
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public interface DocLitBarePortType {


    /**
     * 
     */
    @WebMethod
    @Oneway
    public void oneWayEmpty();

    /**
     * 
     * @param allByMyself
     */
    @WebMethod
    @Oneway
    public void oneWay(
        @WebParam(name = "String", targetNamespace = "http://doclitbare.sample.test.org", partName = "allByMyself")
        String allByMyself);

    /**
     * 
     * @param allByMyself
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "String", targetNamespace = "http://doclitbare.sample.test.org", partName = "allByMyself")
    public String twoWaySimple(
        @WebParam(name = "Integer", targetNamespace = "http://doclitbare.sample.test.org", partName = "allByMyself")
        int allByMyself);

    /**
     * 
     * @param allByMyself
     * @throws FaultBeanWithWrapper
     * @throws SimpleFault
     */
    @WebMethod
    public void twoWayHolder(
        @WebParam(name = "Composite", targetNamespace = "http://doclitbare.sample.test.org", mode = Mode.INOUT, partName = "allByMyself")
        Holder<Composite> allByMyself)
        throws FaultBeanWithWrapper, SimpleFault
    ;

}
