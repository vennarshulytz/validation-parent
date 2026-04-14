# Custom Validation Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vennarshulytz/validation-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.vennarshulytz/validation-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java Version](https://img.shields.io/badge/Java-8%2B-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

##### [📖 English Documentation](README.md) | 📖 中文文档

一个轻量级的 Spring Boot Starter，将参数校验逻辑内聚在 Controller 层，避免实体类被校验注解污染。

## 项目地址

- **GitHub**：[vennarshulytz/validation-parent: Custom validation starter for Spring Boot](https://github.com/vennarshulytz/validation-parent)

---

- 如果在使用过程中遇到问题，欢迎随时提交 Issue；也非常欢迎通过 PR 参与改进。 
- 如果这个项目对你有所帮助，欢迎在 GitHub 上点个 ⭐ Star 支持一下。
- 你的支持是开源作者持续维护和迭代项目的重要动力！

## 项目背景

在传统的 Spring Boot 项目中，我们通常使用 `Spring Validation` 进行参数校验。然而，在实际开发过程中，我们遇到了以下痛点：

### 1. 实体类污染严重

```java
// 使用 Spring Validation 的实体类 - 充斥着大量校验注解
public class UserDTO {

    @NotNull(message = "ID不能为空", groups = {Update.class})
    @Null(groups = {Create.class})
    private Long id;

    @NotBlank(message = "用户名不能为空", groups = {Create.class, Update.class})
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间", groups = {Create.class, Update.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母数字下划线", groups = {Create.class, Update.class})
    private String username;

    @NotBlank(message = "手机号不能为空", groups = {Create.class})
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = {Create.class, Update.class})
    private String phone;

    @NotBlank(message = "邮箱不能为空", groups = {Create.class})
    @Email(message = "邮箱格式不正确", groups = {Create.class, Update.class})
    private String email;

    // ... 更多字段和注解

    public interface Create {}
    public interface Update {}
}
```

### 2. 分组校验复杂难维护

当业务场景增多时，分组接口会越来越多，实体类变得臃肿难以阅读。

### 3. 多字段联合校验困难

```java
// Spring Validation 需要自定义注解 + SpEL 表达式，学习成本高
@ScriptAssert(lang = "javascript",
    script = "_this.startDate == null || _this.endDate == null || _this.startDate.before(_this.endDate)",
    message = "开始日期必须早于结束日期")
public class DateRangeDTO {
    private Date startDate;
    private Date endDate;
}
```

### 4. 复杂业务逻辑校验难以实现

涉及数据库查询、远程调用等业务校验时，Spring Validation 力不从心。

## 核心优势

| 特性 | Custom Validation | Spring Validation |
|------|----------------------|-------------------|
| 实体类污染 | ❌ 无污染 | ✅ 大量注解污染 |
| 分组校验 | ✅ 无需分组，按接口独立配置 | ⚠️ 分组接口泛滥 |
| 多字段联合校验 | ✅ 简单易用 | ⚠️ 需要 SpEL，学习成本高 |
| 复杂业务校验 | ✅ 直接写 Java 代码 | ⚠️ 需要自定义注解 |
| IDE 支持 | ✅ 完整的代码提示和断点调试 | ⚠️ SpEL 无提示 |
| 学习成本 | ✅ 低，像写普通代码一样 | ⚠️ 中高 |
| 可维护性 | ✅ 校验逻辑集中在 Controller | ⚠️ 分散在实体类 |

##  版本兼容性

| Starter 模块                      | Spring Boot 版本 | JDK 版本 | Servlet API |
| --------------------------------- | ---------------- | -------- | ----------- |
| `validation-spring-boot-starter`  | 1.x / 2.x        | 8+       | javax       |
| `validation-spring-boot3-starter` | 3.x              | 17+      | jakarta     |

## 快速开始

### 1. 添加依赖

根据您的 Spring Boot 版本选择合适的 Starter：

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
implementation 'io.github.vennarshulytz:validation-spring-boot-starter:1.0.0'
```

### 2. 启用功能

在启动类上添加 `@EnableValidation` 注解：

```java
@SpringBootApplication
@EnableValidation
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 开始使用

在需要进行参数校验的方法所在的类上，标记 `@ValidatedExt` 注解。

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
                                                        message = "不能为空"))
            })
    }) User user) {
        return Result.success();
    }
}
```

**实体类保持干净：**

```java
// 无任何校验注解！
@Data
public class User {
    private Long id;
    private String username;
    private String phone;
    private String email;
}
```

## 基础使用

### 核心注解说明

#### @ValidationRules

标记在 Controller 方法参数上，包含多个校验规则。

```java
@ValidationRules({
    @ValidationRule(...),
    @ValidationRule(...),
    ...
})
```

#### @ValidationRule

定义单个校验规则：

| 属性 | 类型 | 说明 |
|------|------|------|
| type | Class<?> | 目标校验类型 |
| path | String | 字段路径，支持嵌套如 `"manager.address"` |
| validators | ValidateWith[] | 字段校验器配置列表 |
| custom | Class<? extends CustomValidator> | 自定义校验器类 |

#### @ValidateWith

配置字段校验器：

| 属性 | 类型 | 说明 |
|------|------|------|
| validator | Class<? extends FieldValidator> | 校验器类 |
| fields | FieldConfig[] | 字段配置列表 |
| message | String | 默认错误消息 |
| params | String[] | 默认校验参数 |

#### @FieldConfig

精细化配置单个字段：

| 属性 | 类型 | 说明 |
|------|------|------|
| names | String[] | 字段名列表 |
| message | String | 错误消息 |
| params | String[] | 校验参数 |

### 基础示例

#### 示例 1：简单字段校验

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
                    // 多个字段使用相同校验器和消息
                    @ValidateWith(validator = NotBlankValidator.class,
                                  fields = @FieldConfig(names = {"orderNo", "productName"},
                                                        message = "不能为空")),
                    // 单个字段独立配置
                    @ValidateWith(validator = NotNullValidator.class,
                                  fields = @FieldConfig(names = "quantity",
                                                        message = "数量不能为空")),
                    @ValidateWith(validator = NotNullValidator.class,
                                  fields = @FieldConfig(names = "price",
                                                        message = "价格不能为空"))
            })
    }) CreateOrderRequest request) {
        return Result.success();
    }
}
```

#### 示例 2：不同字段不同错误消息

```java
@PostMapping("/register")
public Result register(@RequestBody @ValidationRules({
        @ValidationRule(type = RegisterRequest.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class, fields = {
                        @FieldConfig(names = "username", message = "用户名不能为空"),
                        @FieldConfig(names = "password", message = "密码不能为空"),
                        @FieldConfig(names = "confirmPassword", message = "确认密码不能为空")
                }),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username",
                                                    message = "用户名长度必须在2-20之间",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone",
                                                    message = "手机号格式不正确",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) RegisterRequest request) {
    return Result.success();
}
```

#### 示例 3：使用便捷注解校验简单参数

```java
@GetMapping("/detail")
public Result getDetail(@RequestParam @NotNullCheck(message = "ID不能为空") Long id,
                        @RequestParam @NotBlankCheck(message = "token不能为空") String token) {
    return Result.success();
}
```

### 使用前后对比

#### 使用前（Spring Validation）

```java
// ========== 实体类被严重污染 ==========
@Data
public class UserDTO {
    @NotNull(message = "ID不能为空", groups = Update.class)
    @Null(groups = Create.class)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Email(message = "邮箱格式错误")
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

#### 使用后（Custom Validation）

```java
// ========== 实体类保持干净 ==========
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String email;
}

// ========== Controller - 校验逻辑内聚 ==========
@PostMapping("/create")
public Result create(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NullValidator.class,
                              fields = @FieldConfig(names = "id", message = "创建时ID必须为空")),
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"username", "phone"}, message = "不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) UserDTO user) {
    return Result.success();
}

@PostMapping("/update")
public Result update(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "更新时ID不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) UserDTO user) {
    return Result.success();
}
```

## 进阶使用

### 嵌套对象校验（path 属性）

当实体类包含嵌套对象时，使用 `path` 属性精确定位校验目标。

#### 实体类结构

```java
@Data
public class Department {
    private String name;
    private Employee manager;           // 单个嵌套对象
    private List<Employee> employees;   // 嵌套集合
}

@Data
public class Employee {
    private String id;
    private String name;
    private String phone;
    private Address address;            // 深层嵌套
}

@Data
public class Address {
    private String province;
    private String city;
    private String detail;
}
```

#### 使用 path 精确校验

```java
@PostMapping("/department/save")
public Result save(@RequestBody @ValidationRules({
        // 校验 Department 直属字段
        @ValidationRule(type = Department.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "name", message = "部门名称不能为空"))
        }),

        // 校验 manager 对象（path = "manager"）
        @ValidationRule(type = Employee.class, path = "manager", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"name", "phone"},
                                                    message = "管理员信息不能为空"))
        }),

        // 校验 employees 列表中的所有对象（path = "employees"）
        @ValidationRule(type = Employee.class, path = "employees", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "id", message = "员工ID不能为空"))
        }),

        // 深层嵌套：校验 manager 下的 address
        @ValidationRule(type = Address.class, path = "manager.address", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"province", "city"},
                                                    message = "地址信息不完整"))
        }),

        // 深层嵌套：校验 employees 列表中每个员工的 address
        @ValidationRule(type = Address.class, path = "employees.address", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "detail", message = "详细地址不能为空"))
        })
}) Department department) {
    return Result.success();
}
```

### path 匹配规则详解

当同一类型存在多个 `@ValidationRule` 时，`path` 的匹配规则如下：

```java
@ValidationRules({
        // 规则1：path = "manager"，只匹配 manager 字段
        @ValidationRule(type = Employee.class, path = "manager", validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "phone", message = "管理员手机必填"))
        }),

        // 规则2：path = "employees"，只匹配 employees 列表
        @ValidationRule(type = Employee.class, path = "employees", validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "员工ID必填"))
        }),

        // 规则3：path = ""（默认），匹配【排除以上路径后】的所有 Employee 实例
        @ValidationRule(type = Employee.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = "name", message = "姓名不能为空"))
        })
})
```

> **匹配优先级**：具体路径 > 默认路径（空字符串）。当 `path` 为空时，会排除其他同类型规则已匹配的对象。

### 自定义校验器（复杂业务逻辑）

对于多字段联合校验、涉及数据库查询等复杂场景，实现 `CustomValidator` 接口。

#### 定义自定义校验器

```java
@Component
public class OrderCustomValidator implements CustomValidator<CreateOrderRequest> {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public void validate(CreateOrderRequest request, ValidationResult result) {

        // 多字段联合校验：价格和数量的乘积校验
        if (request.getPrice() != null && request.getQuantity() != null) {
            BigDecimal total = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            if (total.compareTo(new BigDecimal("100000")) > 0) {
                result.addError("price,quantity", "订单金额不能超过10万元", total);
            }
        }

        // 业务逻辑校验：商品是否存在
        if (StringUtils.hasText(request.getProductNo())) {
            Product product = productService.findByNo(request.getProductNo());
            if (product == null) {
                result.addError("productNo", "商品不存在", request.getProductNo());
                return; // 快速失败
            }

            // 库存校验
            int stock = inventoryService.getStock(request.getProductNo());
            if (request.getQuantity() != null && request.getQuantity() > stock) {
                result.addError("quantity",
                               String.format("库存不足，当前库存: %d", stock),
                               request.getQuantity());
            }
        }

        // 日期范围校验
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().after(request.getEndDate())) {
                result.addError("startDate,endDate", "开始日期不能晚于结束日期", null);
            }
        }
    }
}
```

#### 在 Controller 中使用

```java
@PostMapping("/order/create")
public Result createOrder(@RequestBody @ValidationRules({
        // 基础字段校验
        @ValidationRule(type = CreateOrderRequest.class, validators = {
                @ValidateWith(validator = NotBlankValidator.class,
                              fields = @FieldConfig(names = {"orderNo", "productNo"}, message = "不能为空")),
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = {"quantity", "price"}, message = "不能为空"))
        }),
        // 自定义复杂校验
        @ValidationRule(custom = OrderCustomValidator.class)
}) CreateOrderRequest request) {
    return orderService.create(request);
}
```

### 自定义校验器对比 Spring Validation

#### Spring Validation 方式

```java
// 1. 定义注解
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "日期范围无效";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startField();
    String endField();
}

// 2. 实现校验器
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
        // 使用反射获取字段值...
        // 代码冗长，且难以注入 Spring Bean
    }
}

// 3. 在实体类上标注
@ValidDateRange(startField = "startDate", endField = "endDate")
public class OrderDTO { ... }
```

#### Custom Validation 方式

```java
// 直接实现接口，像写普通 Service 一样简单
@Component
public class OrderCustomValidator implements CustomValidator<OrderDTO> {

    @Autowired
    private SomeService someService; // 可以注入任何 Spring Bean

    @Override
    public void validate(OrderDTO order, ValidationResult result) {
        if (order.getStartDate().after(order.getEndDate())) {
            result.addError("startDate,endDate", "开始日期不能晚于结束日期", null);
        }
    }
}
```

### 手动处理校验结果

默认情况下，校验失败会自动抛出 `ValidationException`。但在某些业务场景中，你可能希望自行决定如何处理校验错误（例如返回自定义格式的错误响应、部分字段失败仍允许继续等）。

只需在方法签名中声明一个 `ValidationResult` 类型的参数，框架将：

1. **跳过**自动抛异常的步骤
2. 将收集到的校验结果**注入**到该参数中

> 该设计理念与 Spring MVC 中 `BindingResult` 紧跟 `@Valid` 参数的惯用法一致。

#### 使用方式

```java
@PostMapping("/users")
public ResponseEntity<?> createUser(@RequestBody @ValidationRules(...) UserDTO dto, ValidationResult validationResult) {
    // 校验结果已自动注入，不会抛出 ValidationException
    if (validationResult.hasErrors()) {
        // 自行决定如何响应
        return ResponseEntity.badRequest().body(validationResult.getErrors()); 
    }
    // 校验通过，执行正常业务逻辑
    return ResponseEntity.ok(userService.save(dto));
}
```

#### 对比

| 方式             | 方法签名                                                     | 校验失败行为               |
| ---------------- | ------------------------------------------------------------ | -------------------------- |
| 自动模式（默认） | `createUser(@ValidationRules UserDTO dto)`                   | 抛出 `ValidationException` |
| 手动模式         | `createUser(@ValidationRules UserDTO dto, ValidationResult result)` | 注入结果，由业务方处理     |

#### 注意事项

- 每个方法签名中最多声明 **一个** `ValidationResult` 参数，框架只会识别第一个。
- `ValidationResult` 参数的位置不限，但推荐放在被校验参数之后，保持可读性。
- 即使校验无错误，`ValidationResult` 也会被注入（此时 `hasErrors()` 返回 `false`）。

### 校验规则模板（Validation Rule Template）

#### 功能介绍

在实际业务开发中，相同的校验规则往往需要在多个接口中重复使用。若直接复制粘贴 `@ValidationRules` 注解，会导致代码冗余、可读性降低以及维护成本上升——一旦规则需要变更，就必须逐一修改所有使用该规则的地方。

为解决上述问题，框架提供了**校验规则模板**功能，允许将一组校验规则封装为可复用的模板，在需要的地方直接引用，从而实现"定义一次，处处复用"。

------

#### 背景示例：未使用模板时的问题

假设有以下接口，它使用了一组较为复杂的校验规则：

```java
@PostMapping("/update")
public Result update(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "更新时ID不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) UserDTO user) {
    return Result.success();
}
```

若另一个接口（如 `/admin/update`）需要完全相同的校验逻辑，只能再次复制上述注解，造成明显的代码冗余。

------

#### 方案一：自定义模板注解

**核心思路：** 创建一个自定义注解，并在该注解上标注 `@ValidationRules`，将校验规则"内嵌"进自定义注解中。使用时，直接将自定义注解标注在方法参数上即可。

##### 第一步：定义模板注解

```java
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "更新时ID不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
})
public @interface UserUpdateValidation {
    // 自定义模板注解，无需添加任何方法
}
```

##### 第二步：在接口中引用模板注解

```java
// 接口一
@PostMapping("/update")
public Result update(@RequestBody @UserUpdateValidation UserDTO user) {
    return Result.success();
}

// 接口二（复用相同校验规则，无需重复编写）
@PostMapping("/admin/update")
public Result adminUpdate(@RequestBody @UserUpdateValidation UserDTO user) {
    return Result.success();
}
```

##### 方案一优缺点

| 项目   | 说明                                           |
| ------ | ---------------------------------------------- |
| ✅ 优点 | 使用简洁，标注方式与原生注解一致，IDE 支持友好 |
| ✅ 优点 | 可为模板注解赋予语义化的名称，提升可读性       |
| ⚠️ 注意 | 需要为每个模板单独创建一个注解类文件           |

------

#### 方案二：自定义模板类

**核心思路：** 创建一个实现了 `ValidationRuleTemplate` 接口的类（或接口），并在其上标注 `@ValidationRules`。使用时，通过 `@ValidationRules(template = TemplateClass.class)` 的方式引用该模板。

##### 第一步：定义模板类

```java
@ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "更新时ID不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
})
public interface UserUpdateValidation extends ValidationRuleTemplate {
    // 自定义模板接口，无需添加任何方法
}
```

> **提示：** 模板也可以定义为 `class`，使用 `interface` 更为简洁，推荐优先使用 `interface`。

##### 第二步：在接口中引用模板类

```java
// 接口一
@PostMapping("/update")
public Result update(@RequestBody @ValidationRules(template = UserUpdateValidation.class) UserDTO user) {
    return Result.success();
}

// 接口二（复用相同校验规则，无需重复编写）
@PostMapping("/admin/update")
public Result adminUpdate(@RequestBody @ValidationRules(template = UserUpdateValidation.class) UserDTO user) {
    return Result.success();
}
```

##### 方案二优缺点

| 项目   | 说明                                                         |
| ------ | ------------------------------------------------------------ |
| ✅ 优点 | 模板以普通类/接口的形式存在，便于统一管理和组织（如集中放置在 `validation/template` 包下） |
| ✅ 优点 | 无需创建额外的注解类型，降低元注解的使用复杂度               |
| ⚠️ 注意 | 引用时需要通过 `template` 属性指定，略比方案一冗长           |

------

#### 三种方式功能等价说明

以下三种写法在**功能上完全等价**，可根据项目风格和团队偏好自由选择：

```java
// 写法一：直接内联校验规则（不使用模板）
@PostMapping("/update")
public Result update(@RequestBody @ValidationRules({
        @ValidationRule(type = UserDTO.class, validators = {
                @ValidateWith(validator = NotNullValidator.class,
                              fields = @FieldConfig(names = "id", message = "更新时ID不能为空")),
                @ValidateWith(validator = SizeValidator.class,
                              fields = @FieldConfig(names = "username", message = "用户名长度2-20",
                                                    params = {"min=2", "max=20"})),
                @ValidateWith(validator = PatternValidator.class,
                              fields = @FieldConfig(names = "phone", message = "手机号格式错误",
                                                    params = {"regexp=^1[3-9]\\d{9}$"}))
        })
}) UserDTO user) {
    return Result.success();
}

// 写法二：使用自定义模板注解（方案一）
@PostMapping("/update")
public Result update(@RequestBody @UserUpdateValidation UserDTO user) {
    return Result.success();
}

// 写法三：使用自定义模板类（方案二）
@PostMapping("/update")
public Result update(@RequestBody @ValidationRules(template = UserUpdateValidation.class) UserDTO user) {
    return Result.success();
}
```

------

#### 方案选择建议

| 场景                                               | 推荐方案                 |
| -------------------------------------------------- | ------------------------ |
| 校验规则与特定业务语义强绑定，希望注解具有自描述性 | 方案一（自定义模板注解） |
| 项目中模板数量较多，希望统一集中管理               | 方案二（自定义模板类）   |
| 校验规则仅在少数地方使用，无需复用                 | 直接内联（不使用模板）   |

## 内置校验器

| 校验器 | 说明 | 参数 |
|--------|------|------|
| AssertFalseValidator | 必须为 false 校验 | - |
| AssertTrueValidator | 必须为 true 校验 | - |
| DecimalMaxValidator | 小数最大值校验 | value, inclusive |
| DecimalMinValidator | 小数最小值校验 | value, inclusive |
| DigitsValidator | 数字位数校验 | integer, fraction |
| EmailValidator | 邮箱格式校验 | - |
| FutureValidator | 必须是将来时间校验 | - |
| FutureOrPresentValidator | 必须是当前或将来时间校验 | - |
| MaxValidator | 最大值校验 | value |
| MinValidator | 最小值校验 | value |
| NegativeValidator | 负数校验 | - |
| NegativeOrZeroValidator | 负数或零校验 | - |
| NotBlankValidator | 非空白字符串校验 | - |
| NotEmptyValidator | 非空校验（字符串、集合、Map、数组） | - |
| NotNullValidator | 非空校验 | - |
| NullValidator | 必须为 null 校验 | - |
| PastValidator | 必须是过去时间校验 | - |
| PastOrPresentValidator | 必须是当前或过去时间校验 | - |
| PatternValidator | 正则表达式校验 | regexp |
| PositiveValidator | 正数校验 | - |
| PositiveOrZeroValidator | 正数或零校验 | - |
| SizeValidator | 大小/长度校验 | min, max |

### 使用示例

```java
@ValidateWith(validator = SizeValidator.class,
              fields = @FieldConfig(names = "username",
                                    message = "用户名长度必须在{min}-{max}之间",
                                    params = {"min=2", "max=20"}))

@ValidateWith(validator = PatternValidator.class,
              fields = @FieldConfig(names = "phone",
                                    message = "手机号格式不正确",
                                    params = {"regexp=^1[3-9]\\d{9}$"}))

```

### 内置校验注解

| 校验注解              | 说明                                | 参数              |
| --------------------- | ----------------------------------- | ----------------- |
| @AssertFalseCheck     | 必须为 false                        | -                 |
| @AssertTrueCheck      | 必须为 true                         | -                 |
| @DecimalMaxCheck      | 小数最大值校验                      | value, inclusive  |
| @DecimalMinCheck      | 小数最小值校验                      | value, inclusive  |
| @DigitsCheck          | 数字位数校验                        | integer, fraction |
| @EmailCheck           | 邮箱格式校验                        | -                 |
| @FutureCheck          | 必须是将来时间                      | -                 |
| @FutureOrPresentCheck | 必须是当前或将来时间                | -                 |
| @MaxCheck             | 最大值校验                          | value             |
| @MinCheck             | 最小值校验                          | value             |
| @NegativeCheck        | 负数校验                            | -                 |
| @NegativeOrZeroCheck  | 负数或零校验                        | -                 |
| @NotBlankCheck        | 非空白字符串校验                    | -                 |
| @NotEmptyCheck        | 非空校验（字符串、集合、Map、数组） | -                 |
| @NotNullCheck         | 非空校验                            | -                 |
| @NullCheck            | 必须为 null 校验                    | -                 |
| @PastCheck            | 必须是过去时间                      | -                 |
| @PastOrPresentCheck   | 必须是当前或过去时间                | -                 |
| @PatternCheck         | 正则表达式校验                      | regexp            |
| @PositiveCheck        | 正数校验                            | -                 |
| @PositiveOrZeroCheck  | 正数或零校验                        | -                 |
| @SizeCheck            | 大小/长度校验                       | min, max          |

### 使用示例

```java
@GetMapping("/detail")
public Result getDetail(@RequestParam @NotNullCheck(message = "ID不能为空") Long id,
                        @RequestParam @NotBlankCheck(message = "token不能为空") String token) {
    return Result.success();
}
```

## 自定义校验器

### 实现自定义字段校验器

```java
@Component
public class IdCardValidator implements FieldValidator {

    @Override
    public boolean validate(Object value, Map<String, String> params) {
        if (value == null) {
            return true; // null 值交给 NotNull 校验器处理
        }
        String idCard = value.toString();
        // 实现身份证号校验逻辑
        return isValidIdCard(idCard);
    }

    @Override
    public String getDefaultMessage() {
        return "身份证号格式不正确";
    }

    private boolean isValidIdCard(String idCard) {
        // 具体校验逻辑
        return idCard.matches("^\\d{17}[\\dXx]$");
    }
}
```

### 使用自定义字段校验器

```java
@ValidateWith(validator = IdCardValidator.class,
              fields = @FieldConfig(names = "idCard", message = "请输入正确的身份证号码"))
```

## BizAssert - 业务断言工具库

一个轻量级、生产就绪的 Java 业务断言工具库。

该工具灵感来源于 Spring 的 `org.springframework.util.Assert`，但专门针对**业务逻辑校验**场景进行了优化设计。

与传统断言工具不同，**BizAssert** 在断言失败时不会抛出通用的 `IllegalArgumentException`，而是抛出**携带业务错误码的自定义业务异常**，从而使错误处理更加清晰、可控。

非常适用于企业级应用中的 Service 层，用于进行参数校验与业务状态校验。

🔗 项目地址：[vennarshulytz/biz-assert: BizAssert is a lightweight and enterprise-grade assertion library for Java, focused on business logic validation. Unlike standard assertion utilities, it throws customizable business exceptions with error codes, enabling consistent error handling and clearer service-layer validation across complex applications.](https://github.com/vennarshulytz/biz-assert)

## 配置说明

### @EnableValidation 注解

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| mode | ValidationMode | true | 是否启用快速失败模式 |
| enableI18n | boolean | false | 是否启用国际化消息 |

```java
// 快速失败模式（默认）：遇到第一个错误立即返回
@EnableValidation

// 全量校验模式：收集所有错误后返回
@EnableValidation(ValidationMode.FAIL_ALL)

// 启用国际化消息支持
@EnableValidation(enableI18n = true)
```

### 国际化配置

启用国际化后，在 `messages.properties` 中配置：

```properties
# messages.properties
validation.user.username.notblank=用户名不能为空
validation.user.phone.pattern=手机号格式不正确

# messages_en.properties
validation.user.username.notblank=Username cannot be blank
validation.user.phone.pattern=Invalid phone number format
```

使用方式：

```java
@FieldConfig(names = "username", message = "validation.user.username.notblank")
```

### 错误响应格式

```json
{
    "code": 400,
    "message": "参数校验失败",
    "errors": [
        {
            "field": "username",
            "message": "用户名不能为空",
            "rejectedValue": null
        },
        {
            "field": "phone",
            "message": "手机号格式不正确",
            "rejectedValue": "123456"
        }
    ]
}
```

## 与 Spring Validation 对比

### 功能对比

| 场景 | Custom Validation | Spring Validation |
|------|----------------------|-------------------|
| 简单非空校验 | ✅ 简单 | ✅ 简单 |
| 分组校验 | ✅ 无需分组 | ⚠️ 需要定义分组接口 |
| 嵌套对象校验 | ✅ path 属性定位 | ✅ @Valid 级联 |
| 多字段联合校验 | ✅ CustomValidator | ⚠️ 需要 SpEL 或自定义注解 |
| 业务逻辑校验 | ✅ 可注入 Spring Bean | ❌ 难以注入 |
| 实体类干净度 | ✅ 无污染 | ❌ 大量注解 |
| 调试便利性 | ✅ 正常断点调试 | ⚠️ SpEL 难调试 |
| 编码提示 | ✅ 完整 IDE 支持 | ⚠️ SpEL 无提示 |

### 代码量对比

**创建 + 更新两个接口的实体校验：**

| 方案 | 实体类代码量 | Controller 代码量 | 额外类 |
|------|-------------|-------------------|--------|
| Spring Validation | ~50 行注解 | ~10 行 | 2 个分组接口 |
| Custom Validation | 0 | ~40 行 | 0 |

虽然 Controller 代码量略增，但：

1. 实体类完全干净，可复用于多层
2. 无需维护分组接口
3. 校验逻辑集中，易于维护
4. 不同接口的校验规则独立，互不影响

## 模块结构

```
validation-parent/
├── validation-core                  # 核心模块
├── validation-spring-boot-starter   # Spring Boot 1.x / Spring Boot 2.x 支持 （JDK 8+）
└── validation-spring-boot3-starter  # Spring Boot 3.x 支持 （JDK 17+）
```

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源。

---