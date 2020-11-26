/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.context.properties;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for
 * {@link EnableConfigurationProperties @EnableConfigurationProperties}.
 *
 * @author Phillip Webb
 */
// 使用 @Component 可以将 @ConfigurationProperties 的 bean 加载，通过注入这个 bean，就可以取到属性。
// 有些第三方的包，使用了 @ConfigurationProperties 注解，但是没有 @Component 注解，这个时候使用者就需要使用 @EnableConfigurationProperties，
// 来把 @ConfigurationProperties 的类转成 BeanDefinition 注册到 BeanFactory 中。
class EnableConfigurationPropertiesRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		registerInfrastructureBeans(registry);
		ConfigurationPropertiesBeanRegistrar beanRegistrar = new ConfigurationPropertiesBeanRegistrar(registry);
		getTypes(metadata).forEach(beanRegistrar::register);
	}

	private Set<Class<?>> getTypes(AnnotationMetadata metadata) {
		// demo：@EnableConfigurationProperties({UserProperties.class, XxxProperties.class})
		// 将一个类上的所有 @EnableConfigurationProperties 注解里的 value 值，解析到 Set 里。
		return metadata.getAnnotations().stream(EnableConfigurationProperties.class)
				.flatMap((annotation) -> Arrays.stream(annotation.getClassArray(MergedAnnotation.VALUE)))
				.filter((type) -> void.class != type).collect(Collectors.toSet());
	}

	@SuppressWarnings("deprecation")
	static void registerInfrastructureBeans(BeanDefinitionRegistry registry) {
		ConfigurationPropertiesBindingPostProcessor.register(registry);
		ConfigurationPropertiesBeanDefinitionValidator.register(registry);
		ConfigurationBeanFactoryMetadata.register(registry);
	}

}
