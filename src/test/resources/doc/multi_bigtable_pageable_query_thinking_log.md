# 多大表分页查询优化过程
### 主导优化方向：
> 1 主键，索引，减少中间过程

> 2 查询条件写在where处和写在join on处有区别吗？
 *答*：没有区别，测试用例如: **PerformanceCndOnJoinOrWhereTest** 
 
> 3 适当时主键作为筛选和排序的条件

> 4 聚合函数->纵向表拉成横向

> 5 左连接，右链接，全连接的存在意义
  * 左连接，以左面表为主，如果右边的表不存在则补NULL
  * 右连接，以右面表为主，如果左边的表不存在则补NULL
  * 全连接，左右两侧都不为NULL值时有结果
  * 左右链接的互换，所有的有链接最右边的数据作为主表，其他表右连接之
  * where子句产生筛选是全连接
  * 多表查询的时候，减少中间过程可以提高查询效率，核心就是合理使用左右链接。
  * **所有的连接都可以写成仅仅左连接的方式，并且，把数据规模小的表，放在连接的左侧是最优的连接**。


### 方案一.VTable 通过减少连接表次数以减少笛卡尔积中间结果
> 1.根据查询条件，分页，排序等，动态生成连接sql语句
> 2.通过主键查询，查一页内，补全未查询的字段。
> 3.提醒用户建立索引	
>TODO 单条关联查询 与 少数据，多条sql查询，哪一个根据优势，优势在何处？

### 方案二.TRE (Thinking->Result->Experience)
>所有的依赖关系仅以主键生成
>1.不使用虚表，提供接口，/thinking -> 结论 -> experience {id,[param],sql,resultType}

####关系链表达式  <推到关系链> >> <结果集>
> <推导关系> >> <结果集>
> {
	{A.bid1->B.id} >> {[A:{name alias,...}],},
  }
 * case 正向 : 
 {GoodsOrder.buyerId->User} >> {GoodsOrder{name goodsName,id goodsOrderId},User{name buyerName,receiverAvatar}}
 {GoodsOrder.receiverId->User} >> {GoodsOrder{name goodsName,id goodsOrderId},User{name receiverName,receiverAvatar}}
 *

 
 