# 多大表分页查询优化过程
### 主导优化方向：
> 1 主键，索引，减少中间过程

> 2 查询条件写在where处和写在join on处有区别吗？
 *答*：没有区别，测试用例如: **PerformanceCndOnJoinOrWhereTest** 
 
> 3 适当时主键作为筛选和排序的条件

### 方案一.VTable 通过减少连接表次数以减少笛卡尔积中间结果
> 1.根据查询条件，分页，排序等，动态生成连接sql语句
> 2.通过主键查询，查一页内，补全未查询的字段。
> 3.提醒用户建立索引	
>TODO 单条关联查询 与 少数据，多条sql查询，哪一个根据优势，优势在何处？

### 方案二.TRE (Thinking->Result->Experience)
>所有的依赖关系仅以主键生成
>1.不使用虚表，提供接口，/thinking -> 结论 -> experience {id,[param],sql,resultType}

