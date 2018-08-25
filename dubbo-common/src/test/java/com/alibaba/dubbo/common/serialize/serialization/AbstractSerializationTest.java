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
package com.alibaba.dubbo.common.serialize.serialization;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.model.AnimalEnum;
import com.alibaba.dubbo.common.model.BizException;
import com.alibaba.dubbo.common.model.BizExceptionNoDefaultConstructor;
import com.alibaba.dubbo.common.model.SerializablePerson;
import com.alibaba.dubbo.common.model.media.Image;
import com.alibaba.dubbo.common.model.media.Image.Size;
import com.alibaba.dubbo.common.model.media.Media;
import com.alibaba.dubbo.common.model.media.Media.Player;
import com.alibaba.dubbo.common.model.media.MediaContent;
import com.alibaba.dubbo.common.model.person.BigPerson;
import com.alibaba.dubbo.common.model.person.FullAddress;
import com.alibaba.dubbo.common.model.person.PersonInfo;
import com.alibaba.dubbo.common.model.person.PersonStatus;
import com.alibaba.dubbo.common.model.person.Phone;
import com.alibaba.dubbo.common.serialize.ObjectInput;
import com.alibaba.dubbo.common.serialize.ObjectOutput;
import com.alibaba.dubbo.common.serialize.Serialization;

/**
 * @author ding.lid
 */
public abstract class AbstractSerializationTest {
    Serialization         serialization;

    URL                   url                   = new URL("protocl", "1.1.1.1", 1234);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    static Random         random                = new Random();

    // ================ Primitive Type ================ 

    @Test
    public void test_Bool() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBool(false);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertFalse(deserialize.readBool());

        try {
            deserialize.readBool();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Bool_Multi() throws Exception {
        boolean[] array = new boolean[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextBoolean();
        }

        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        for (boolean b : array) {
            objectOutput.writeBool(b);
        }
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        for (boolean b : array) {
            assertEquals(b, deserialize.readBool());
        }

        try {
            deserialize.readBool();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Byte() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeByte((byte) 123);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals((byte) 123, deserialize.readByte());

        try {
            deserialize.readByte();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Byte_Multi() throws Exception {
        byte[] array = new byte[100];
        random.nextBytes(array);

        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        for (byte b : array) {
            objectOutput.writeByte(b);
        }
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        for (byte b : array) {
            assertEquals(b, deserialize.readByte());
        }

        try {
            deserialize.readByte();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Short() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeShort((short) 123);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals((short) 123, deserialize.readShort());

        try {
            deserialize.readShort();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Integer() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeInt(1);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        int i = deserialize.readInt();
        assertEquals(1, i);

        try {
            deserialize.readInt();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Long() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeLong(123L);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals(123L, deserialize.readLong());

        try {
            deserialize.readLong();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Float() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeFloat(1.28F);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertTrue(1.28F == deserialize.readFloat());

        try {
            deserialize.readFloat();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Double() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeDouble(1.28);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertTrue(1.28 == deserialize.readDouble());

        try {
            deserialize.readDouble();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_UtfString() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeUTF("123中华人民共和国");
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals("123中华人民共和国", deserialize.readUTF());

        try {
            deserialize.readUTF();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_Bytes() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBytes("123中华人民共和国".getBytes());
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertArrayEquals("123中华人民共和国".getBytes(), deserialize.readBytes());

        try {
            deserialize.readBytes();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_BytesRange() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBytes("123中华人民共和国-新疆维吾尔自治区".getBytes(), 1, 9);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        byte[] expectedArray = new byte[9];
        System.arraycopy("123中华人民共和国-新疆维吾尔自治区".getBytes(), 1, expectedArray, 0, expectedArray.length);
        assertArrayEquals(expectedArray, deserialize.readBytes());

        try {
            deserialize.readBytes();
            fail();
        } catch (IOException expected) {}
    }

    // ================== Util methods ==================

    <T> void assertObjectArray(T[] data, Class<T[]> clazz) throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertArrayEquals(data, clazz.cast(deserialize.readObject()));

        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }

    <T> void assertObjectArrayWithType(T[] data, Class<T[]> clazz) throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertArrayEquals(data, clazz.cast(deserialize.readObject(clazz)));

        try {
            deserialize.readObject(clazz);
            fail();
        } catch (IOException expected) {
        }
    }

    @SuppressWarnings("unchecked")
    <T> void assertObject(T data) throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals(data, (T) deserialize.readObject());

        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }

    <T> void assertObjectWithType(T data, Class<T> clazz) throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals(data, (T) deserialize.readObject(clazz));

        try {
            deserialize.readObject(clazz);
            fail();
        } catch (IOException expected) {
        }
    }
    
    // ================ Array Type ================ 
    
    @Test
    public void test_boolArray() throws Exception {
        boolean[] data = new boolean[] { true, false, true};
        
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        
        assertTrue(Arrays.equals(data, (boolean[]) deserialize.readObject()));
        
        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }
    
    @Test
    public void test_boolArray_withType() throws Exception {
        boolean[] data = new boolean[] { true, false, true};
        
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        
        assertTrue(Arrays.equals(data, (boolean[]) deserialize.readObject(boolean[].class)));
        
        try {
            deserialize.readObject(boolean[].class);
            fail();
        } catch (IOException expected) {
        }
    }
    
    @Test
    public void test_charArray() throws Exception {
        char[] data = new char[] { 'a', '中', '无' };
        
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        
        assertArrayEquals(data, (char[]) deserialize.readObject());
        
        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }
    
    @Test
    public void test_charArray_withType() throws Exception {
        char[] data = new char[] { 'a', '中', '无' };
        
        ObjectOutput objectOutput                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                zablePerson> args = new HashMap<Integer, SerializablePerson>();
        args.put(1, new SerializablePerson());

        assertObject(args);
    }

    @Test
    public void test_StringSPersonMap() throws Exception {
        Map<String, SerializablePerson> args = new HashMap<String, SerializablePerson>();
        args.put("1", new SerializablePerson());

        assertObject(args);
    }

    // ================ Complex Collection Type ================ 

    @Test
    public void test_StringSPersonListMap() throws Exception {
        Map<String, List<SerializablePerson>> args = new HashMap<String, List<SerializablePerson>>();

        List<SerializablePerson> sublist = new ArrayList<SerializablePerson>();
        sublist.add(new SerializablePerson());
        args.put("1", sublist);

        assertObject(args);
    }

    @Test
    public void test_SPersonListList() throws Exception {
        List<List<SerializablePerson>> args = new ArrayList<List<SerializablePerson>>();
        List<SerializablePerson> sublist = new ArrayList<SerializablePerson>();
        sublist.add(new SerializablePerson());
        args.add(sublist);

        assertObject(args);
    }
    
    // ================ complex POJO =============
    
    BigPerson bigPerson;
    {
        bigPerson = new BigPerson();
        bigPerson.setPersonId("superman111");
        bigPerson.setLoginName("superman");
        bigPerson.setStatus(PersonStatus.ENABLED);
        bigPerson.setEmail("sm@1.com");
        bigPerson.setPenName("pname");

        ArrayList<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("86", "0571", "87654321", "001");
        Phone phone2 = new Phone("86", "0571", "87654322", "002");
        phones.add(phone1);
        phones.add(phone2);
        
        PersonInfo pi = new PersonInfo();
        pi.setPhones(phones);
        Phone fax = new Phone("86", "0571", "87654321", null);
        pi.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "3480", "wensanlu", "315000");
        pi.setFullAddress(addr);
        pi.setMobileNo("13584652131");
        pi.setMale(true);
        pi.setDepartment("b2b");
        pi.setHomepageUrl("www.capcom.com");
        pi.setJobTitle("qa");
        pi.setName("superman");
        
        bigPerson.setInfoProfile(pi);
    }
    
    @Test
    public void test_BigPerson() throws Exception {
        assertObject(bigPerson);
    }
    
    @Test
    public void test_BigPerson_WithType() throws Exception {
        assertObjectWithType(bigPerson, BigPerson.class);
    }
    
    MediaContent mediaContent;
    {
        Media media = new Media();
        media.setUri("uri://中华人民共和国");
        media.setTitle("title");
        media.setWidth(1239);
        media.setHeight(1938);
        media.setFormat("format-xxxx");
        media.setDuration(93419235);
        media.setSize(3477897);
        media.setBitrate(94523);
        List<String> persons = new ArrayList<String>();
        persons.add("jerry");
        persons.add("tom");
        persons.add("lucy");
        media.setPersons(persons);
        media.setCopyright("1999-2011");
        media.setPlayer(Player.FLASH);
        
        List<Image> images = new ArrayList<Image>();
        for(int i = 0; i < 10; ++i) {
            Image image = new Image();
            image.setUri("url" + i);
            if(i % 2 == 0) image.setTitle("title" + i);
            image.setWidth(34 + i);
            image.setHeight(2323 + i);
            image.setSize((i % 2 == 0) ? Size.SMALL : Size.LARGE);
            
            images.add(image);
        }
        
       mediaContent = new MediaContent(media, images);
    }
    
    @Test
    public void test_MediaContent() throws Exception {
        assertObject(mediaContent);
    }
    
    @Test
    public void test_MediaContent_WithType() throws Exception {
        assertObjectWithType(mediaContent, MediaContent.class);
    }
    
    @Test
    public void test_MultiObject() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBool(false);
        objectOutput.writeObject(bigPerson);
        objectOutput.writeByte((byte) 23);
        objectOutput.writeObject(mediaContent);
        objectOutput.writeInt(-23);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals(false, deserialize.readBool());
        assertEquals(bigPerson, deserialize.readObject());
        assertEquals((byte) 23, deserialize.readByte());
        assertEquals(mediaContent, deserialize.readObject());
        assertEquals(-23, deserialize.readInt());

        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }
    
    @Test
    public void test_MultiObject_WithType() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBool(false);
        objectOutput.writeObject(bigPerson);
        objectOutput.writeByte((byte) 23);
        objectOutput.writeObject(mediaContent);
        objectOutput.writeInt(-23);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        assertEquals(false, deserialize.readBool());
        assertEquals(bigPerson, deserialize.readObject(BigPerson.class));
        assertEquals((byte) 23, deserialize.readByte());
        assertEquals(mediaContent, deserialize.readObject(MediaContent.class));
        assertEquals(-23, deserialize.readInt());

        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }
    
    
    // abnormal case 
    
    @Test
    public void test_MediaContent_badStream() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(mediaContent);
        objectOutput.flushBuffer();

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        for (int i = 0; i < byteArray.length; i++) {
            if(i%3 == 0) {
                byteArray[i] = (byte)~byteArray[i];
            }
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        
        try {
            ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
            @SuppressWarnings("unused") // local variable, convenient for debug
            Object read = deserialize.readObject();
            fail();
        } catch (IOException expected) {
            System.out.println(expected);
        }
    }
    
    @Test
    public void test_MediaContent_WithType_badStream() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(mediaContent);
        objectOutput.flushBuffer();

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        for (int i = 0; i < byteArray.length; i++) {
            if(i%3 == 0) {
                byteArray[i] = (byte)~byteArray[i];
            }
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        
        try {
            ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
            @SuppressWarnings("unused") // local variable, convenient for debug
            Object read = deserialize.readObject(MediaContent.class);
            fail();
        } catch (IOException expected) {
            System.out.println(expected);
        }
    }
    
    
    @Test(timeout=3000)
    public void test_LoopReference() throws Exception {
        Map<String, Object> map= new HashMap<String, Object>();
        map.put("k1", "v1");
        map.put("self", map);
        

        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(map);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        @SuppressWarnings("unchecked")
        Map<String, Object> output = (Map<String, Object>) deserialize.readObject();
        
        assertEquals("v1", output.get("k1"));
        assertSame(output, output.get("self"));
    }
    
    // ================ final field test ================
    
    @Test
    public void test_URL_mutable_withType() throws Exception {
        URL data = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&noValue");

        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);

        URL actual = (URL) deserialize.readObject(URL.class);
        assertEquals(data, actual);
        assertEquals(data.getParameters(), actual.getParameters());

        try {
            deserialize.readObject();
            fail();
        } catch (IOException expected) {
        }
    }
}