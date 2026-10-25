package io.github.lkfs.developer.mongo;

import io.github.lkfs.developer.mongo.function.FieldUtil;
import io.github.lkfs.developer.mongo.function.MongoOperator;
import io.github.lkfs.developer.mongo.function.SFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaObject;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author : Luis
 * @date : 2021/5/10 11:18
 */
@Slf4j
public class BaseMongoQuery {

    /**
     * qo中排除的字段名
     */
    private List<String> fieldExcludeList;
    /**
     * 字段与操作符使用该值进行分隔
     */
    private String opSeparator = "_";

    private List<Matcher> whereList = new ArrayList<>();
    private Map<String, Sort.Direction> orderMap = new HashMap<>();

    public BaseMongoQuery() {
        this(null);
    }


    public BaseMongoQuery(Object qo) {
        this(qo, new String[0]);
    }

    public BaseMongoQuery(Object qo, String... fieldExclude) {
        this.fieldExcludeList = Arrays.asList(fieldExclude);
        if (qo != null) {
            //处理qo中的条件
            ReflectionUtils.doWithLocalFields(qo.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    if (!fieldExcludeList.contains(field.getName())) {
                        try {
                            Object val = FieldUtils.readDeclaredField(qo, field.getName(), true);
                            if (!ObjectUtils.isEmpty(val)) {
                                append(field.getName(), val);
                            }
                        } catch (IllegalAccessException e) {
                            log.error("反射提取字段值异常", e);
                        }
                    }
                }
            });
        }
    }

    public List<Criteria> getCriteriaList() {
        Map<String, Criteria> criteriaMap = new HashMap<>();

        whereList.stream()
                .forEach(matcher -> {
                    Object value = matcher.getValue();
                    if (value instanceof Criteria) {
                        criteriaMap.put(matcher.getKey(), (Criteria) value);
                    } else {
                        Criteria criteria = criteriaMap.get(matcher.getKey());
                        if (criteria == null) {
                            criteria = Criteria.where(matcher.getKey());
                            criteriaMap.put(matcher.getKey(), criteria);
                        }

                        if (MongoOperator.Is.equals(matcher.getOperator()) || MongoOperator.Eq.equals(matcher.getOperator())) {
                            criteria.is(value);
                        } else if (MongoOperator.Neq.equals(matcher.getOperator())) {
                            criteria.ne(value);
                        } else if (MongoOperator.StartWith.equals(matcher.getOperator())) {
                            criteria.regex("^" + value + ".*");
                        } else if (MongoOperator.EndWith.equals(matcher.getOperator())) {
                            criteria.regex(".*" + value + "$");
                        } else if (MongoOperator.Like.equals(matcher.getOperator())) {
                            criteria.regex(".*" + value + ".*");
                        } else if (MongoOperator.Gt.equals(matcher.getOperator())) {
                            criteria.gt(value);
                        } else if (MongoOperator.Gte.equals(matcher.getOperator())) {
                            criteria.gte(value);
                        } else if (MongoOperator.Lt.equals(matcher.getOperator())) {
                            criteria.lt(value);
                        } else if (MongoOperator.Lte.equals(matcher.getOperator())) {
                            criteria.lte(value);
                        } else if (MongoOperator.Null.equals(matcher.getOperator())) {
                            criteria.type(JsonSchemaObject.Type.nullType());
                        } else if (MongoOperator.NotNull.equals(matcher.getOperator())) {
                            Boolean b = Boolean.valueOf(value.toString());
                            criteria.exists(b);
                        } else if (MongoOperator.Exists.equals(matcher.getOperator())) {
                            Boolean b = Boolean.valueOf(value.toString());
                            criteria.exists(b);
                        } else if (MongoOperator.Between.equals(matcher.getOperator())) {
                            if (value instanceof List) {
                                List list = (List) value;
                                if (list.size() < 2) {
                                    throw new UncategorizedMongoDbException("between参数缺少正确的值", null);
                                }
                                criteria.gte(list.get(0)).lte(list.get(1));
                            } else if (value.getClass().isArray()) {
                                String[] strs = (String[]) value;
                                if (strs.length < 2) {
                                    throw new UncategorizedMongoDbException("between参数缺少正确的值", null);
                                }
                                criteria.gte(strs[0]).lte(strs[1]);
                            } else {
                                throw new UncategorizedMongoDbException("between参数值类型错误", null);
                            }
                        } else if (MongoOperator.In.equals(matcher.getOperator())) {
                            if (value instanceof List) {
                                List list = (List) value;
                                criteria.in(list);
                            } else if (value.getClass().isArray()) {
                                String[] strs = (String[]) value;
                                criteria.in(strs);
                            } else {
                                throw new UncategorizedMongoDbException("in参数值类型错误", null);
                            }
                        } else if (MongoOperator.NotIn.equals(matcher.getOperator())) {
                            if (value instanceof List) {
                                List list = (List) value;
                                criteria.nin(list);
                            } else if (value.getClass().isArray()) {
                                String[] strs = (String[]) value;
                                criteria.nin(strs);
                            } else {
                                throw new UncategorizedMongoDbException("notIn参数值类型错误", null);
                            }
                        } else {
                            //没有匹配任何后缀
                            criteria.is(value);
                        }
                    }
                });
        return new ArrayList<>(criteriaMap.values());
    }

    public Query toQuery() {
        List<Criteria> criteriaList = this.getCriteriaList();

        Query query = new Query();
        criteriaList.stream()
                .forEach(criteria -> {
                    query.addCriteria(criteria);
                });

        orderMap.keySet()
                .stream()
                .forEach(field -> {
                    query.with(Sort.by(orderMap.get(field), field));
                });

        return query;
    }


    /**
     * 该方法可增加额外的查询条件，存储在 whereMap 中
     */
    public BaseMongoQuery append(String key, Object val) {
        return this.append(true, key, val);
    }

    public BaseMongoQuery append(Boolean condition, String key, Object val) {
        if (condition) {
            Matcher matcher;
            String[] keyOperator = key.split(opSeparator);
            if (keyOperator.length == 1) {
                matcher = new Matcher(key, val, MongoOperator.Eq);
            } else if (keyOperator.length == 2) {
                matcher = new Matcher(keyOperator[0], val, MongoOperator.parse(keyOperator[1]));
            } else {
                String operStr = keyOperator[keyOperator.length - 1];
                String keyStr = key.replaceAll(opSeparator + operStr + "$", "");
                matcher = new Matcher(keyStr, val, MongoOperator.parse(operStr));
            }
            this.whereList.add(matcher);
        }
        return this;
    }

    /**
     * 直接增加Criteria条件，存储在 whereMap 中
     */
    public BaseMongoQuery append(Criteria criteria) {
        return this.append(true, criteria);
    }

    public BaseMongoQuery append(Boolean condition, Criteria criteria) {
        if (condition) {
            Matcher matcher = new Matcher(criteria.toString(), criteria, null);
            this.whereList.add(matcher);
        }
        return this;
    }

    public <T> BaseMongoQuery append(SFunction<T, ?> fn, Object val, MongoOperator operator) {
        return this.append(true, fn, val, operator);
    }

    public <T> BaseMongoQuery append(Boolean condition, SFunction<T, ?> fn, Object val, MongoOperator operator) {
        if (condition) {
            String key = FieldUtil.getName(fn);
            Matcher matcher = new Matcher(key, val, operator);
            this.whereList.add(matcher);
        }
        return this;
    }

    /**
     * 排序
     *
     * @param key      数据库键
     * @param sortType 排序规则 Sort.Direction.DESC/ASC 不传则为ASC
     * @return 返回
     */
    public BaseMongoQuery orderBy(String key, Sort.Direction sortType) {
        this.orderMap.put(key, sortType);
        return this;
    }

    @Data
    @AllArgsConstructor
    protected class Matcher {
        private String key;
        private Object value;
        private MongoOperator operator;
    }


}
