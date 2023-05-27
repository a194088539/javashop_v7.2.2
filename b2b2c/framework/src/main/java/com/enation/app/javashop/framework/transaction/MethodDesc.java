/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.enation.app.javashop.framework.transaction;

import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * The type Method desc.
 *
 * @author slievrly
 */
public class MethodDesc {
    private Transactional transactionAnnotation;
    private Method method;

    /**
     * Instantiates a new Method desc.
     *
     * @param transactionAnnotation the transaction annotation
     * @param method                the method
     */
    public MethodDesc(Transactional transactionAnnotation, Method method) {
        this.transactionAnnotation = transactionAnnotation;
        this.method = method;
    }

    /**
     * Gets transaction annotation.
     *
     * @return the transaction annotation
     */
    public Transactional getTransactionAnnotation() {
        return transactionAnnotation;
    }

    /**
     * Sets transaction annotation.
     *
     * @param transactionAnnotation the transaction annotation
     */
    public void setTransactionAnnotation(Transactional transactionAnnotation) {
        this.transactionAnnotation = transactionAnnotation;
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets method.
     *
     * @param method the method
     */
    public void setMethod(Method method) {
        this.method = method;
    }
}
