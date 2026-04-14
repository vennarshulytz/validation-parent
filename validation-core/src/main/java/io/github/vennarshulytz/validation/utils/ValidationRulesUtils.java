package io.github.vennarshulytz.validation.utils;

import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.template.ValidationRuleTemplate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ValidationRules 注解解析工具类
 *
 * @author vennarshulytz
 * @since 1.3.1
 */
public class ValidationRulesUtils {

    /**
     * 解析 ValidationRules 注解，收集所有 ValidationRule 数组
     *
     * @param validationRules 目标 ValidationRules 注解
     * @return 收集到的 ValidationRule 数组（顶级优先）
     */
    public static ValidationRule[] resolve(ValidationRules validationRules) {
        if (validationRules == null) {
            return new ValidationRule[0];
        }

        List<ValidationRule[]> collectedRules = new ArrayList<>();
        // 用于检测死循环：记录已处理过的 template Class（排除默认值）
        Set<Class<? extends ValidationRuleTemplate>> visitedTemplates = new LinkedHashSet<>();

        doResolve(validationRules, collectedRules, visitedTemplates);

        // 按顺序合并所有 ValidationRule 数组（顶级在前）
        return mergeRules(collectedRules);
    }

    /**
     * 递归解析 ValidationRules 注解
     *
     * @param validationRules  当前处理的 ValidationRules 注解
     * @param collectedRules   按顺序收集的 ValidationRule 数组列表
     * @param visitedTemplates 已访问的 template Class 集合，用于死循环检测
     */
    private static void doResolve(
            ValidationRules validationRules,
            List<ValidationRule[]> collectedRules,
            Set<Class<? extends ValidationRuleTemplate>> visitedTemplates) {

        // 先收集当前 ValidationRules 的 value（保证顶级在前）
        ValidationRule[] currentValue = validationRules.value();
        collectedRules.add(currentValue);

        Class<? extends ValidationRuleTemplate> templateClass = validationRules.template();

        // 步骤1：template 是默认值，直接终止递归
        if (ValidationRuleTemplate.class.equals(templateClass)) {
            return;
        }

        // 死循环检测：template 非默认值且已被访问过，则报错
        if (visitedTemplates.contains(templateClass)) {
            throw new IllegalStateException(
                    String.format(
                            "检测到 ValidationRules 注解的 template 属性存在循环引用，template class: [%s]，" +
                            "已访问的 template 链路: %s",
                            templateClass.getName(),
                            buildVisitedChain(visitedTemplates, templateClass)
                    )
            );
        }

        // 记录当前 template Class
        visitedTemplates.add(templateClass);

        // 检索 template Class 上是否标记了 ValidationRules 注解
        ValidationRules templateAnnotation = templateClass.getAnnotation(ValidationRules.class);

        // template Class 上没有标记 ValidationRules，终止递归
        if (templateAnnotation == null) {


            ValidationRule validationRule = templateClass.getAnnotation(ValidationRule.class);
            if (validationRule == null) {
                return;
            }
            templateAnnotation = ValidationEngine.createValidationRules(validationRule);
        }

        // 递归处理 template Class 上的 ValidationRules
        doResolve(templateAnnotation, collectedRules, visitedTemplates);
    }

    /**
     * 合并多个 ValidationRule 数组为一个数组
     *
     * @param collectedRules 按顺序收集的 ValidationRule 数组列表
     * @return 合并后的 ValidationRule 数组
     */
    private static ValidationRule[] mergeRules(List<ValidationRule[]> collectedRules) {
        int totalLength = collectedRules.stream()
                .mapToInt(rules -> rules.length)
                .sum();

        ValidationRule[] result = new ValidationRule[totalLength];
        int index = 0;
        for (ValidationRule[] rules : collectedRules) {
            System.arraycopy(rules, 0, result, index, rules.length);
            index += rules.length;
        }
        return result;
    }

    /**
     * 构建已访问 template 链路描述，用于错误提示
     *
     * @param visitedTemplates 已访问的 template Class 集合
     * @param currentTemplate  当前引发死循环的 template Class
     * @return 链路描述字符串
     */
    private static String buildVisitedChain(
            Set<Class<? extends ValidationRuleTemplate>> visitedTemplates,
            Class<? extends ValidationRuleTemplate> currentTemplate) {

        StringBuilder chain = new StringBuilder();
        for (Class<? extends ValidationRuleTemplate> clazz : visitedTemplates) {
            chain.append(clazz.getName()).append(" -> ");
        }
        chain.append(currentTemplate.getName()).append("（循环）");
        return chain.toString();
    }
}