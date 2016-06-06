/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.security;

import android.test.InstrumentationTestCase;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

/**
 * Created by xuewu.wei on 2016/6/6.
 */
public class RSAUtilsTest extends InstrumentationTestCase {
    public void testRSA() throws Exception {
        HashMap<String, Object> map = RSAUtils.getKeys();
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        String modulus = publicKey.getModulus().toString();
        System.err.println("模 " + modulus);
        //公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        System.err.println("公钥指数 " + public_exponent);
        //私钥指数
        String private_exponent = privateKey.getPrivateExponent().toString();
        System.err.println("私钥指数 " + private_exponent);
        //明文
        String ming = "123456789";
        System.err.println("明文 " + ming);
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        System.err.println("密文 " + mi);
        //解密后的明文
        ming = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println("原文 " + ming);
    }
}