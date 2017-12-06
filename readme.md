#宗旨：消除一切形式化的创新。标准化操作，去除一切不必要的差异！最终实现任何事儿全人类只做一次，所有人共享。
#为变更而设计hi

##a.关联查询（虚表）
* 1.根据查询条件（查询参数，排序条件）生成最少连接 DONE 
* 2.通过关联关系，补全最少连接查询数据。 

##b.生成表结构,根据POJO生成建表sql，并能比较版本变更。

##c.RESTful接口实现。
 	1.统一的表单验证
 	2.同簇数据的产生
 	3.接口调用以及触发动作的执行
 	4.数据的逻辑删除与找回
 	5.同簇表间关联虚表
 	
##d. 额外功能
 	1.接口文档产生（校验，关联数据），通知接口变更。
 	2.表结构的查看，在线生成接口（通过编辑json）
 	3.国际化与常量 DONE
 	4.权限与审计（黑名单，白名单）
 	5.资源保存与删除(去除重复文件，资源被引用次数)
 	5.1.图片处理,缩略图,裁剪,压缩
 	5.2.文本压缩
 	6.执行任务（如修改ID，调整字段名称，数据迁移等耗时比较长，导致服务器停顿的业务)，带数据升级，调整以前数据组织架构功能
 	7.请求通知协议，每个请求除了userId,token外，还有最新通知码 通知你需要更新你的数据
 
 
 