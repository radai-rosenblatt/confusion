/*
 * This file is part of Confusion.
 *
 * Confusion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Confusion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Confusion.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.confusion.spring;

import com.google.common.io.ByteStreams;
import net.radai.confusion.cats.Cats;
import net.radai.confusion.core.api.ConfigurationService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by Radai Rosenblatt
 */
public class SpringAnnotationConfigTest {

    @Test
    public void testSpringAnnotationIntegration() throws Exception {
        Path dir = Files.createTempDirectory("test");
        Path targetFile = dir.resolve("confFile");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cats.ini");
             OutputStream os = Files.newOutputStream(targetFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)){
            ByteStreams.copy(is, os);
        }
        String absolutePath = targetFile.toFile().getCanonicalPath();
        System.setProperty("confFile", absolutePath);
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("annotationSpringContext.xml");
        //noinspection unchecked
        SpringAwareConfigurationService<Cats> confService = (SpringAwareConfigurationService<Cats>) context.getBean(ConfigurationService.class);
        Jsr330AnnotatedConfigurationConsumerBean consumer = context.getBean(Jsr330AnnotatedConfigurationConsumerBean.class);
        //noinspection unchecked
        Assert.assertNotNull(consumer.getConfService());
        Assert.assertNotNull(consumer.getInitialConf());
        Assert.assertTrue(consumer.getCats().isEmpty());

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cat.ini");
             OutputStream os = Files.newOutputStream(targetFile, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
            ByteStreams.copy(is, os);
        }
        Thread.sleep(100);

        //verify event was received
        Assert.assertEquals(1, consumer.getCats().size());
        Assert.assertTrue(consumer.getInitialConf() != consumer.getCats().get(0));
        Assert.assertTrue(confService.getConfiguration() == consumer.getCats().get(0));

        context.close();
    }
}
