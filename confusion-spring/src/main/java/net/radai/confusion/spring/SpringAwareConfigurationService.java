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

import net.radai.confusion.core.api.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Created by Radai Rosenblatt
 * using spring's startup/shutdown interfaces for broader compatibility
 */
public class SpringAwareConfigurationService<T> implements
        ConfigurationService<T>, InitializingBean, DisposableBean, FactoryBean<T>,
        ApplicationEventPublisherAware, ConfigurationListener<T> {
    private final ConfigurationService<T> delegate;
    private ApplicationEventPublisher springPublisher;

    public SpringAwareConfigurationService(ConfigurationService<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void register(ConfigurationListener<T> newListener) {
        delegate.register(newListener);
    }

    @Override
    public void unregister(ConfigurationListener<T> existingListener) {
        delegate.unregister(existingListener);
    }

    @Override
    public T getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public void updateConfiguration(T newConfiguration) throws IOException, InvalidConfigurationException {
        delegate.updateConfiguration(newConfiguration);
    }

    @Override
    public Class<T> getConfigurationType() {
        return delegate.getConfigurationType();
    }

    @Override
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        delegate.register(this);
        if (delegate instanceof ServiceLifecycle) {
            ((ServiceLifecycle)delegate).start();
        }
    }

    @Override
    @PreDestroy
    public void destroy() throws Exception {
        delegate.unregister(this);
        if (delegate instanceof ServiceLifecycle) {
            ((ServiceLifecycle)delegate).stop();
        }
    }

    @Override
    public T getObject() throws Exception {
        return delegate.getConfiguration();
    }

    @Override
    public Class<?> getObjectType() {
        return getConfigurationType();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        springPublisher = applicationEventPublisher;
    }

    @Override
    public void configurationChanged(ConfigurationChangeEvent<T> event) {
        springPublisher.publishEvent(new SpringConfigurationChangedEvent<>(this, event));
    }

    @Override
    public void invalidConfigurationRead(InvalidConfigurationEvent<T> event) {
        springPublisher.publishEvent(new SpringInvalidConfigurationEvent<>(this, event));
    }
}
