/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.alibaba.dubbo.common;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.dubbo.common.utils.CollectionUtils;

/**
 * @author ding.lid
 * @author william.liangf
 */
public class URLTest {

    @Test
    public void test_valueOf_noProtocolAndHost() throws Exception {
        URL url = URL.valueOf("/context/path?version=1.0.0&application=morgan");
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));


        url = URL.valueOf("context/path?version=1.0.0&application=morgan");
        //                 ^^^^^^^ Caution , parse as host
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("context", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_valueOf_noProtocol() throws Exception {
        URL url = URL.valueOf("10.20.130.230");
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals(null, url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("10.20.130.230:20880");
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals(null, url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("10.20.130.230/context/path");
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("10.20.130.230:20880/context/path");
        assertNull(url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        assertNull(url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_valueOf_noHost() throws Exception {
        URL url = URL.valueOf("file:///home/user1/router.js");
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("home/user1/router.js", url.getPath());
        assertEquals(0, url.getParameters().size());

        // Caution!! 
        url = URL.valueOf("file://home/user1/router.js");
        //                      ^^ only tow slash!
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("home", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("user1/router.js", url.getPath());
        assertEquals(0, url.getParameters().size());


        url = URL.valueOf("file:/home/user1/router.js");
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("home/user1/router.js", url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("file:///d:/home/user1/router.js");
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("d:/home/user1/router.js", url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("file:///home/user1/router.js?p1=v1&p2=v2");
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("home/user1/router.js", url.getPath());
        assertEquals(2, url.getParameters().size());
        Map<String, String> params = new HashMap<String, String>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        assertEquals(params, url.getParameters());

        url = URL.valueOf("file:/home/user1/router.js?p1=v1&p2=v2");
        assertEquals("file", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertNull(url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("home/user1/router.js", url.getPath());
        assertEquals(2, url.getParameters().size());
        params = new HashMap<String, String>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        assertEquals(params, url.getParameters());
    }

    @Test
    public void test_valueOf_WithProtocolHost() throws Exception {
        URL url = URL.valueOf("dubbo://10.20.130.230");
        assertEquals("dubbo", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals(null, url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("dubbo://10.20.130.230:20880/context/path");
        assertEquals("dubbo", url.getProtocol());
        assertNull(url.getUsername());
        assertNull(url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals(null, url.getPath());
        assertEquals(0, url.getParameters().size());

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880?version=1.0.0");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals(null, url.getPath());
        assertEquals(1, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&noValue");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(3, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));
        assertEquals("noValue", url.getParameter("noValue"));
    }

    // TODO 不希望空格？ 详见： DUBBO-502 URL类对特殊字符处理统一约定
    @Test
    public void test_valueOf_spaceSafe() throws Exception {
        URL url = URL.valueOf("http://1.2.3.4:8080/path?key=value1 value2");
        assertEquals("http://1.2.3.4:8080/path?key=value1 value2", url.toString());
        assertEquals("value1 value2", url.getParameter("key"));
    }

    @Test
    public void test_noValueKey() throws Exception {
        URL url = URL.valueOf("http://1.2.3.4:8080/path?k0&k1=v1");

        assertTrue(url.hasParameter("k0"));

        // 没有Value的Key，生成的Value值使用Key值！！ -_-!!!
        assertEquals("k0", url.getParameter("k0"));
    }

    @Test
    public void test_valueOf_Exception_noProtocol() throws Exception {
        try {
            URL.valueOf("://1.2.3.4:8080/path");
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("url missing protocol: \"://1.2.3.4:8080/path\"", expected.getMessage());
        }
    }

    @Test
    public void test_getAddress() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        assertEquals("10.20.130.230:20880", url1.getAddress());
    }

    @Test
    public void test_getAbsolutePath() throws Exception {
        URL url = new URL("p1", "1.2.2.2",  33);
        assertEquals(null, url.getAbsolutePath());

        url = new URL("file", null, 90, "/home/user1/route.js");
        assertEquals("/home/user1/route.js", url.getAbsolutePath());
    }

    @Test
    public void test_equals() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");

        Map<String, String> params = new HashMap<String, String>();
        params.put("version", "1.0.0");
        params.put("application", "morgan");
        URL url2 = new URL("dubbo", "admin", "hello1234", "10.20.130.230", 20880, "context/path", params);

        assertEquals(url1, url2);
    }

    @Test
    public void test_toString() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        assertThat(url1.toString(), anyOf(
                equalTo("dubbo://10.20.130.230:20880/context/path?version=1.0.0&application=morgan"),
                equalTo("dubbo://10.20.130.230:20880/context/path?application=morgan&version=1.0.0"))
                );
    }

    @Test
    public void test_toFullString() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        assertThat(url1.toFullString(), anyOf(
                equalTo("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan"),
                equalTo("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&version=1.0.0"))
                );
    }

    @Test
    public void test_set_methods() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");

        url = url.setHost("host");

        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = url.setPort(1);

        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(1, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = url.setPath("path");

        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(1, url.getPort());
        assertEquals("path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = url.setProtocol("protocol");

        assertEquals("protocol", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(1, url.getPort());
        assertEquals("path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = url.setUsername("username");

        assertEquals("protocol", url.getProtocol());
        assertEquals("username", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(1, url.getPort());
        assertEquals("path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));

        url = url.setPassword("password");

        assertEquals("protocol", url.getProtocol());
        assertEquals("username", url.getUsername());
        assertEquals("password", url.getPassword());
        assertEquals("host", url.getHost());
        assertEquals(1, url.getPort());
        assertEquals("path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("1.0.0", url.getParameter("version"));
        assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_removeParameters() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");

        url = url.removeParameter("version");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(3, url.getParameters().size());
        assertEquals("morgan", url.getParameter("application"));
        assertEquals("v1", url.getParameter("k1"));
        assertEquals("v2", url.getParameter("k2"));
        assertNull(url.getParameter("version"));

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");
        url = url.removeParameters("version", "application", "NotExistedKey");
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("v1", url.getParameter("k1"));
        assertEquals("v2", url.getParameter("k2"));
        assertNull(url.getParameter("version"));
        assertNull(url.getParameter("application"));

        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");
        url = url.removeParameters(Arrays.asList("version", "application"));
        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("v1", url.getParameter("k1"));
        assertEquals("v2", url.getParameter("k2"));
        assertNull(url.getParameter("version"));
        assertNull(url.getParameter("application"));
    }

    @Test
    public void test_addParameter() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameter("k1", "v1");

        assertEquals("dubbo", url.getProtocol());
        assertEquals("admin", url.getUsername());
        assertEquals("hello1234", url.getPassword());
        assertEquals("10.20.130.230", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("context/path", url.getPath());
        assertEquals(2, url.getParameters().size());
        assertEquals("morgan", url.getParameter("application"));
        assertEquals("v1", url.getParameter("k1"));
    }

    @Test
    public void test_addParameter_sameKv() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&k1=v1");
        URL newUrl = url.addParameter("k1", "v1");

        assertSame(newUrl, url);
    }


    @Test
    public void test_addParameters() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?applicatiMAM*H  ���������������������������������������������������ʚ�ʪ�˼���������Ȫ�����������˻���������������˙��ʪ������ʪ���������˩�˫            �y �     ��   ���    ����    ����� ������������  ����� ������   ���ə� ��� ����	�  ���{    �        ��N66�2/�{�9#F�z�=I����%���Rb�M�^{��'O�΄����Dj��[2'�_73�׉��p}D�h��*�����F�Y-���/^���#�^A����'��7/�m6;��p#��v����ꁏ.9;��h���SN�����]�)4Sx���x�U_9�p�]/�S����A���@��m�o/�����胶�X�Sk���O�YJe���2%�	K>�.xD��ͭ����X}� �o���Ncyo����G���W�T�nٚO�� �j� �iFF@��7 �D
�<J�64[�ur/8�	�I� r�=�=k�YLk2��x�tm�ɩ�tAS��
�?  ^��;����  �'У�������%E������R:��b�� �s����uE/� :H 8����@zM- �0<�v/O��HoPڀP*-]Hy� �A3� -@4R[ࡓ����a�Dl ��s�P�0u[� �(�� �)`x2��>f�.4���}�J�)b���- ��7<�w!S*������ �Nx�����������%� X��  ���@l�܂,�[  ���b	��L�sH�8� ᩀ��
C^���J@G��Axt)a�A�8}�qX�6� >�)nx����`K5'9N��� �`U��Ҧ��o�R3BP�wK��R��"���0�2J0�@%�,�p	Ld�@�N'�>�`�"תh*,.�X�����%�� �lp�`	�K`� ��pt��	P�6*��Z^� bf@��7`�K\� ��&䝀�O(؇(��4�4TM��\��240��T܀	,Lpd@�N�t�	<;� A(�tx�� %�l���'�:@������&�@%�2� �v�9e�(��`7:� �"��0� 4M�x�-��\��/7`��%	0M�p��
@E�C�4Z �nv ��<@�^` bf@��JP���%�.��	�s��zj��Iu�C��?;��GhܕC?��� x �`�÷���ȿ@V��7V��A���/��@a��m��`^b @fl��	pK``� ��p��	t;��"',� ����`38�`�AV��<�Wٳ.]P|���1X���V�޷ӴW�W i(��`iEshJ�̇H��@v��Q"=�Q:��T�GE�?*Da|Tu�QE>G*�b>���GE�P:*ŜQQ����Ύ�D�wT"�V�+�>����]m��@�.�܂��|�JJPT/ڊ�؂3a��{p�~1��*A�n� �A&6� ��*`�,��@�.�	�Lhr`�	�N'�>�`�"0�h,���@��*A�	pN`Ё�'�'J���(��PD�4TQ��"e%f԰���;�� l���0�� B��c�"9�_.$.�P�`3ܺ�|�]|��(��o��Dr � �<���t���b�;�g��g��[�`L�M!�n��a6D�g6D{�1!F�g��Q�L��ڸ�0�����+��2��(<�@ق�f):��=!:  � f��� &䝀Y	�@��8��	:�-�`������/t<��	�.��L�?�D��6��
�Z�Q@�^��Aœ`��$p�A���X��9�|�tk�܊�
��4ظ��cdlЁ0���;l]��1P���D��u���WY�4*����PA*n� ��e�A\8@2��� �q�� �����:pU���X�zX<���A�����P��M�!l���T��Z� �*HT� �.l<�&m��@y��  }�u`�����������,���6 m������P X�*'��B-ة���ăj�t+�'[A��hx���� %�p��	Ml8A��<��	;  �14 �B/,047P�A&4� �(�B�_h�@	58p��%	09��BE,���Ex� %Ԗ��&	2M�ȁA'	<;�b�/*`a�"p�� �̀	(6P�K\� ��&����m�k�uD��d�Z��Ḍ0:�t �՚l�0 0��<7躵8*dP��X��%���l ���A�� `0�	|�s{1� 9�d�r�� � �`	o �`�A���8�];E�t���0pȲ�%v��'�.g%
E��L��z�	�EE�DY �����<[owב�~{���~�������@e0�AOK�\��`�tA��@Ё0�X�:`v��v�FuC���ٙ*�fx؁�r=��N�C240P�J%�0��	�N'�>�`�"�,��@����jn� �&�2�	�M'�:@���� ��X(�\`� dh`@	57`��p<��	;( A���@`^b `	pL`������ؘ�V0&A$p�A���l%���o�`��	c��d�p�	j6P��.x�':��.`�`xA�h 9`r@�a��L~�xb��F�5�{�{@p@�i ���Kl�Ah�}�Z-������p8 q����t%��(�����ܨE��ETD�n�"D5ZT���DM�Т�?*4�q�'l:B�B�Z���@@�Ȋ����!:��p-.V�����$.�*=�l��V�Z,�	YE7p
�t��	; U���ZP���@�(l��	�N�L���!�SpCj��9�ǀ+v�����ܞ��A�jyu�z�z����w �@~�2P�@�ߺ��@1�~@20� �P��9�e���dm�����p�#��Rрa�4a 1�ƃ���?���o���\B�[�h@�om�����{��V�*�.��0�~X?8(q��`�T`�e���dv�� �A���A>��ka� ]�U?�&��ĉ�/���&�6�T4��� %���|�ᮃ~K�g�x�,�� �>�H����/p� wp�b� �4+P(�r�@�P
?.��l@�p�r�������E0V^M�_FE��9*%�Q�(i�TD�|Q�h5���D�~T���E��GQ����GE�~<*E�QE��B�Q�f �
���_xa,�.��$����>�>8(r����惥�T�L�*�j���x���~��נA�I�@	�oA�!Z�6,���@O�؁(��u�����EM`Q�8|5C�j@bJ��'���}-��
�¶�Oc�Qi�\$y��nP���X�t��t� �4�V����H�~P�ǥ��a��Eu����?+�5H�A� �T�;էeC��MS�yd4FZ�bO.��Z�T�O����4�A'!�V�*HU�[�p`^d `j���U�XT&N@�ʲZ��
Y�@��(��7`�LЀ� �Z�z�B��`�h�QBTDQ5"�����*��"��(��T��O
��U�C�ӣ �
���$19R�8��8�h�,����&�����8��%�0�J>�7��H�!&�0o�&I$�ӟ�uT�2`Ա��	�a:��|g��T���[VfAf��ѕb���	�`F��UWGaO��a� �����I+���;jVRc5�k�1����}{�Y&F�2�h%d��{k\F2`�6h	��"�bZh��Ցl�u�	aAI���y�2ac���b2���H�W!$Q����n�`"��J�'z� �!`ؘ�l=��c$d��ѻ�D/�+7�X��W
`��N��&F�t՝�`fXE�^��a�a«	Rb��d�0o��`5cdy�p��e���~~W�a�� ~R[j��!��B�ba��q,_c�-L9���Z`��̩=�[Xv�ab������ `뮗HDd�Y�\r�e'I�t���`C��Y~��bL~ͬe$�X�5��"O'�sM�J-6����}l�����T��c"�ꬣ��C��?�	����?o;��/.�d52"͛YG�_��}Cǳzї����'vw��7���;'R�ko-�g��D�@�f�QƱ�)2���n��$A"��C�V����D �@"�<ԁD Bo`�@D"�1m��0t�D BW���D	D �K@�@D"���D��3 ��?Ak����2�u�K�C��Tٱ��!��!�z�B�I$9�ɑ�"`Ԉ�������̟                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      