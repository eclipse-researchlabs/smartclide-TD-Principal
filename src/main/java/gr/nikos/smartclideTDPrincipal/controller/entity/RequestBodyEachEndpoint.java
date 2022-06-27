/*******************************************************************************
 * Copyright (C) 2021-2022 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package gr.nikos.smartclideTDPrincipal.controller.entity;

public class RequestBodyEachEndpoint {
    private String fileName;
    private String endpointMethod;

    public RequestBodyEachEndpoint(String fileDir, String endpointMethod) {
        this.fileName = fileDir;
        this.endpointMethod = endpointMethod;
    }

    public String getFileName() {
        return fileName;
    }

    public String getEndpointMethod() {
        return endpointMethod;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setEndpointMethod(String endpointMethod) {
        this.endpointMethod = endpointMethod;
    }
}
