# Controller Validation Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vennarshulytz/validation-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.vennarshulytz/validation-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java Version](https://img.shields.io/badge/Java-8%2B-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

一个轻量级的 Spring Boot Starter，将参数校验逻辑内聚在 Controller 层，避免实体类被校验注解污染。

## 📖 目录

- [项目背景](#项目背景)
- [核心优势](#核心优势)
- [快速开始](#快速开始)
- [基础使用](#基础使用)
- [进阶使用](#进阶使用)
- [内置校验器](#内置校验器)
- [自定义校验器](#自定义校验器)
- [配置说明](#配置说明)
- [与 Spring Validation 对比](#与-spring-validation-对比)
- [License](#license)

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

| 特性 | Controller Validation | Spring Validation |
|------|----------------------|-------------------|
| 实体类污染 | ❌ 无污染 | ✅ 大量注解污染 |
| 分组校验 | ✅ 无需分组，按接口独立配置 | ⚠️ 分组接口泛滥 |
| 多字段联合校验 | ✅ 简单易用 | ⚠️ 需要 SpEL，学习成本高 |
| 复杂业务校验 | ✅ 直接写 Java 代码 | ⚠️ 需要自定义注解 |
| IDE 支持 | ✅ 完整的代码提示和断点调试 | ⚠️ SpEL 无提示 |
| 学习成本 | ✅ 低，像写普通代码一样 | ⚠️ 中高 |
| 可维护性 | ✅ 校验逻辑集中在 Controller | ⚠️ 分散在实体类 |

## 快速开始

### 1. 添加依赖

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

```java
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

#### 使用后（Controller Validation）

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

#### Controller Validation 方式

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

## 内置校验器

| 校验器 | 说明 | 参数 |
|--------|------|------|
| NotNullValidator | 非空校验 | - |
| NotBlankValidator | 非空白字符串校验 | - |
| NotEmptyValidator | 非空集合/数组/字符串校验 | - |
| NullValidator | 必须为空校验 | - |
| SizeValidator | 长度/大小校验 | min, max |
| PatternValidator | 正则表达式校验 | regexp |
| RangeValidator | 数值范围校验 | min, max |
| EmailValidator | 邮箱格式校验 | - |
| PhoneValidator | 手机号校验（中国大陆） | - |

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

@ValidateWith(validator = RangeValidator.class,
              fields = @FieldConfig(names = "age",
                                    message = "年龄必须在{min}-{max}岁之间",
                                    params = {"min=0", "max=150"}))
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

## 配置说明

### @EnableValidation 注解

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| failFast | boolean | true | 是否启用快速失败模式 |
| enableI18n | boolean | false | 是否启用国际化消息 |

```java
// 快速失败模式（默认）：遇到第一个错误立即返回
@EnableValidation

// 全量校验模式：收集所有错误后返回
@EnableValidation(failFast = false)

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
@FieldConfig(names = "username", message = "{validation.user.username.notblank}")
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

| 场景 | Controller Validation | Spring Validation |
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
| Controller Validation | 0 | ~40 行 | 0 |

虽然 Controller 代码量略增，但：

1. 实体类完全干净，可复用于多层
2. 无需维护分组接口
3. 校验逻辑集中，易于维护
4. 不同接口的校验规则独立，互不影响

## License

Apache License 2.0