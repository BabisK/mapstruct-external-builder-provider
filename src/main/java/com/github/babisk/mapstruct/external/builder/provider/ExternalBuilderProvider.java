package com.github.babisk.mapstruct.external.builder.provider;

import java.util.Collection;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.mapstruct.ap.spi.BuilderInfo;
import org.mapstruct.ap.spi.DefaultBuilderProvider;

public class ExternalBuilderProvider extends DefaultBuilderProvider {

    @Override
    public BuilderInfo findBuilderInfo(TypeMirror type) {
        TypeElement typeElement = getTypeElement(type);
        if ( typeElement == null ) {
            return null;
        }
        if (!shouldIgnore(typeElement)) {
            String name = type.toString();
            TypeElement builderElement = elementUtils.getTypeElement(name + "Builder");
            if (builderElement != null) {
                return createBuilderInfo(builderElement, typeElement);
            }
        }
        return super.findBuilderInfo(type);
    }

    public BuilderInfo createBuilderInfo(TypeElement builderElement, TypeElement typeElement) {
        BuilderInfo.Builder builderInfoBuilder = new BuilderInfo.Builder();
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(builderElement.getEnclosedElements());
        for (ExecutableElement constructor: constructors) {
            if (constructor.getParameters().isEmpty()) {
                builderInfoBuilder.builderCreationMethod(constructor);
            }
        }
        Collection<ExecutableElement> buildMethods = findBuildMethods(builderElement, typeElement);
        builderInfoBuilder.buildMethod(buildMethods);
        return builderInfoBuilder.build();
    }
}
