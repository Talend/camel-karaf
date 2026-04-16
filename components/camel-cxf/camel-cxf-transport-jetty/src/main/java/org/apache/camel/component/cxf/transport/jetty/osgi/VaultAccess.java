/*
 * Copyright 2026 Talend.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.cxf.transport.jetty.osgi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class VaultAccess {

    private static final Object SEM = new Object();
    private static final String VAULT_PREFIX = "VAULT(";
    private static final String VAULT_SUFFIX = ")";
    private static final int VAULT_PREFIX_LEN = VAULT_PREFIX.length();
    private static final int VAULT_SUFFIX_LEN = VAULT_SUFFIX.length();

    private static Object vaultObject;
    private static Method vaultAccessMethod;

    private VaultAccess() {
    }

    public static String resolveValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (!(value.startsWith(VAULT_PREFIX) && value.endsWith(VAULT_SUFFIX))) {
            return value;
        }
        String resKey = value.substring(VAULT_PREFIX_LEN, value.length() - VAULT_SUFFIX_LEN);
        String result = getValue(resKey);
        return result == null ? value : result;
    }

    private static String getValue(String resKey) {
        synchronized (SEM) {
            try {
                initVault();
                return (String) vaultAccessMethod.invoke(vaultObject, resKey);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static void initVault() throws Exception {
        if (vaultObject == null) {
            Class<?> cls = VaultAccess.class.getClassLoader().loadClass("org.talend.cxf.crypto.config.SimpleVault");
            Constructor<?> c = cls.getConstructor(String.class);
            Method m = cls.getMethod("getValue", String.class);
            char[] vaultKeyChars = new char[12];
            // FIXME configurable vault key
            vaultKeyChars[0] = 'h';
            vaultKeyChars[1] = 'e';
            vaultKeyChars[2] = 't';
            vaultKeyChars[3] = 'B';
            vaultKeyChars[4] = 'w';
            vaultKeyChars[5] = '1';
            vaultKeyChars[6] = '1';
            vaultKeyChars[7] = 'B';
            vaultKeyChars[8] = '1';
            vaultKeyChars[9] = 'N';
            vaultKeyChars[10] = 'O';
            vaultKeyChars[11] = '6';
            ++vaultKeyChars[0];
            --vaultKeyChars[1];
            ++vaultKeyChars[2];
            --vaultKeyChars[3];
            ++vaultKeyChars[4];
            --vaultKeyChars[5];
            ++vaultKeyChars[6];
            --vaultKeyChars[7];
            ++vaultKeyChars[8];
            --vaultKeyChars[9];
            ++vaultKeyChars[10];
            --vaultKeyChars[11];
            Object vo = c.newInstance(new String(vaultKeyChars));
            vaultObject = vo;
            vaultAccessMethod = m;
        }
    }
}