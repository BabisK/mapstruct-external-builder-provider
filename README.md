# MapStruct External Builder Provider

MapStruct automatically uses builder under the assumptions defined
[here](https://mapstruct.org/documentation/stable/reference/html/#mapping-with-builders). The
requirements can be summed as:

* The requested type has a static method that return a Builder for the type.
* The builder type has a public `build` (or differently named) method without parameters that
  returns the requested type.

While the second requirement is quite straightforward, the first is not, and limits the usage of
builders that are defined differently. This module aims to provide a solution for builders that are
defined as separate classes, unconnected to the type they create.

Consider the following class structure:

```java
public class Customer {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

```java
public interface CustomerDTO {

    Long getId();

    String getName();
}
```

```java
public class CustomerDTOBuilder {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CustomerDTOBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CustomerDTOBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CustomerDTO build() {
        CustomerDTOImpl impl = new CustomerDTOImpl();
        impl.id = id;
        impl.name = name;
        return impl;
    }

    private final class CustomerDTOImpl implements CustomerDTO {
        private Long id;
        private String name;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
```

```java
@Mapper
public interface CustomerToCustomerDTO {

    CustomerDTO map(Customer customer);
}
```

In this case, the `CustomerDTO` is defined as an interface while its implementation is a private
class within the builder. This module allows you to use this builder with MapStruct with the
following assumptions:

* The requested type has a static method that return a Builder for the type **OR** there is a class
  named as the requested type with the `Builder` suffix in the same package.
* The builder type has a public `build` (or differently named) method without parameters that
  returns the requested type.
  
Other cases this will be useful are if the `CustomerDTO` is a concrete class with package-private
setters, or if the CustomerDTOImpl is package-private class.

## Usage

To use this module, you need to add it as an annotation processor path in your compiler
configuration after the `mapstruct-processor` path. For example:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <annotationProcessorPaths>
            <path>
              <groupId>org.mapstruct</groupId>
              <artifactId>mapstruct-processor</artifactId>
              <version>1.4.2.Final</version>
            </path>
            <path>
              <groupId>com.github.babisk</groupId>
              <artifactId>mapstruct-external-builder-provider</artifactId>
              <version>0.1.0-SNAPSHOT</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
    </plugins>
  </build>
```