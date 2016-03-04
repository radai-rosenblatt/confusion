/*
 * This file is part of ConfigLib.
 *
 * ConfigLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConfigLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ConfigLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.configlib.core.ini.cats;

import com.google.common.io.ByteStreams;
import net.radai.configlib.core.ini.IniUtil;
import net.radai.configlib.ini.IniBeanCodec;
import org.ini4j.Ini;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Radai Rosenblatt
 */
public class CatsTest {

    @Test
    public void testParsingCats() throws Exception {
        IniBeanCodec codec = new IniBeanCodec("UTF-8");
        Cats cats;
        byte[] contents;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cats.ini")) {
            contents = ByteStreams.toByteArray(is);
        }
        cats = codec.parse(Cats.class, new ByteArrayInputStream(contents));
        Assert.assertNotNull(cats);
        Assert.assertEquals("bob", cats.getCreator());
        Assert.assertEquals(Arrays.asList("funny cats", "and stuff"), cats.getComments());
        Assert.assertEquals(Arrays.asList(
                new Cat("Maru", "Master of Boxes", Arrays.asList("boxes", "more boxes")),
                new Cat("Tardar Sauce", "Grumpy Cat", Collections.singletonList("nothing")),
                new Cat("Snowball", "Kitler", null)
                ), cats.getCats());

        String serialized;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            codec.serialize(cats, os);
            serialized = new String(os.toByteArray(), codec.getCharset());
        }
        Ini original = IniUtil.read(new InputStreamReader(new ByteArrayInputStream(contents), "UTF-8"));
        Ini produced = IniUtil.read(new StringReader(serialized));
        Assert.assertTrue(IniUtil.equals(original, produced, false));
    }

    @Test
    public void testParsingCat() throws Exception {
        IniBeanCodec codec = new IniBeanCodec("UTF-8");
        Cats cats;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cat.ini")) {
            cats = codec.parse(Cats.class, is);
        }
        Assert.assertNotNull(cats);
        Assert.assertEquals("bob", cats.getCreator());
        Assert.assertEquals(Collections.singletonList("just a single cat"), cats.getComments());
        Assert.assertEquals(Collections.singletonList(
                new Cat("Colonel Meow", "Guinness World Record Holder For Longest Cat Hair", Collections.singletonList("baths"))
        ), cats.getCats());
    }
}
