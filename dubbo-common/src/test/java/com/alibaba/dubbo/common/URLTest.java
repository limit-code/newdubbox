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

    // TODO ä¸å¸Œæœ›ç©ºæ ¼ï¼Ÿ è¯¦è§ï¼š DUBBO-502 URLç±»å¯¹ç‰¹æ®Šå­—ç¬¦å¤„ç†ç»Ÿä¸€çº¦å®š
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

        // æ²¡æœ‰Valueçš„Keyï¼Œç”Ÿæˆçš„Valueå€¼ä½¿ç”¨Keyå€¼ï¼ï¼ -_-!!!
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
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?applicatiMAM*H  ”˜¦»©—©©™—©©ª§ªªš˜ª«š§™©™—™ª™§šº‰‡‰™ˆ§‰™˜‡™©š¨¹»ª§ªÊš¸Êª§Ë¼š§«»š§ºªªÈªª™¸ª¬©¸ªª©¨Ë»©¸ª°š¨»¬ª§ºº©¨¬Ë™˜ºÊª§«ºª˜ºÊª¨º¬§ªªšÈÊË©¸Ë«            Ày À     ‡¦   À‡©    À‡ªÈ    ˆ›™ÀÊ À ˆª‰°Ì  ˆ©‡  €˜ª‡ À €—«˜     ˜É™À   ª ÀÀ°€	À  °°»{    °        ÑÚN66â2/ô{9#F¢z¤=I”¦Åî%¦ÄñRbğM ^{Ğü'OşÎ„¨İ÷ÆDj½º[2' _73¢×‰àp}Dºh‰Ö*÷æËâì‡FõY-şÌÂ/^ñãË#è^A‡¾ó×'¾Ï7/¢m6;òğ‘p#¶¢vÛõøê.9;…æ‡h´£¤SNˆüãÑí]‡)4Sx‚ÔxßU_9õp’]/SÏáÃ½Aéøó@ÊÈm²o/à‘ìµÒÁèƒ¶³X½SkÉ½ÈOìYJe½­ê2%Ö	K>ä›.xDŠŞÍ­œœ¾šX}÷ ‘o•á¥Ncyo¤—äÉGÀ³ÂWéT›nÙšO›ä œj ¬iFF@µí7 ‘D
€<J•64[€ur/8É	ÀI rŞ=Œ=kõYLk2«›xätmÔÉ©ÀtASÊ
Ÿ?  ^İ×;èáğ€  é'Ğ£¶ƒ©ÎÁ®%E›‘ĞáÀR:²b“€ Æs¯à÷˜uE/ÿ :H 8ªøû@zM- æ´0<Àv/OÄûHoPÚ€P*-]Hy¯ £A3Ã -@4R[à¡“€˜²†a°Dl ôñ·€s¦Pü0u[Ì Á(şÜ €)`x2ê Ö>fô.4«ª}ÕJĞ)b¾àî¥- ÀÔ7<Çw!S*¾ô¢»ÁÀ èNxÀ’Â÷òòÀ¦«¦%¸ X£·  ÓÍğ@läÜ‚,œ[  Ïàôb	ØßL†sHôˆ8§ á©€šÂ
C^‚¨ñJ@G¦¾Axt)aò¥A½8}ŞqXíŸ6Ä >Ê)nxûßóÙ`K5'9NÓÁ£ Ì`UİçÒ¦ÜËoŒR3BPªwK€´RØâ°"ŒÂß0ğ2J0¨@%Ø,Áp	Ld›@N'Ğ>` "×ªh*,.ĞX€% Ô  lpà`	‚K`š Ø‚pt à	P„6*ğÔZ^À bf@ •7`€K\™ ĞÀ&ä€ O(Ø‡(øî4Ş4TMÃ€\ °240 ‚TÜ€	,Lpd@›Nt	<;ğ A(ótx  %Ôl à™'Ğ:@ø„‚¡†&¨@%Ø2Á  vÈ9eÁ(• `7:€ á"”Û0è 4M€x‚-”ˆ\˜°/7`°Á%	0M p‚Á
@EÃCÜ4Z Ànv ‹æ<@¸^` bf@€”JP°%À.€	€s°ızjœÊIuøCô?;ÛáGhÜ•C?Ûø x û`ûÃ·éÁ÷È¿@Vƒè7VãèA§†¢/Òş@a›“m¸ğ`^b @fl€à	pK``‚ šØp‚ 	t;àë"',È ÁƒÈ `38ì`ŞAVƒë±<áWÙ³.]P|Á€Ã1XààÁVƒŞ·Ó´W¤W i(Ìœ`iEshJöÌ‡H À@vƒÎQ"=¿Q:¨ˆTâGE¢?*Da|TuòQE>G*¢b>¨ˆéGE¢P:*ÅœQQ’¨ˆ¨ÎŠDíwT"ÊV£+Ê>‰Ê¡’]m‘È@À.Ü‚¹¶|‹JJPT/ÚŠ‹Ø‚3a£ø{p°~1ÀĞ*AÀn– ÀA&6œ è‚*`Á,ÀÀ@È.€	‚Lhr`À	N'Ğ>` "0¡h,ÀÀÈ@ÀĞ*AÀ	pN`Ğ€'ì'J¸«Š(µˆPD½4TQÊ£"e%fÔ°ìÄ«;ˆ¨ l‚¸î0àŒ B´ÎcÒ"9_.$.ŠPÈ`3Üº¨|Á]|€(àÀoÈ†Dr Ê ™<€€àtÁê›bĞ;ÀgĞâgƒç[Å`L‘M!øn¢éa6DÉg6D{Ğ1!Fág¯“Q°L‡úÚ¸’0öô˜â+Áğ2ÃÂ(<Ì@Ù‚„f):ª€=!:  Å f–€¸ &ä€Y	È@ÀØ8ÁĞ	:ğ-Ô`©Ìõ€…â/t<ğ	….°LÜ?ÚD ´6˜¶
’ZÉQ@½^øìAÅ“`„Ÿ$pŒA¼€‚XÀĞ9°| tkÕÜŠÛ
˜‰4Ø¸à´cdlĞ0Ä° ;l]Œ¶1PÈÀ†DÀÀuäóİWYÓ4*°€˜PA*nÀ ÂÉe–A\8@2¨ËÀ q„â ÈÁ—àÀ:pUôóêXézX<ı‹ÅAˆÖà‰PÑÃM®!l‚ ¡T€…Z× ‰*HT‡ Æ.l<·&m„Û@yÁü  } u`úÁìõÁõàÁø ü,Øºˆ6 m ¼àş‚ÔP XÁ*'ıÈB-Ø©¥šØÄƒjÖt+é'[A±¡hx  %Ôp €	Ml8A§Ã<áğ	;  Â14 ÈB/,047P¸A&4œ è(ØB¨_h€@	58p°Á%	09 BE,ŒàExˆ %Ô– ¸&	2M ÈA'	<;ğ¶bÂ/*`a"p¼À ÄÌ€	(6P€K\™ ĞÀ&ä¢ı–Ğm kíuDµşd¬îšZæãá¸Œ0:Àt ìÕšlÀ0 0‚Á<7èºµ8*dP•ÁXğË%…Ê°l ÛÀ¸Aö‚ `0	|Ñs{1è 9ÁdšréÆ £ Á`	o ß`ÁA…ƒˆ8 ];E—t†³î0pÈ²›%váÁ'¢.g%
E¨ˆL±‰zè	‰EEğDY ´¸àÕ<[ow×‘ƒ~{Ç€á~áçÃˆÅá¿@e0–AOKØ\àØ`àtAÊç@Ğ0œXĞ:`vÀúvúFuC±÷Ù™*ÑfxØµr=ÇÂNÃC240PJ%°0ÁÀ	N'Ğ>` "¡,ÀÀ@ÈĞÀ€jnÀ –&¸2 	M'È:@ø€‚ …ŠX( \`à dh`@	57`Ğ‚p<ğ	;( A °¸@`^b `	pL`¯à…íÜÁØ˜°V0&A$pŒA¼ÖŒl%Á•µoä`ğ	c½ªd¤p€	j6Pø‚.xÁ':Â.`Ú`xAøh 9`r@ùaªºL~ÒxbˆıFø5Ğ{à{@p@ıi ÒüáKlİAhé}ôZ-¦¹ úğøp8 qãòƒÏ¨t%áÍ(´‹Šˆ²Ü¨E”õETDÊn¢"D5ZT¢¢°DM†Ğ¢ø?*4êq“'l:B¥BˆZ­¨‹@@‚ÈŠÔÙ!:ìğp-.VÜÀ¾¿$.„*=˜lşšVÖZ,à	YE7p
ät€à	; UëŒµZPÕÀÈ@Á(l À	NĞLûÖà!’SpCjô9ÇÇ€+v€üíİÜˆ¼Añ¼jyuózäzÃÅñÄw È@~î2P¸@¼ßº€î‚@1Ç~@20É ‘PÈà9ÁeƒÑ¦dm ¯›ƒœpä#‡°RÑ€aü4a 1ƒÆƒŞ‚Ğ?†¸¬oûÜÃ\BÁ[‡h@ŠomİÃôĞÀ{¤·V¤*Ò.°¸0ä~X?8(q€ä`“T`ÁeƒÑ¦dv í ÜAøƒáA>ƒ‚ka´ ]âU?¬&ñÎÄ‰Œ/ „¥&¸6T4Ùû‚ %Ñî”ç|Ôá®ƒ~K¦gïx,ü Ğ>¶Hù·æ¤/p¿ wpÁb €4+P(Ár@¸P
?.‚l@àp‰r¨¦¨¹‹Š–E0V^M”_FE¢î9*%ËQŠ(iTD•|Q£h5‰¡D¥~Tˆâë¨E”ÔGQº¨ˆïGE¢~<*EöQE”–B…Q¸f Ó
‹ªÆ_xa,ˆ.†ß$á®ª>ş>8(rğæ Îåƒæƒ¥ƒTÜL”*‚j¶”x™©~€Š× AŒIø@	€oAÒ!Zş6,˜‰‰@O°Ø(ÂÔuÄíÀŞÁEM`Q°8|5C‰j@bJù’'Šı}-½Ş
¶Â¶‚OcôQiœ\$y ÔnP²¤¼Xt¬ît‡ ˜4ĞV‡£øHË~PˆÇ¥¯³aííEu†‹éî?+’5HƒA½ ¡T€;Õ§eC…ßMSºyd4FZŒbO.ĞÃZÁT±O£®‘¤4šA'!ëV¨*HU¥[¸p`^d `j–À„UÃXT&N@©Ê²Z°À
Y´@€À( •7`€LĞ€‚ ¢Z¼z‰Báÿ`h¢QBTDQ5"ª²¡¨ˆ*ø¢"‘Ã(ÎšTçøO
”ÎUÙCòÓ£ ˜
´Ôæœ$19R”8¥½8ñh¿,ÀÑ§˜&¿÷é¥¿8åß%ß0ñJ>ô7“â›HÒ!&”0oÅ&I$øÓŸ¯uT×2`Ô±ø‹	€a:ôÒ|gŞ’T’’Â•Í[VfAf•ÕÑ•b•„Ã	ä`F’ÙUWGaOöõaŒ «û§ô×I+®¤¡;jVRc5ík„1û¦æ‰}{óY&F›2ëh%d’è{k\F2`Š6h	½"€bZhÀ«Õ‘lèu¢	aAI¿‚Ÿyû2acŞªõb2ú‡HÄW!$QÓùÕën„`"š Jª'z£ ä!`Ø˜”l=”×c$dÓá¿Ñ»ıD/×+7íXú¯W
`­ÄNÌÕ&F tÕ”`fXE¤^±a“aÂ«	Rbïşd°0oÊâ`5cdy‚p¥¦e¬Æ~~WÉa½¨ ~R[jØŞ!°ÁBÔba£æq,_c˜-L9’ëZ`ÉÉÌ©=š[XvÍabô˜…®°Ä `ë®—HDdY©\rÿe'I†t®¦‘`CÚÍY~¬µbL~Í¬e$™XÅ5¼¶"O'ùsMÿJ-6ÄìòÙ}lâŸöÂĞT’óc"Éê¬£ÄÑC…ç?º	ó„üšä?o;ˆÿ/.Œd52"Í›YGõ_Ìé}CÇ³zÑ—¸àßÑ'vw«7òï”ÿ;'R ko-²gŸ‡D¤@ªfÇQÆ±È)2£§ònˆé$A"¡CßVÂùˆàD ü@"±<ÔD Bo`ˆ@D"Ì1mÀœ0tD BW ı”D	D ÂK@ˆ@D"„ñ€DäÂ3 ÿ€?Akê¹Òÿë2©uóKÔC¬£TÙ±†œ!­¨!„z¬B¦I$9ÓÉ‘—"`ÔˆˆˆˆˆˆˆˆÌŸ                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      