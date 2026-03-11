

# Custom Validation Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vennarshulytz/validation-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.vennarshulytz/validation-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java Version](https://img.shields.io/badge/Java-8%2B-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

##### 📖 English Documentation | 📖 [中文文档](README_zh.md)

A lightweight Spring Boot Starter that encapsulates validation logic in the Controller layer, keeping your entity classes clean and free from validation annotations.

## Project Repository

- **GitHub**：[vennarshulytz/validation-parent: Custom validation starter for Spring Boot](https://github.com/vennarshulytz/validation-parent)

---

- If you encounter any issues while using this project, feel free to open an Issue. Pull Requests are always welcome and highly appreciated.
- If you find this project helpful, please consider giving it a ⭐ Star on GitHub.
- Your support means a lot and helps keep the project actively maintained and improved.

## Background

In traditional Spring Boot projects, we typically use `Spring Validation` for parameter validation. However, we encountered several pain points in practice:

### 1. Entity Class Pollution

```java
// Spring Validation - Entity classes cluttered with annotations
public class UserDTO {

    @NotNull(message = "ID is required", groups = {Update.class})
    @Null(groups = {Create.class})
    private Long id;

    @NotBlank(message = "Username is required", groups = {Create.class, Update.class})
    @Size(min = 2, max = 20, message = "Username must be 2-20 characters", groups = {Create.class, Update.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    private String username;

    // ... more fields and annotations

    public interface Create {}
    public interface Update {}
}
```

### 2. Complex Group Validation

As business scenarios grow, group interfaces proliferate, making entity classes bloated and hard to read.

### 3. Difficult Multi-field Cross Validation

```java
// Spring Validation requires custom annotations + SpEL expressions
@ScriptAssert(lang = "javascript",
    script = "_this.startDate == null || _this.endDate == null || _this.startDate.before(_this.endDate)",
    message = "Start date must be before end date")
public class DateRangeDTO {
    private Date startDate;
    private Date endDate;
}
```

### 4. Complex Business Logic Validation

Validations involving database queries or remote calls are difficult to implement with Spring Validation.

## Key Advantages

| Feature | Custom Validation | Spring Validation |
|---------|----------------------|-------------------|
| Entity Pollution | ❌ No pollution | ✅ Heavy annotation pollution |
| Group Validation | ✅ No groups needed | ⚠️ Group interfaces everywhere |
| Multi-field Validation | ✅ Simple and intuitive | ⚠️ Requires SpEL, steep learning curve |
| Business Logic Validation | ✅ Plain Java code | ⚠️ Requires custom annotations |
| IDE Support | ✅ Full code completion & debugging | ⚠️ No SpEL support |
| Learning Curve | ✅ Low - just write normal code | ⚠️ Medium to High |
| Maintainability | ✅ Validation logic centralized | ⚠️ Scattered in entities |

##  Version Compatibility

| Starter Module                    | Spring Boot | JDK  | Servlet API |
| --------------------------------- | ----------- | ---- | ----------- |
| `validation-spring-boot-starter`  | 1.x / 2.x   | 8+   | javax       |
| `validation-spring-boot3-starter` | 3.x         | 17+  | jakarta     |

## Quick Start

### 1. Add Dependency

Choose the appropriate starter based on your Spring Boot version:

#### Spring Boot 1.x / Spring Boot 2.x（JDK 8+）

**Maven:**

```xml
<dependency>
    <groupId>io.github.vennarshulytz</groupId>
    <artifactId>validation-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'io.github.vennarshulytz:validation-spring-boot-starter:1.0.0'
```

#### Spring Boot 3.x（JDK 17+）

**Maven:**

```xml
<dependency>
    <groupId>io.github.vennarshulytz</groupId>
    <artifactId>validation-spring-boot3-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'io.github.vennarshulytz:validation-spring-boot3-starter:1.0.0'
```

### 2. Enable the Feature

Add `@EnableValidation` annotation to your main class:

```java
@SpringBootApplication
@EnableValidation
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. Start Using

Mark the class containing the methods that need parameter validation with the `@ValidatedExt` annotation.

```java
@ValidatedExt
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/create")
    public Result create(@RequestBody @ValidationRules({
            @ValidationRule(type = User.class, validators = {
                    @ValidateWith(validator = NotBlankValidator.class,
                                  fields = @FieldConfig(names = {"username", "phone"},
                                                        message = "cannot be blank"))
            })
    }) User user) {
        return Result.success();
    }
}
```

**Entity class stays clean:**

```java
// No validation annotations at all!
@Data
public class User {
    private Long id;
    private String username;
    private String phone;
    private String email;
}
```

## Basic Usage

### Core Annotations

#### @ValidationRules

Annotated on Controller method parameters, contains multiple validation rules.

```java
@ValidationRules({
    @ValidationRule(...),
    @ValidationRule(...),
    ...
})
```

#### @ValidationRule

Defines a single validation rule:

| Attribute | Type | Description |
|-----------|------|-------------|
| type | Class<?> | Target type to validate |
| path | String | Field path, supports nesting like `"manager.address"` |
| validators | ValidateWith[] | Field validator configurations |
| custom | Class<? extends CustomValidator> | Custom validator class |

#### @ValidateWith

Configures field validators:

| Attribute | Type | Description |
|-----------|------|-------------|
| validator | Class<? extends FieldValidator> | Validator class |
| fields | FieldConfig[] | Field configurations |
| message | String | Default error message |
| params | String[] | Default validation parameters |

#### @FieldConfig

Fine-grained configuration for individual fields:

| Attribute | Type | Description |
|-----------|------|-------------|
| names | String[] | Field names |
| message | String | Error message |
| params | String[] | Validation parameters |

### Basic Examples

#### Example 1: Simple Field Validation

```java
@Data
public class CreateOrderRequest {
    private String orderNo;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}

@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/create")
    public Result create(@RequestBody @ValidationRules({
            @ValidationRule(type = CreateOrderRequest.class, validators = {
                    @ValidateWith(validator = NotBlankValidator.class,
                                  fields = @FieldConfig(names = {"orderNo", "productName"},
                                                        message = "cannot be blank")),
                    @ValidateWith(validator = NotNullValidator.class,
                                  fields = @FieldConfig(names = "quantity",
                                                        message = "quantity is required")),
                    @ValidateWith(validator = NotNullValidator.class,
                                  fields = @FieldConfig(names = "price",
                                                        message = "price is required"))
            })
    }) CreateOrderRequest request) {
        return Result.success();
    }
}
```

#### Example 2: Different Messages for Different Fields

```java
@PostMapping("/register")
public Result register(@RequestBody @ValidationRules({
        @ValidationRule(type = RegisterRequest.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class, fields = {
                        @FieldConfig(names = "username", message = "Username is required"),
                        @FieldConfig(names = "password", message = "Password is required"),
                        @FieldConfig(names = "confirmPassword", message = "Confirm password is required")
                }),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username",
                                                    message = "Username must be 2-20 characters",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone",
                                                    message = "Invalid phone number format",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) RegisterRequest request) {
    return Result.success();
}
```

#### Example 3: Convenient Annotations for Simple Parameters

```java
@GetMapping("/detail")
public Result getDetail(@RequestParam @NotNullCheck(message = "ID is required") Long id,
                        @RequestParam @NotBlankCheck(message = "Token is required") String token) {
    return Result.success();
}
```

### Before and After Comparison

#### Before (Spring Validation)

```java
// ========== Entity class heavily polluted ==========
@Data
public class UserDTO {
    @NotNull(message = "ID is required", groups = Update.class)
    @Null(groups = Create.class)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 20, message = "Username must be 2-20 characters")
    private String username;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone format")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    public interface Create {}
    public interface Update {}
}

// ========== Controller ==========
@PostMapping("/create")
public Result create(@RequestBody @Validated(UserDTO.Create.class) UserDTO user) {
    return Result.success();
}

@PostMapping("/update")
public Result update(@RequestBody @Validated(UserDTO.Update.class) UserDTO user) {
    return Result.success();
}
```

#### After (Custom Validation)

```java
// ========== Entity class stays clean ==========
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String email;
}

// ========== Controller - Validation logic encapsulated ==========
@PostMapping("/create")
public Result create(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NullValidator.class,
                              fields = @FieldConfig(names = "id", message = "ID must be null when creating")),
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"username", "phone"}, message = "cannot be blank")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "Username must be 2-20 chars",
                                                    params = {"min=2", "max=20"}))
        })
}) UserDTO user) {
    return Result.success();
}

@PostMapping("/update")
public Result update(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "ID is required when updating")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "Username must be 2-20 chars",
                                                    params = {"min=2", "max=20"}))
        })
}) UserDTO user) {
    return Result.success();
}
```

## Advanced Usage

### Nested Object Validation (path attribute)

When entity classes contain nested objects, use the `path` attribute to precisely locate validation targets.

#### Entity Structure

```java
@Data
public class Department {
    private String name;
    private Employee manager;           // Single nested object
    private List<Employee> employees;   // Nested collection
}

@Data
public class Employee {
    private String id;
    private String name;
    private String phone;
    private Address address;            // Deep nesting
}

@Data
public class Address {
    private String province;
    private String city;
    private String detail;
}
```

#### Using path for Precise Validation

```java
@PostMapping("/department/save")
public Result save(@RequestBody @ValidationRules({
        // Validate Department direct fields
        @ValidationRule(type = Department.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "name", message = "Department name is required"))
        }),

        // Validate manager object (path = "manager")
        @ValidationRule(type = Employee.class, path = "manager", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"name", "phone"},
                                                    message = "Manager info is required"))
        }),

        // Validate all objects in employees list (path = "employees")
        @ValidationRule(type = Employee.class, path = "employees", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "id", message = "Employee ID is required"))
        }),

        // Deep nesting: validate address under manager
        @ValidationRule(type = Address.class, path = "manager.address", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"province", "city"},
                                                    message = "Address info incomplete"))
        }),

        // Deep nesting: validate address for each employee
        @ValidationRule(type = Address.class, path = "employees.address", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "detail", message = "Detail address is required"))
        })
}) Department department) {
    return Result.success();
}
```

### Path Matching Rules

When multiple `@ValidationRule` exist for the same type, the `path` matching rules are:

```java
@ValidationRules({
        // Rule 1: path = "manager", only matches manager field
        @ValidationRule(type = Employee.class, path = "manager", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "phone", message = "Manager phone required"))
        }),

        // Rule 2: path = "employees", only matches employees list
        @ValidationRule(type = Employee.class, path = "employees", validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "Employee ID required"))
        }),

        // Rule 3: path = "" (default), matches all Employee instances
        // EXCLUDING those matched by rules above
        @ValidationRule(type = Employee.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "name", message = "Name is required"))
        })
})
```

> **Priority**: Specific path > Default path (empty string). When `path` is empty, objects already matched by other same-type rules are excluded.

### Custom Validators (Complex Business Logic)

For multi-field cross validation or scenarios involving database queries, implement the `CustomValidator` interface.

#### Defining Custom Validators

```java
@Component
public class OrderCustomValidator implements CustomValidator<CreateOrderRequest> {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public void validate(CreateOrderRequest request, ValidationResult result) {

        // Multi-field cross validation: price * quantity check
        if (request.getPrice() != null && request.getQuantity() != null) {
            BigDecimal total = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            if (total.compareTo(new BigDecimal("100000")) > 0) {
                result.addError("price,quantity", "Order total cannot exceed 100,000", total);
            }
        }

        // Business logic validation: product existence check
        if (StringUtils.hasText(request.getProductNo())) {
            Product product = productService.findByNo(request.getProductNo());
            if (product == null) {
                result.addError("productNo", "Product not found", request.getProductNo());
                return; // Fail fast
            }

            // Inventory check
            int stock = inventoryService.getStock(request.getProductNo());
            if (request.getQuantity() != null && request.getQuantity() > stock) {
                result.addError("quantity",
                               String.format("Insufficient stock, current: %d", stock),
                               request.getQuantity());
            }
        }

        // Date range validation
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().after(request.getEndDate())) {
                result.addError("startDate,endDate", "Start date cannot be after end date", null);
            }
        }
    }
}
```

#### Using in Controller

```java
@PostMapping("/order/create")
public Result createOrder(@RequestBody @ValidationRules({
        // Basic field validation
        @ValidationRule(type = CreateOrderRequest.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"orderNo", "productNo"}, message = "cannot be blank")),
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = {"quantity", "price"}, message = "is required"))
        }),
        // Custom complex validation
        @ValidationRule(custom = OrderCustomValidator.class)
}) CreateOrderRequest request) {
    return orderService.create(request);
}
```

### Custom Validator vs Spring Validation

#### Spring Validation Approach

```java
// 1. Define annotation
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Invalid date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startField();
    String endField();
}

// 2. Implement validator
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    private String startField;
    private String endField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startField = annotation.startField();
        this.endField = annotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Use reflection to get field values...
        // Verbose code, and hard to inject Spring Beans
    }
}

// 3. Annotate on entity class
@ValidDateRange(startField = "startDate", endField = "endDate")
public class OrderDTO { ... }
```

#### Custom Validation Approach

```java
// Just implement the interface - as simple as writing a normal Service
@Component
public class OrderCustomValidator implements CustomValidator<OrderDTO> {

    @Autowired
    private SomeService someService; // Can inject any Spring Bean

    @Override
    public void validate(OrderDTO order, ValidationResult result) {
        if (order.getStartDate().after(order.getEndDate())) {
            result.addError("startDate,endDate", "Start date cannot be after end date", null);
        }
    }
}
```

### Manual Validation Result Handling

By default, validation failures automatically throw a `ValidationException`. However, in some scenarios you may want to handle validation errors yourself — for example, returning a custom error response format or allowing partial failures.

Simply declare a `ValidationResult` parameter in the method signature, and the framework will:

1. **Skip** throwing an exception automatically
2. **Inject** the collected validation result into that parameter

> This design follows the same convention as Spring MVC's `BindingResult` placed after a `@Valid` parameter.

#### Usage
```java
@PostMapping("/users")
public ResponseEntity<?> createUser(@RequestBody @ValidationRules(...) UserDTO dto, ValidationResult validationResult) {
    // The validation result is injected automatically; no ValidationException will be thrown 
    if (validationResult.hasErrors()) { 
        // Handle errors as you see fit
        return ResponseEntity.badRequest().body(validationResult.getErrors()); 
    }
    // Validation passed — proceed with business logic
    return ResponseEntity.ok(userService.save(dto));
}
```

#### Comparison

| Mode                | Method Signature                                             | On Validation Failure              |
| ------------------- | ------------------------------------------------------------ | ---------------------------------- |
| Automatic (default) | `createUser(@ValidationRules UserDTO dto)`                   | Throws `ValidationException`       |
| Manual              | `createUser(@ValidationRules UserDTO dto, ValidationResult result)` | Result injected; handled by caller |

#### Notes

- At most **one** `ValidationResult` parameter should be declared per method; only the first one will be recognized.
- The position of the `ValidationResult` parameter is flexible, but placing it right after the validated parameter is recommended for readability.
- Even when validation passes with no errors, the `ValidationResult` is still injected (`hasErrors()` returns `false`).

## Built-in Validators

| Validator | Description | Parameters |
|-----------|-------------|------------|
| AssertFalseValidator | Must be false validation | - |
| AssertTrueValidator | Must be true validation | - |
| DecimalMaxValidator | Decimal maximum value validation | value, inclusive |
| DecimalMinValidator | Decimal minimum value validation | value, inclusive |
| DigitsValidator | Numeric digits validation | integer, fraction |
| EmailValidator | Email format validation | - |
| FutureValidator | Must be a future date/time validation | - |
| FutureOrPresentValidator | Must be a present or future date/time validation | - |
| MaxValidator | Maximum value validation | value |
| MinValidator | Minimum value validation | value |
| NegativeValidator | Must be negative validation | - |
| NegativeOrZeroValidator | Must be negative or zero validation | - |
| NotBlankValidator | Not blank string validation | - |
| NotEmptyValidator | Not empty validation (string, collection, map, array) | - |
| NotNullValidator | Not null validation | - |
| NullValidator | Must be null validation | - |
| PastValidator | Must be a past date/time validation | - |
| PastOrPresentValidator | Must be a present or past date/time validation | - |
| PatternValidator | Regular expression validation | regexp |
| PositiveValidator | Must be positive validation | - |
| PositiveOrZeroValidator | Must be positive or zero validation | - |
| SizeValidator | Length/size validation | min, max |

### Usage Examples

```java
@ValidateWith(validator = SizeValidator.class,
              fields = @FieldConfig(names = "username",
                                    message = "Username must be between {min} and {max} characters",
                                    params = {"min=2", "max=20"}))

@ValidateWith(validator = PatternValidator.class,
              fields = @FieldConfig(names = "phone",
                                    message = "Invalid phone number format",
                                    params = {"regexp=^1[3-9]\\d{9}$"}))

```

### Built-in Check Annotations

| Check Annotation      | Description                                      | Parameters        |
| --------------------- | ------------------------------------------------ | ----------------- |
| @AssertFalseCheck     | Must be false                                    | -                 |
| @AssertTrueCheck      | Must be true                                     | -                 |
| @DecimalMaxCheck      | Decimal maximum value check                      | value, inclusive  |
| @DecimalMinCheck      | Decimal minimum value check                      | value, inclusive  |
| @DigitsCheck          | Numeric digits check                             | integer, fraction |
| @EmailCheck           | Email format check                               | -                 |
| @FutureCheck          | Must be a future date/time                       | -                 |
| @FutureOrPresentCheck | Must be a present or future date/time            | -                 |
| @MaxCheck             | Maximum value check                              | value             |
| @MinCheck             | Minimum value check                              | value             |
| @NegativeCheck        | Must be negative                                 | -                 |
| @NegativeOrZeroCheck  | Must be negative or zero                         | -                 |
| @NotBlankCheck        | Not blank string check                           | -                 |
| @NotEmptyCheck        | Not empty check (string, collection, map, array) | -                 |
| @NotNullCheck         | Not null check                                   | -                 |
| @NullCheck            | Must be null check                               | -                 |
| @PastCheck            | Must be a past date/time                         | -                 |
| @PastOrPresentCheck   | Must be a present or past date/time              | -                 |
| @PatternCheck         | Regular expression check                         | regexp            |
| @PositiveCheck        | Must be positive                                 | -                 |
| @PositiveOrZeroCheck  | Must be positive or zero                         | -                 |
| @SizeCheck            | Length/size check                                | min, max          |

### Usage Examples

```java
@GetMapping("/detail")
public Result getDetail(@RequestParam @NotNullCheck(message = "ID is required") Long id,
                        @RequestParam @NotBlankCheck(message = "Token is required") String token) {
    return Result.success();
}
```

## Custom Validators

### Implementing Custom Field Validators

```java
@Component
public class IdCardValidator implements FieldValidator {

    @Override
    public boolean validate(Object value, Map<String, String> params) {
        if (value == null) {
            return true; // Let NotNull validator handle null values
        }
        String idCard = value.toString();
        return isValidIdCard(idCard);
    }

    @Override
    public String getDefaultMessage() {
        return "Invalid ID card number";
    }

    private boolean isValidIdCard(String idCard) {
        return idCard.matches("^\\d{17}[\\dXx]$");
    }
}
```

### Using Custom Field Validators

```java
@ValidateWith(validator = IdCardValidator.class,
              fields = @FieldConfig(names = "idCard", message = "Please enter a valid ID card number"))
```

## Configuration

### @EnableValidation Annotation

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| mode | ValidationMode | true | Enable fail-fast mode |
| enableI18n | boolean | false | Enable i18n message support |

```java
// Fail-fast mode (default): Return on first error
@EnableValidation

// Full validation mode: Collect all errors before returning
@EnableValidation(ValidationMode.FAIL_ALL)

// Enable i18n message support
@EnableValidation(enableI18n = true)
```

### Internationalization Configuration

After enabling i18n, configure in `messages.properties`:

```properties
# messages.properties
validation.user.username.notblank=用户名不能为空
validation.user.phone.pattern=手机号格式不正确

# messages_en.properties
validation.user.username.notblank=Username cannot be blank
validation.user.phone.pattern=Invalid phone number format
```

Usage:

```java
@FieldConfig(names = "username", message = "validation.user.username.notblank")
```

### Error Response Format

```json
{
    "code": 400,
    "message": "Validation failed",
    "errors": [
        {
            "field": "username",
            "message": "Username cannot be blank",
            "rejectedValue": null
        },
        {
            "field": "phone",
            "message": "Invalid phone number format",
            "rejectedValue": "123456"
        }
    ]
}
```

## Comparison with Spring Validation

### Feature Comparison

| Scenario | Custom Validation | Spring Validation |
|----------|----------------------|-------------------|
| Simple null check | ✅ Simple | ✅ Simple |
| Group validation | ✅ No groups needed | ⚠️ Requires group interfaces |
| Nested object validation | ✅ path attribute | ✅ @Valid cascade |
| Multi-field validation | ✅ CustomValidator | ⚠️ Requires SpEL or custom annotations |
| Business logic validation | ✅ Can inject Spring Beans | ❌ Difficult to inject |
| Entity class cleanliness | ✅ No pollution | ❌ Heavy annotations |
| Debugging | ✅ Normal breakpoints | ⚠️ SpEL hard to debug |
| IDE support | ✅ Full code completion | ⚠️ No SpEL support |

### Code Volume Comparison

**Entity validation for create + update endpoints:**

| Approach | Entity Code | Controller Code | Extra Classes |
|----------|-------------|-----------------|---------------|
| Spring Validation | ~50 lines of annotations | ~10 lines | 2 group interfaces |
| Custom Validation | 0 | ~40 lines | 0 |

Although Controller code slightly increases, benefits include:

1. Entity classes stay completely clean, reusable across layers
2. No need to maintain group interfaces
3. Validation logic centralized, easy to maintain
4. Different endpoint validations are independent

## Module Structure

```
validation-parent/
├── validation-core                  # Core module
├── validation-spring-boot-starter   # Spring Boot 1.x / Spring Boot 2.x support  (JDK 8+)
└── validation-spring-boot3-starter  # Spring Boot 3.x support (JDK 17+)
```

## Contributing

Issues and Pull Requests are welcome!

## License

This project is licensed under the [Apache License 2.0](LICENSE).

---
